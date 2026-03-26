package com.community.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("activity_registration")
public class ActivityRegistration {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private Integer status; // 0-已报名，1-已签到，2-已取消
    private LocalDateTime registerTime;
    private LocalDateTime checkInTime;
}
