package com.community.ai.dto;

import lombok.Data;

@Data
public class SecurityHazardEnhanceResponse {
    private String title;
    private String content;
    private String hazardType;
    private String location;
}
