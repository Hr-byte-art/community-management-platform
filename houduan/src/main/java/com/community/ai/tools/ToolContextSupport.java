package com.community.ai.tools;

import org.springframework.ai.chat.model.ToolContext;

import java.util.Map;

public final class ToolContextSupport {

    public static final String USER_ID = "userId";
    public static final String USERNAME = "username";
    public static final String ROLE = "role";

    private ToolContextSupport() {
    }

    public static Long getUserId(ToolContext toolContext) {
        Object value = getContextValue(toolContext, USER_ID);
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String text && !text.isBlank()) {
            try {
                return Long.parseLong(text.trim());
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    public static String getUsername(ToolContext toolContext) {
        return getString(toolContext, USERNAME);
    }

    public static String getRole(ToolContext toolContext) {
        return getString(toolContext, ROLE);
    }

    private static String getString(ToolContext toolContext, String key) {
        Object value = getContextValue(toolContext, key);
        return value == null ? null : String.valueOf(value);
    }

    private static Object getContextValue(ToolContext toolContext, String key) {
        if (toolContext == null) {
            return null;
        }
        Map<String, Object> context = toolContext.getContext();
        return context == null ? null : context.get(key);
    }
}
