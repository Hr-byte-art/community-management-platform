package com.community.ai.tools;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.WorkOrder;
import com.community.service.WorkOrderService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class WorkOrderTools {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final WorkOrderService workOrderService;

    public WorkOrderTools(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @Tool(name = "query_my_work_orders", description = "Query current user's work orders with optional filters")
    public String queryMyWorkOrders(
            @ToolParam(description = "Work order status: 0 pending, 1 processing, 2 completed, 3 closed", required = false)
            @Nullable Integer status,
            @ToolParam(description = "Work order type: REPAIR, COMPLAINT, SUGGESTION or OTHER", required = false)
            @Nullable String orderType,
            @ToolParam(description = "Number of records to return, from 1 to 10", required = false)
            @Nullable Integer limit,
            ToolContext toolContext) {
        Long userId = ToolContextSupport.getUserId(toolContext);
        if (userId == null) {
            return "Missing current user context, unable to query work orders.";
        }

        int pageSize = normalizeLimit(limit);
        Page<WorkOrder> page = workOrderService.pageQuery(1, pageSize, null, normalizeText(orderType), status, userId);
        List<WorkOrder> records = page.getRecords();
        if (records == null || records.isEmpty()) {
            return "No matching work orders were found for the current user.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Found ").append(records.size()).append(" work orders:");
        for (int i = 0; i < records.size(); i++) {
            WorkOrder item = records.get(i);
            builder.append("\n")
                    .append(i + 1).append(". ")
                    .append("id=").append(item.getId())
                    .append(", title=").append(defaultText(item.getTitle()))
                    .append(", type=").append(toWorkOrderTypeLabel(item.getOrderType()))
                    .append(", status=").append(toWorkOrderStatusLabel(item.getStatus()))
                    .append(", priority=").append(toPriorityLabel(item.getPriority()))
                    .append(", createdAt=").append(formatDateTime(item.getCreateTime()));
        }
        return builder.toString();
    }

    @Tool(name = "query_my_work_order_detail", description = "Query details of one work order owned by the current user")
    public String queryMyWorkOrderDetail(
            @ToolParam(description = "Work order id")
            Long workOrderId,
            ToolContext toolContext) {
        Long userId = ToolContextSupport.getUserId(toolContext);
        if (userId == null) {
            return "Missing current user context, unable to query work order detail.";
        }
        if (workOrderId == null) {
            return "Please provide a work order id.";
        }

        WorkOrder workOrder = workOrderService.getById(workOrderId);
        if (workOrder == null || !userId.equals(workOrder.getSubmitterId())) {
            return "The work order was not found, or the current user has no permission to view it.";
        }

        return "Work order detail:"
                + "\nid=" + workOrder.getId()
                + "\ntitle=" + defaultText(workOrder.getTitle())
                + "\ncontent=" + defaultText(workOrder.getContent())
                + "\ntype=" + toWorkOrderTypeLabel(workOrder.getOrderType())
                + "\nstatus=" + toWorkOrderStatusLabel(workOrder.getStatus())
                + "\npriority=" + toPriorityLabel(workOrder.getPriority())
                + "\nisOvertime=" + (Integer.valueOf(1).equals(workOrder.getIsOvertime()) ? "YES" : "NO")
                + "\ndeadline=" + formatDateTime(workOrder.getDeadline())
                + "\nhandleResult=" + defaultText(workOrder.getHandleResult())
                + "\ncreatedAt=" + formatDateTime(workOrder.getCreateTime())
                + "\nupdatedAt=" + formatDateTime(workOrder.getUpdateTime());
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

    private String toWorkOrderTypeLabel(@Nullable String value) {
        if (value == null) {
            return "UNKNOWN";
        }
        return switch (value) {
            case "REPAIR" -> "REPAIR";
            case "COMPLAINT" -> "COMPLAINT";
            case "SUGGESTION" -> "SUGGESTION";
            case "OTHER" -> "OTHER";
            default -> value;
        };
    }

    private String toWorkOrderStatusLabel(@Nullable Integer value) {
        if (value == null) {
            return "UNKNOWN";
        }
        return switch (value) {
            case 0 -> "PENDING";
            case 1 -> "PROCESSING";
            case 2 -> "COMPLETED";
            case 3 -> "CLOSED";
            default -> "UNKNOWN";
        };
    }

    private String toPriorityLabel(@Nullable Integer value) {
        if (value == null) {
            return "UNKNOWN";
        }
        return switch (value) {
            case 0 -> "LOW";
            case 1 -> "MEDIUM";
            case 2 -> "HIGH";
            default -> "UNKNOWN";
        };
    }
}
