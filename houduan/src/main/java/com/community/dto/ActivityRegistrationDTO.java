package com.community.dto;

import lombok.Data;

/**
 * 活动报名请求DTO
 */
@Data
public class ActivityRegistrationDTO {
    private Long activityId;
    private Long userId;
}