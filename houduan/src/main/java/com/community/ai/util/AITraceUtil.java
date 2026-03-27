package com.community.ai.util;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public final class AITraceUtil {

    private static final String AI_TRACE_ID_HEADER = "X-AI-Trace-Id";

    private AITraceUtil() {
    }

    public static String currentTraceId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "unknown";
            }
            HttpServletRequest request = attributes.getRequest();
            if (request == null) {
                return "unknown";
            }
            String traceId = request.getHeader(AI_TRACE_ID_HEADER);
            return StrUtil.isBlank(traceId) ? "unknown" : traceId;
        } catch (Exception e) {
            return "unknown";
        }
    }

    public static int safeLength(String value) {
        return value == null ? 0 : value.length();
    }
}
