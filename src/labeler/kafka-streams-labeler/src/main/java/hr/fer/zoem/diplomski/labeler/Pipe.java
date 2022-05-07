package hr.fer.zoem.diplomski.labeler;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

public class Pipe {

    public static final Logger logger = Logger.getLogger(Pipe.class.getName());
    public static final String INPUT_TOPIC_KEY = "labeler.input.topic";
    public static final String OUTPUT_TOPIC_KEY = "labeler.output.topic";
    public static final String LABELS_PATH_KEY = "labeler.labels.path";
    // TODO maybe read header from input.topic with offset=0 ?
    // TODO maybe read header from a topic "schema-registry" ?
    // https://kafka.apache.org/26/javadoc/org/apache/kafka/clients/consumer/KafkaConsumer.html#seek-org.apache.kafka.common.TopicPartition-long-
    public static final String HEADER_KEY = "labeler.header";
    public static final String SOURCE_KEY = "src_ip";
    public static final String DESTINATION_KEY = "dst_ip";

    private static final String MISSING_PROPERTY_MSG_TEMPLATE = "A %s is required in the properties file";

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            logger.severe("An argument is expected: path to configuration file");
            // System.err.println("An argument is expected: path to configuration file");
            System.exit(1);
        }

        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get(args[0])));

        String labelsPath = getOrThrow(props, LABELS_PATH_KEY);
        String header = getOrThrow(props, HEADER_KEY);
        String inputTopic = getOrThrow(props, INPUT_TOPIC_KEY);
        String outputTopic = getOrThrow(props, OUTPUT_TOPIC_KEY);

        Map<String, Boolean> labels = loadLabels(labelsPath);
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

        builder.stream(inputTopic)
                .mapValues(v -> {
                    String label = "pimpilinich";
                    if (header.equals(v)) {
                        logger.info("It is the header!");
                        label = "attack";
                    } else {
                        String[] row = v.toString().split(",");
                        logger.info("I have split it successfully!");
                        Boolean isSourceAttacker = labels.get(row[sourceIndex]);
                        logger.info("isSourceAttacker: " + isSourceAttacker);
                        Boolean isDestinationAttacker = labels.get(row[destinationIndex]);
                        logger.info("isDestinationAttacker: " + isDestinationAttacker);
                        // if both addresses are unknown, declare NA
                        if (isSourceAttacker == null && isDestinationAttacker == null) {
                            logger.info("Both ip adresses are null!");
                            label = "NA";
                            // if source or destination is attacker, declare attack
                        } else if (Boolean.TRUE.equals(isSourceAttacker) || Boolean.TRUE.equals(isDestinationAttacker)) {
                            label = "true";
                            logger.info("One of them is the attacker!");
                            // else declare benign
                        } else {
                            logger.info("Neither of them is the attacker!");
                            label = "false";
                        }
                    }
                    logger.info("Is this one an attack? " + label);
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
                .skip(1)
                .map(l -> l.split(","))
                .forEach(l -> labels.put(l[0], Boolean.parseBoolean(l[1])));

        return labels;
    }
}
