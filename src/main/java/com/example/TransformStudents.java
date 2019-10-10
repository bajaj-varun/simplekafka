package com.example;

import io.confluent.developer.avro.InputStudent;
import io.confluent.developer.avro.ProcessedStudents;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class TransformStudents {
    private Properties props;

    public Properties config() {
        props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        return props;
    }

    public Topology processStudentStream() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String inputTopic = "input-students";

        // is - inputStudents, ps - processedStudents
        KStream<String, InputStudent> is = builder.stream(inputTopic);
        KStream<Long, ProcessedStudents> ps = is.map((key, ist) ->
                new KeyValue(ist.getId(), convertRawStudent(ist)));

        ps.to("processed-students", Produced.with(Serdes.Long(), studentAvroSerde()));

        return builder.build();
    }

    private ProcessedStudents convertRawStudent(InputStudent is) {
        ProcessedStudents ps = new ProcessedStudents(
                is.getId(),
                is.getName(),
                is.getDoj(),
                is.getClassId(),
                "TODO: Class",
                is.getSubject(),
                "TODO: remarks"
        );
        System.out.println(ps.toString());
        return ps;
    }

    /**
     * @return
     */
    private SpecificAvroSerde<ProcessedStudents> studentAvroSerde() {
        SpecificAvroSerde<ProcessedStudents> stAvroSerde = new SpecificAvroSerde<>();

        final HashMap<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "http://localhost:8081");

        stAvroSerde.configure(serdeConfig, false);
        return stAvroSerde;
    }

    public static void main(String[] args) {
        TransformStudents ts = new TransformStudents();

        final KafkaStreams streams = new KafkaStreams(ts.processStudentStream(), ts.config());
        final CountDownLatch latch = new CountDownLatch(1);

        // Attach shutdown handler to catch Control-C.
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }
}
