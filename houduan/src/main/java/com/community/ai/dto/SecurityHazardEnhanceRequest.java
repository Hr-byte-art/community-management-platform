package com.community.ai.dto;

import lombok.Data;

@Data
public class SecurityHazardEnhanceRequest {
    private String title;
    private String content;
    private String hazardType;
    private String location;
}
