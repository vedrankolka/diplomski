package hr.fer.zoem.diplomski.labeler;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Pipe {

    public static final Logger logger = Logger.getLogger(Pipe.class.getName());
    public static final String INPUT_TOPIC_KEY = "labeler.input.topic";
    public static final String OUTPUT_TOPIC_KEY = "labeler.output.topic";
    public static final String LABELS_PATH_KEY = "labeler.labels.path";
    public static final String SAVE_LABELS_KEY = "labeler.save.labels";
    public static final String BLOCK_LABELER_KEY = "labeler.block";
    public static final String LABELS_HEADER_KEY = "labeler.labels.header";
    // TODO maybe read header from input.topic with offset=0 ?
    // TODO maybe read header from a topic "schema-registry" ?
    // https://kafka.apache.org/26/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html#seek-org.apache.kafka.common.TopicPartition-long-
    public static final String HEADER_KEY = "labeler.data.header";
    public static final String SOURCE_KEY = "src_ip";
    public static final String DESTINATION_KEY = "dst_ip";
    public static final String LABELS_CONSUMER_PROPERTIES_KEY = "labels.consumer.properties.path";

    private static final String MISSING_PROPERTY_MSG_TEMPLATE = "A %s is required in the properties file";

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            logger.severe("An argument is expected: path to configuration file");
            System.exit(1);
        }

        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get(args[0])));
        // TODO this can now be null
        String labelsPath = getOrThrow(props, LABELS_PATH_KEY);
        String header = getOrThrow(props, HEADER_KEY);
        String inputTopic = getOrThrow(props, INPUT_TOPIC_KEY);
        String outputTopic = getOrThrow(props, OUTPUT_TOPIC_KEY);

        Map<String, Boolean> labels = null;
        // try to get the labels from kafka
        KafkaLabelsProvider labelsProvider = null;
        String labelsConsumerPropertiesPath = props.getProperty(LABELS_CONSUMER_PROPERTIES_KEY);
        boolean saveLabels = (boolean) props.getOrDefault(SAVE_LABELS_KEY, Boolean.FALSE);

        if (labelsConsumerPropertiesPath != null) {
            try {
                Properties consumerProperties = new Properties();
                consumerProperties.load(Files.newInputStream(Paths.get(labelsConsumerPropertiesPath)));
                labelsProvider = new KafkaLabelsProvider(consumerProperties);
                labels = labelsProvider.poll();
                if (labels != null) {
                    logger.info("Got labels from kafka topic.");
                    // if configured to save read labels, write to file
                    if (saveLabels) {
                        saveLabels(labels, labelsPath);
                        logger.info("Saved labels to file.");
                    }
                } else {
                    logger.info("No labels available on the kafka topic.");
                }


            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not read configuration file of labels consumer.", e);
            }
        }
        // if there are still no labels, try to read from the file
        if (labels == null) {
            try {
                labels = loadLabels(labelsPath);
                logger.info("Labels loaded from file: '" + labelsPath + "'.");
            } catch (IOException e) {
                logger.log(Level.WARNING, "Could not read labels from file.", e);
            }
        }
        // if labels are still null and block is set to true, block until labels on kafka appear
        if (labels == null) {
            boolean blockLabeler = (boolean) props.getOrDefault(BLOCK_LABELER_KEY, Boolean.FALSE);
            if (blockLabeler && labelsProvider != null) {
                labels = labelsProvider.get();
                logger.info("Got labels from kafka topic.");
                if (saveLabels) {
                    saveLabels(labels, labelsPath);
                    logger.info("Saved labels to file.");
                }
            } else {
                logger.severe("Could not initialize labels. Load labels from kafka or file.");
                System.exit(1);
            }
        }

        List<String> columns = Arrays.asList(header.split(","));
        int sourceIndex = columns.indexOf(SOURCE_KEY);
        int destinationIndex = columns.indexOf(DESTINATION_KEY);

        if (sourceIndex < 0) {
            throw new IllegalArgumentException("Header does not have \"" + SOURCE_KEY + "\" column.");
        }
        if (destinationIndex < 0) {
            throw new IllegalArgumentException("Header does not have \"" + DESTINATION_KEY + "\" column.");
        }

        final StreamsBuilder builder = new StreamsBuilder();

        Map<String, Boolean> finalLabels = labels;
        builder.stream(inputTopic)
                .mapValues(v -> {
                    String label;
                    if (header.equals(v)) {
                        label = "attack";
                    } else {
                        String[] row = v.toString().split(",");
                        Boolean isSourceAttacker = finalLabels.get(row[sourceIndex]);
                        Boolean isDestinationAttacker = finalLabels.get(row[destinationIndex]);
                        // if both addresses are unknown, declare NA
                        if (isSourceAttacker == null && isDestinationAttacker == null) {
                            label = "NA";
                            // if source or destination is attacker, declare attack
                        } else if (Boolean.TRUE.equals(isSourceAttacker) || Boolean.TRUE.equals(isDestinationAttacker)) {
                            label = "true";
                            // else declare benign
                        } else {
                            label = "false";
                        }
                    }
                    return v + "," + label;
                })
                .to(outputTopic);

        final Topology topology = builder.build();

        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
                System.out.println("Count down.");
            }
        });

        logger.info("Starting the stream from topic '" + inputTopic + "' to topic '" + outputTopic + "'.");

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.out.println("Exiting peacefully.");
        System.exit(0);
    }

    private static String getOrThrow(Properties props, String key) {
        return Objects.requireNonNull(props.getProperty(key), String.format(MISSING_PROPERTY_MSG_TEMPLATE, key));
    }

    private static Map<String, Boolean> loadLabels(String path) throws IOException {
        Map<String, Boolean> labels = new HashMap<>();
        Files.readAllLines(Paths.get(path)).stream()
                // skip header
                .skip(1)
                .map(l -> l.split(","))
                .forEach(l -> labels.put(l[0], Boolean.parseBoolean(l[1])));

        return labels;
    }

    private static void saveLabels(Map<String, Boolean> labels, String path) throws IOException {
        Writer bufferedWriter = Files.newBufferedWriter(Paths.get(path));
        bufferedWriter.write(LABELS_HEADER_KEY);
        for (Map.Entry<String, Boolean> entry : labels.entrySet()) {
            bufferedWriter.write(entry.getKey() + "," + entry.getValue());
        }
    }
}
