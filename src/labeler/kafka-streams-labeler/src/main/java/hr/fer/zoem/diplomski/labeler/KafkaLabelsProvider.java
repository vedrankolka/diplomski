package hr.fer.zoem.diplomski.labeler;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class KafkaLabelsProvider extends KafkaConsumer<String, String> {

    public static final String TOPIC_KEY = "labels.provider.topic";
    public static final String EOF_KEY = "labels.provider.eof";
//    public static final String MAX_RETRIES_KEY = "labels.provider.max.retries";

    private static final Logger logger = Logger.getLogger(KafkaLabelsProvider.class.getName());

    private String topic;

    public KafkaLabelsProvider(Properties props) {
        super(props);
        // TODO dodat sve potrebno u props i procitat to
        this.topic = Objects.requireNonNull(props.getProperty(TOPIC_KEY), "A topic is required.");
        this.subscribe(Collections.singleton(this.topic));
    }

    /**
     * Get labels from the kafka topic.
     * @return map of ip addresses and attack labels for the addresses
     * or <code>null</code> if no new labels are on the topic.
     */
    public Map<String, Boolean> poll() {
        boolean eofReceived = false;

        // TODO get labels and parse them

        return null;
    }

    /**
     * Blocking version of the poll method.
     * @return map of ip addresses and attack labels for the addresses.
     * @see KafkaLabelsProvider#poll
     */
    public Map<String, Boolean> get() {
        Map<String, Boolean> labels = null;

        while (labels == null) {
            labels = poll();
        }

        return labels;
    }

    private Map<String, Boolean> loadLabels() {
        Map<String, Boolean> labels = new HashMap<>();
        // first see if there are any new labels on the topic

        // TODO procitat samo zadnju poruku, nek onda bude formatirana ko i csv
//                .skip(1)
//                .map(l -> l.split(","))
//                .forEach(l -> labels.put(l[0], Boolean.parseBoolean(l[1])));

        return labels;
    }

}
