package ru.kkb.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.elasticsearch.core.EntityMapper;
import org.springframework.data.mapping.MappingException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomEntityMapper implements EntityMapper {

    private final ObjectMapper objectMapper;

    public CustomEntityMapper() {
        objectMapper = new ObjectMapper();

        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String mapToString(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }

    @Override
    public <T> T mapToObject(String source, Class<T> clazz) throws IOException {
        return objectMapper.readValue(source, clazz);
    }

    @Override
    public Map<String, Object> mapObject(Object source) {
        try {
            return objectMapper.readValue(mapToString(source), HashMap.class);
        } catch (IOException e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

    @Override
    public <T> T readObject(Map<String, Object> source, Class<T> targetType) {
        try {
            return mapToObject(mapToString(source), targetType);
        } catch (IOException e) {
            throw new MappingException(e.getMessage(), e);
        }
    }
}
