package com.example;

import io.confluent.developer.avro.Movie;
import io.confluent.developer.avro.RawMovie;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.*;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class TransformStream {

    public Properties buildStreamsProperties() {
        Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, "http://localhost:8081");

        return props;
    }

    public Topology buildTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        final String inputTopic = "raw-movies";

        KStream<String, RawMovie> rawMovies = builder.stream(inputTopic);
        KStream<Long, Movie> movies = rawMovies.map((key, rawMovie) ->
                new KeyValue<Long, Movie>(rawMovie.getId(), convertRawMovie(rawMovie)));

        movies.to("movies", Produced.with(Serdes.Long(), movieAvroSerde()));

        return builder.build();
    }

    public static Movie convertRawMovie(RawMovie rawMovie) {
        String titleParts[] = rawMovie.getTitle().toString().split("::");
        String title = titleParts[0];
        int releaseYear = Integer.parseInt(titleParts[1]);
        Movie m = new Movie(rawMovie.getId(), title, releaseYear, rawMovie.getGenre());
        System.out.println(m.toString());
        return m;
    }

    private SpecificAvroSerde<Movie> movieAvroSerde() {
        SpecificAvroSerde<Movie> movieAvroSerde = new SpecificAvroSerde<>();

        final HashMap<String, String> serdeConfig = new HashMap<>();
        serdeConfig.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                "http://localhost:8081");

        movieAvroSerde.configure(serdeConfig, false);
        return movieAvroSerde;
    }

    public void createTopics() {
        Map<String, Object> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        AdminClient client = AdminClient.create(config);

        List<NewTopic> topics = new ArrayList<>();

//        topics.add(new NewTopic(
//                envProps.getProperty("input.topic.name"),
//                Integer.parseInt(envProps.getProperty("input.topic.partitions")),
//                Short.parseShort(envProps.getProperty("input.topic.replication.factor"))));
//
//        topics.add(new NewTopic(
//                envProps.getProperty("output.topic.name"),
//                Integer.parseInt(envProps.getProperty("output.topic.partitions")),
//                Short.parseShort(envProps.getProperty("output.topic.replication.factor"))));

        client.createTopics(topics);
        client.close();
    }

    public Properties loadEnvProperties(String fileName) throws IOException {
        Properties envProps = new Properties();
        FileInputStream input = new FileInputStream(fileName);
        envProps.load(input);
        input.close();

        return envProps;
    }

    public static void main(String[] args) throws Exception {
        TransformStream ts = new TransformStream();
        Properties streamProps = ts.buildStreamsProperties();
        Topology topology = ts.buildTopology();

        ts.createTopics();

        final KafkaStreams streams = new KafkaStreams(topology, streamProps);
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
