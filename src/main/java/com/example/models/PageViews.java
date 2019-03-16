package com.example.models;

import io.confluent.kafka.streams.serdes.avro.GenericAvroSerde;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import io.confluent.ksql.avro_schemas.KsqlDataSourceSchema;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class PageViews {

    public static void main(String[] args) {
        final String TOPIC = "pageviews";
        final String USERS_TOPIC = "users";

        final StreamsBuilder builder = new StreamsBuilder();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        final Map<String, String> serdeConfig = new HashMap();
        serdeConfig.put("schema.registry.url", "http://localhost:8081");

        final Serde<String> keySpecificAvroSerde = new Serdes.StringSerde();
        keySpecificAvroSerde.configure(serdeConfig, true);

        final Serde<KsqlDataSourceSchema> valueSpecificAvroSerde = new SpecificAvroSerde<KsqlDataSourceSchema>();
        valueSpecificAvroSerde.configure(serdeConfig, false);

        final KStream<String, KsqlDataSourceSchema> stream = builder.stream(TOPIC, Consumed.with(keySpecificAvroSerde, valueSpecificAvroSerde));

        // Users Serde
        final Serde<GenericRecord> usersSerde = new GenericAvroSerde();
        usersSerde.configure(serdeConfig, false);

        final KStream<String, GenericRecord> userStream = builder.stream(USERS_TOPIC, Consumed.with(Serdes.String(),usersSerde));

        //stream.print(Printed.toSysOut());
        //stream.foreach((k,v) -> System.out.println("Key =>"+k.toString()+", v=>"+v.toString()));
        //userStream.foreach((k,v) -> System.out.println("Key =>"+k.toString()+", v=>"+v.toString()));

        stream.join(userStream,
                (k,v)->"K=>"+k+", v=>"+v.toString(),
                JoinWindows.of(TimeUnit.SECONDS.toMillis(10000)),
                Joined.with(
                        Serdes.String(),
                        valueSpecificAvroSerde,
                        usersSerde
                )
        ).print(Printed.toSysOut());

        KafkaStreams streams = new KafkaStreams(builder.build(),props);
        streams.start();
    }
}