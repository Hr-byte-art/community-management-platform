package com.community.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class FlexibleLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

        private static final List<DateTimeFormatter> FORMATTERS = List.of(
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
        );

        @Override
        public LocalDateTime deserialize(JsonParser parser, DeserializationContext context)
                throws IOException {
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

            try {
                return OffsetDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                        .atZoneSameInstant(ZoneId.systemDefault())
                        .toLocalDateTime();
            } catch (DateTimeParseException ignored) {
            }

            throw context.weirdStringException(
                    value,
                    LocalDateTime.class,
                    "Unsupported LocalDateTime format, expected yyyy-MM-dd HH:mm:ss, yyyy-MM-dd HH:mm or ISO-8601"
            );
        }
}