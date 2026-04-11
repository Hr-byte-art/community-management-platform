package com.community.ai.tools;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.entity.MessageNotice;
import com.community.service.MessageNoticeService;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class MessageTools {

    private static final int DEFAULT_LIMIT = 5;
    private static final int MAX_LIMIT = 10;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final MessageNoticeService messageNoticeService;

    public MessageTools(MessageNoticeService messageNoticeService) {
        this.messageNoticeService = messageNoticeService;
    }

    @Tool(name = "query_my_unread_message_summary", description = "Query current user's unread message count and recent unread messages")
    public String queryMyUnreadMessageSummary(
            @ToolParam(description = "Number of recent unread messages to return, from 1 to 10", required = false)
            @Nullable Integer limit,
            ToolContext toolContext) {
        Long userId = ToolContextSupport.getUserId(toolContext);
        if (userId == null) {
            return "Missing current user context, unable to query messages.";
        }

        long unreadCount = messageNoticeService.unreadCount(userId);
        int pageSize = normalizeLimit(limit);
        Page<MessageNotice> page = messageNoticeService.pageByUser(userId, 1, pageSize, 0, null);
        List<MessageNotice> records = page.getRecords();

        StringBuilder builder = new StringBuilder();
        builder.append("Unread message count: ").append(unreadCount).append(".");
        if (records == null || records.isEmpty()) {
            builder.append("\nNo recent unread messages are available.");
            return builder.toString();
        }

        builder.append("\nRecent unread messages:");
        for (int i = 0; i < records.size(); i++) {
            MessageNotice item = records.get(i);
            builder.append("\n")
                    .append(i + 1).append(". ")
                    .append("id=").append(item.getId())
                    .append(", title=").append(defaultText(item.getTitle()))
                    .append(", type=").append(defaultText(item.getMessageType()))
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
}
