package com.community.ai.tools;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.ServiceAppointment;
import com.community.service.ServiceAppointmentService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class AppointmentTools {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ServiceAppointmentService serviceAppointmentService;

    public AppointmentTools(ServiceAppointmentService serviceAppointmentService) {
        this.serviceAppointmentService = serviceAppointmentService;
    }

    @Tool(name = "query_my_appointments", description = "Query current user's service appointments with optional filters")
    public String queryMyAppointments(
            @ToolParam(description = "Appointment status: 0 pending confirmation, 1 confirmed, 2 completed, 3 cancelled", required = false)
            @Nullable Integer status,
            @ToolParam(description = "Service type such as REPAIR, CLEAN, MEDICAL or OTHER", required = false)
            @Nullable String serviceType,
            @ToolParam(description = "Number of records to return, from 1 to 10", required = false)
            @Nullable Integer limit,
            ToolContext toolContext) {
        Long userId = ToolContextSupport.getUserId(toolContext);
        if (userId == null) {
            return "Missing current user context, unable to query appointments.";
        }

        int pageSize = normalizeLimit(limit);
        Page<ServiceAppointment> page = serviceAppointmentService.pageQuery(1, pageSize, normalizeText(serviceType), status, userId);
        List<ServiceAppointment> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            return "No matching appointments were found for the current user.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Found ").append(records.size()).append(" appointments:");
        for (int i = 0; i < records.size(); i++) {
            ServiceAppointment item = records.get(i);
            builder.append("\n")
                    .append(i + 1).append(". ")
                    .append("id=").append(item.getId())
                    .append(", title=").append(defaultText(item.getTitle()))
                    .append(", serviceType=").append(defaultText(item.getServiceType()))
                    .append(", status=").append(toAppointmentStatusLabel(item.getStatus()))
                    .append(", appointmentTime=").append(formatDateTime(item.getAppointmentTime()))
                    .append(", address=").append(defaultText(item.getAddress()));
        }
        return builder.toString();
    }

    private int normalizeLimit(@Nullable Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String normalizeText(@Nullable String value) {
        return value == null ? null : value.trim();
    }

    private String defaultText(@Nullable String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }

    private String formatDateTime(@Nullable LocalDateTime value) {
        return value == null ? "N/A" : value.format(DATE_TIME_FORMATTER);
    }

    private String toAppointmentStatusLabel(@Nullable Integer value) {
        if (value == null) {
            return "UNKNOWN";
        }
        return switch (value) {
            case 0 -> "PENDING_CONFIRMATION";
            case 1 -> "CONFIRMED";
            case 2 -> "COMPLETED";
            case 3 -> "CANCELLED";
            default -> "UNKNOWN";
        };
    }
}
