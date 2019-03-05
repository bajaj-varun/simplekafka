package com.example.serdes;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.util.Map;


public class JsonPojoDeserializer<T> implements Deserializer<T> {
    private ObjectMapper objectMapper = new ObjectMapper();
    private Class<T> tClass;

    @Override
    public void configure(Map<String, ?> map, boolean isKey) {
        tClass = (Class<T>) map.get("JsonPOJOClass");
    }

    @Override
    public T deserialize(String s, byte[] data) {
        if (data == null){
            return null;
        }

        try {
            //objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            return objectMapper.readValue(data, tClass);
        } catch (IOException e) {
            e.printStackTrace();
        }
    return null;
    }

    @Override
    public void close() {

    }
}
