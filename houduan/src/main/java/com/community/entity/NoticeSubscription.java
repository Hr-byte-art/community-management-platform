package com.community.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 通知订阅表 - 记录用户订阅的通知类型
 */
@Data
@TableName("notice_subscription")
public class NoticeSubscription {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String noticeType; // 订阅的通知类型
    private Boolean receiveNotification; // 是否接收通知
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}