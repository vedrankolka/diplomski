package hr.fer.diplomski.kolka.labeler;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Labeler {

    public static final Logger logger = Logger.getLogger(Labeler.class.getName());
    public static final String INPUT_TOPIC_KEY = "labeler.input.topic";
    public static final String OUTPUT_TOPIC_KEY = "labeler.output.topic";
    public static final String HEADER_KEY = "labeler.header";
    public static final String SOURCE_KEY = "src_ip";
    public static final String DESTINATION_KEY = "dst_ip";

    private static final String MISSING_PROPERTY_MSG_TEMPLATE = "A %s is required in the properties file";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            logger.severe("Two arguments are expected: path to configuration file and path to labels file");
            System.exit(1);
        }

        System.out.println("Properties path: " + args[0]);
        System.out.println("Labels path: " + args[1]);

        Properties props = new Properties();
        props.load(Files.newInputStream(Paths.get(args[0])));

        String labelsPath = args[1];
        String header = getOrThrow(props, HEADER_KEY);
        String inputTopic = getOrThrow(props, INPUT_TOPIC_KEY);
        String outputTopic = getOrThrow(props, OUTPUT_TOPIC_KEY);

        Map<String, Boolean> labels = loadLabels(labelsPath);
        logger.info("Loaded " + labels.size() + " labels.");
        List<String> columns = Arrays.asList(header.split(","));
        int sourceIndex = columns.indexOf(SOURCE_KEY);
        int destinationIndex = columns.indexOf(DESTINATION_KEY);

        if (sourceIndex < 0) {
            throw new IllegalArgumentException("Header does not have \"" + SOURCE_KEY + "\" column.");
        }
        if (destinationIndex < 0) {
            throw new IllegalArgumentException("Header does not have \"" + DESTINATION_KEY + "\" column.");
        }

        createTopics(props, inputTopic, outputTopic);
        logger.info("source topic = " + inputTopic);
        logger.info("destination topic = " + outputTopic);

        final StreamsBuilder builder = new StreamsBuilder();
        builder.stream(inputTopic).mapValues((k, v) -> {
            logger.info("Got message: " + v);
            String label;
            if (header.equals(v.toString())) {
                // if it is the header, expand it by adding column "attack"
                logger.info("It is the header!");
                label = "attack";
            } else {
                String[] row = v.toString().split(",");
                logger.info("row splitted: " + row);
                Boolean isSrcAttacker = labels.get(row[sourceIndex]);
                Boolean isDstAttacker = labels.get(row[destinationIndex]);
                logger.info("isSrcAttacker = " + isSrcAttacker + " isDstAttacker = " + isDstAttacker);
                // if source or destination is attacker, declare attack
                if (Boolean.TRUE.equals(isSrcAttacker) || Boolean.TRUE.equals(isDstAttacker)) {
                    label = "true";
                // if either address is unknown, declare NA
                } else if (isSrcAttacker == null || isDstAttacker == null) {
                    label = "NA";
                // else declare benign
                } else {
                    label = "false";
                }
            }
            return v + "," + label;
        }).to(outputTopic);

        final Topology topology = builder.build();
        try (final KafkaStreams streams = new KafkaStreams(topology, props)) {
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
                logger.info("Started kafka streams.");
                latch.await();
            } catch (Throwable e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
        System.out.println("Goodbye!");
        System.exit(0);
    }

    private static String getOrThrow(Properties props, String key) {
        return Objects.requireNonNull(props.getProperty(key), String.format(MISSING_PROPERTY_MSG_TEMPLATE, key));
    }

    private static Map<String, Boolean> loadLabels(String path) throws IOException {
        Map<String, Boolean> labels = new HashMap<>();

        Files.readAllLines(Paths.get(path)).stream()
                .skip(1)
                .filter(l -> l != null && !l.isBlank())
                .map(l -> l.split(","))
                .forEach(l -> labels.put(l[0], Boolean.parseBoolean(l[1])));

        return labels;
    }

    /**
     * Creates topics with default configuration.
     * @param topicNames names of topics to create
     */
    private static void createTopics(Properties properties, String ...topicNames) {
        AdminClient adminClient = AdminClient.create(properties);
        adminClient.createTopics(
                Arrays.stream(topicNames)
                        .map(tn -> new NewTopic(tn, 1, (short) 1))
                        .collect(Collectors.toList())
        );

        adminClient.close();
    }
}
