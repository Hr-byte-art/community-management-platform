package com.community.ai.dto;

import lombok.Data;

@Data
public class ServiceAppointmentEnhanceRequest {
    private String title;
    private String content;
    private String serviceType;
    private String address;
    private String appointmentTime;
    private String remark;
}
