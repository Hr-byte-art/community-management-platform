package com.community.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer localDateTimeDeserializerCustomizer() {
        return builder -> builder.deserializerByType(LocalDateTime.class, new FlexibleLocalDateTimeDeserializer());
    }

    static class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        private static final List<DateTimeFormatter> FORMATTERS = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
                throws IOException, JsonProcessingException {
            String text = parser.getValueAsString();
            if (text == null || text.isBlank()) {
                return null;
            }

            String value = text.trim();
            for (DateTimeFormatter formatter : FORMATTERS) {
                try {
                    return LocalDateTime.parse(value, formatter);
                } catch (DateTimeParseException ignored) {
                }
            }

            throw context.weirdStringException(
                    value,
                    LocalDateTime.class,
                    "Unsupported LocalDateTime format, expected yyyy-MM-dd HH:mm:ss or ISO-8601"
            );
        }
    }
}
