package com.example.twittertest.serde;

import com.example.twittertest.models.User;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class UserSerde extends Serdes.WrapperSerde<User>{

    public UserSerde() {
        super(new JsonSerializer<>(), new JsonDeserializer<>(User.class));
    }
}
