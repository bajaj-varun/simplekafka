package com.example;

import com.examples.pojos.AirportsPojo;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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

        // Simple Producer example
        //new PaymentProducer().startProducer(props);

        // Read from File example
        // new FlightDataProducer().startAirportProducer(props);

        // Read from Mysql and update column names
    }
}

class FlightDataProducer{
    private static final String AIRPORTS="AIRPORTS";
    private static final String AIRPORTS_FILE = "src/main/resources/airports.csv";

    public static void startAirportProducer(Properties props){
        KafkaProducer<String, AirportsPojo> airportsKafkaProducer = new KafkaProducer(props);
        try {
            Reader reader = Files.newBufferedReader(Paths.get(AIRPORTS_FILE));
            CSVParser csvParser = new CSVParser(reader,
                    CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreHeaderCase()
                    .withTrim()
            );

            for (CSVRecord record: csvParser) {
                AirportsPojo airportsPojo = new AirportsPojo(
                        record.get("iata"),
                        record.get("airport"),
                        record.get("city"),
                        record.get("state"),
                        record.get("country"),
                        Double.parseDouble(record.get("lat")),
                        Double.parseDouble(record.get("long"))
                );
                ProducerRecord<String, AirportsPojo> pr =
                        new ProducerRecord(AIRPORTS, airportsPojo.getIata(), airportsPojo);
                airportsKafkaProducer.send(pr,(recordMetadata, e) -> {
                    if(e==null){
                        System.out.println("Success");
                        System.out.println(recordMetadata);
                    }else{
                        System.out.println("Error =>"+recordMetadata.toString());
                        e.printStackTrace();
                    }
                });
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class  PaymentProducer{
    public static void startProducer(Properties props){
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

