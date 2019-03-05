package com.example.models;

import com.example.PageViewsValues;
import com.example.pojos.PageViewsPojo;
import com.example.serdes.JsonPojoDeserializer;
import com.example.serdes.JsonPojoSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PageViews {
    public static void main(String[] args) {
        final String TOPIC = "pageviews1";
        final StreamsBuilder builder = new StreamsBuilder();

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "Test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        //props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, "1000");
        //props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<PageViewsPojo> serializer = new JsonPojoSerializer<>();
        serdeProps.put("JsonPOJOClass",PageViewsPojo.class);
        serializer.configure(serdeProps, false);

        final Deserializer<PageViewsPojo> deserializer = new JsonPojoDeserializer<>();
        serdeProps.put("JsonPOJOClass",PageViewsPojo.class);
        deserializer.configure(serdeProps, false);

        final KStream<String, PageViewsPojo> stream = builder.stream(TOPIC, Consumed.with(Serdes.String(), Serdes.serdeFrom(serializer,deserializer)));

        stream.print(Printed.toSysOut());
        stream.foreach((k,v) -> System.out.println("Key =>"+k+", Value =>"+v.toString()));

        KafkaStreams streams = new KafkaStreams(builder.build(),props);
        streams.start();
    }
}