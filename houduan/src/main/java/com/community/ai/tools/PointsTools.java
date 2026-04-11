package com.community.ai.tools;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.PointsAccount;
import com.community.entity.PointsRecord;
import com.community.service.PointsAccountService;
import com.community.service.PointsRecordService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PointsTools {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final PointsAccountService pointsAccountService;
    private final PointsRecordService pointsRecordService;

    public PointsTools(PointsAccountService pointsAccountService, PointsRecordService pointsRecordService) {
        this.pointsAccountService = pointsAccountService;
        this.pointsRecordService = pointsRecordService;
    }

    @Tool(name = "query_my_points_account", description = "Query current user's points balance and recent points changes")
    public String queryMyPointsAccount(
            @ToolParam(description = "Number of recent points records to return, from 1 to 10", required = false)
            @Nullable Integer detailLimit,
            ToolContext toolContext) {
        Long userId = ToolContextSupport.getUserId(toolContext);
        if (userId == null) {
            return "Missing current user context, unable to query the points account.";
        }

        PointsAccount account = pointsAccountService.getOrCreateByUserId(userId);
        int pageSize = normalizeLimit(detailLimit);
        Page<PointsRecord> page = pointsRecordService.pageByUserId(userId, 1, pageSize, null, null);
        List<PointsRecord> records = page.getRecords();

        StringBuilder builder = new StringBuilder();
        builder.append("Current points balance: ")
                .append(account.getTotalPoints() == null ? 0 : account.getTotalPoints())
                .append(".");
        if (records == null || records.isEmpty()) {
            builder.append("\nNo recent points records are available.");
            return builder.toString();
        }

        builder.append("\nRecent points records:");
        for (int i = 0; i < records.size(); i++) {
            PointsRecord item = records.get(i);
            builder.append("\n")
                    .append(i + 1).append(". ")
                    .append("id=").append(item.getId())
                    .append(", changeType=").append(toChangeTypeLabel(item.getChangeType()))
                    .append(", points=").append(item.getPoints() == null ? 0 : item.getPoints())
                    .append(", businessType=").append(defaultText(item.getBusinessType()))
                    .append(", remark=").append(defaultText(item.getRemark()))
                    .append(", createdAt=").append(formatDateTime(item.getCreateTime()));
        }
        return builder.toString();
    }

    private int normalizeLimit(@Nullable Integer limit) {
        if (limit == null || limit <= 0) {
            return DEFAULT_LIMIT;
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private String defaultText(@Nullable String value) {
        return value == null || value.isBlank() ? "N/A" : value.trim();
    }

    private String formatDateTime(@Nullable LocalDateTime value) {
        return value == null ? "N/A" : value.format(DATE_TIME_FORMATTER);
    }

    private String toChangeTypeLabel(@Nullable Integer value) {
        if (value == null) {
            return "UNKNOWN";
        }
        return value > 0 ? "INCOME" : "EXPENSE";
    }
}
