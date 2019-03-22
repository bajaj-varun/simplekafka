package com.example.consumers;

import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.*;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Printed;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PageViewConsumer {
    public static void main(String[] args) {
        final String TOPIC="pageviews";
        final String TOPIC1="users";

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "devecpvm004010:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test");
        props.put("application.id", "test");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();
        Map<String, Object> serdeProps = new HashMap<>();
        //serdeProps.put("JsonPOJOClass", PageViewsPojo.class);

        /*Serializer<PageViewsPojo> pvPojoSerializer = new JsonPOJOSerializer<>();
        pvPojoSerializer.configure(serdeProps, false);

        Deserializer<PageViewsPojo> pvPojoDeserializer = new JsonPOJODeserializer<>();
        pvPojoDeserializer.configure(serdeProps, false);

        KStream<String, PageViewsPojo> one;
        one = builder.stream(TOPIC, Consumed.with(Serdes.String(),Serdes.serdeFrom(pvPojoSerializer, pvPojoDeserializer)));

        serdeProps.put("schema.registry.url", "http://devecpvm004010:8081");
        final Serde<UserPojo> valueSpecificAvroSerde = new SpecificAvroSerde();
        valueSpecificAvroSerde.configure(serdeProps, false); // `false` for record values
        KStream<String, UserPojo> two = builder.stream(TOPIC1, Consumed.with(Serdes.String(),valueSpecificAvroSerde));

        one.print(Printed.toSysOut());
        two.print(Printed.toSysOut());*/

       /* KStream<String, String> joined = one.leftJoin(two,
                (one.,"userid") ->
        );*/

        /*KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();*/
    }
}

class UserPageView{
    /*UserPojo userPojo;
    PageViewsPojo pageViewsPojo;

    public UserPojo getUserPojo() {
        return userPojo;
    }

    public void setUserPojo(UserPojo userPojo) {
        this.userPojo = userPojo;
    }

    public PageViewsPojo getPageViewsPojo() {
        return pageViewsPojo;
    }

    public void setPageViewsPojo(PageViewsPojo pageViewsPojo) {
        this.pageViewsPojo = pageViewsPojo;
    }*/
}