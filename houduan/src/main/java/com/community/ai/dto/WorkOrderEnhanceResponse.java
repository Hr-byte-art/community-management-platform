package com.community.ai.dto;

import lombok.Data;

@Data
public class WorkOrderEnhanceResponse {
    private String title;
    private String content;
    private String orderType;
    private Integer priority;
}
