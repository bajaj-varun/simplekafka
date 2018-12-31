package com.example;

import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.stream.IntStream;

public class SimpleProducer {


    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        KafkaProducer<String, Payments> kafkaProducer = new KafkaProducer<String, Payments>(props);
        final String TOPIC = "abcd32";

        IntStream.range(1,10).forEach(v -> {
                Payments payments = new Payments(v+"", 1000.00d);
                ProducerRecord <String, Payments> record = new ProducerRecord<>(TOPIC,payments.getId().toString(), payments);
                System.out.println("Message Sent =>"+v);
                try {
                    kafkaProducer.send(record,(recordMetadata, e) -> {System.out.println("abc");e.printStackTrace();});
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
        });
    }
}

