package org.example.blps_lab3_monolit.jms.listener.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TextMessageToObjectConverter {
    public static <T> T convert(String jsonMessage, Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(jsonMessage, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
