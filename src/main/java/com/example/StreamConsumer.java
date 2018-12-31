package com.example;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

public class StreamConsumer {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final String TOPIC = "abcd32";
        // When you want to override serdes explicitly/selectively
        final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
                "http://localhost:8081");
        final Serde<String> stringSerde = Serdes.String();
        final Serde<Payments> valueSpecificAvroSerde = new SpecificAvroSerde<>();
        valueSpecificAvroSerde.configure(serdeConfig, false); // `false` for record values

        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Payments> stream = builder.stream(TOPIC, Consumed.with(stringSerde, valueSpecificAvroSerde));
        stream.print(Printed.toSysOut());

        stream.foreach((k,v) -> System.out.println("Key =>"+k+", Value =>"+v.toString()));

        KafkaStreams streams = new KafkaStreams(builder.build(),props);
        streams.start();
    }
}
