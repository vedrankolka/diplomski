package hr.fer.zoem.diplomski.labeler;

import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class KafkaLabelsProvider extends KafkaConsumer<String, String> {

    public static final String TOPIC_KEY = "labels.provider.topic";

    private static final Logger logger = Logger.getLogger(KafkaLabelsProvider.class.getName());

    private String topic;

    public KafkaLabelsProvider(Properties props) {
        super(props);
        this.topic = Objects.requireNonNull(props.getProperty(TOPIC_KEY), "A topic is required.");
        this.subscribe(Collections.singleton(this.topic));
    }

    private Map<String, Boolean> loadLabels() {
        Map<String, Boolean> labels = new HashMap<>();
        this.seekToEnd(this.assignment());
        // TODO procitat samo zadnju poruku, nek onda bude formatirana ko i csv
//                .skip(1)
//                .map(l -> l.split(","))
//                .forEach(l -> labels.put(l[0], Boolean.parseBoolean(l[1])));

        return labels;
    }
}
