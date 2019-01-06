package com.example.twittertest;

import com.example.twittertest.config.KStreamsProperties;
import com.example.twittertest.models.User;
import com.example.twittertest.serde.UserSerde;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Properties;

@SpringBootApplication
public class TwitterTestApplication {

	@Bean
	public Topology kStreamTopology(KStreamsProperties config){
		Serde<String> stringSerde = Serdes.String();
		UserSerde userSerde = new UserSerde();

		StreamsBuilder builder = new StreamsBuilder();
		KStream<String, User> users = builder.stream(config.getUserTopic());

		users.print(Printed.toSysOut());

		return builder.build();
	}

	@Bean
	public KafkaStreams kafkaStreams(KStreamsProperties config) {
		Properties props = new Properties();
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.getConsumerAutoOffsetReset());
		props.put(StreamsConfig.APPLICATION_SERVER_CONFIG, config.getApplicationServer());
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, config.getApplicationId());
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, config.getBootstrapServers());
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, config.getDefaultKeySerde());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, config.getDefaultValueSerde());
		props.put(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, config.getProcessingGuarantee());
		props.put(StreamsConfig.STATE_DIR_CONFIG, config.getStateStoreDirectory());
		props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, config.getCacheMaxBytesBuffer());
		props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, config.getCommitIntervalMs());
		props.put(CommonClientConfigs.METADATA_MAX_AGE_CONFIG, config.getMetadataMaxAgeMs());
		return new KafkaStreams(kStreamTopology(config), props);
	}

	@Component
	public static class KafkaStreamsBootstrap implements CommandLineRunner {

		@Autowired
		KafkaStreams kafkaStreams;

		@Override
		public void run(String... args) throws Exception {
			kafkaStreams.cleanUp();
			kafkaStreams.start();
			Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(TwitterTestApplication.class, args);
	}

}

