package com.community.ai.service;

import com.community.ai.dto.NeighborHelpEnhanceRequest;
import com.community.ai.dto.NeighborHelpEnhanceResponse;
import com.community.ai.dto.SecurityHazardEnhanceRequest;
import com.community.ai.dto.SecurityHazardEnhanceResponse;
import com.community.ai.dto.ServiceAppointmentEnhanceRequest;
import com.community.ai.dto.ServiceAppointmentEnhanceResponse;
import com.community.ai.dto.WorkOrderEnhanceRequest;
import com.community.ai.dto.WorkOrderEnhanceResponse;
import reactor.core.publisher.Flux;

public interface AIService {


    /**
     * 扩写/完善输入信息
     *
     * @param question 问题
     * @return 答案
     */
    String getAnswers(String question);

    /**
     * 完善工单表单信息
     *
     * @param request 工单表单当前内容
     * @return 完善后的工单信息
     */
    WorkOrderEnhanceResponse enhanceWorkOrder(WorkOrderEnhanceRequest request);

    /**
     * 完善安全隐患表单信息
     *
     * @param request 隐患表单当前内容
     * @return 完善后的隐患信息
     */
    SecurityHazardEnhanceResponse enhanceSecurityHazard(SecurityHazardEnhanceRequest request);

    /**
     * 完善邻里互助表单信息
     *
     * @param request 互助表单当前内容
     * @return 完善后的互助信息
     */
    NeighborHelpEnhanceResponse enhanceNeighborHelp(NeighborHelpEnhanceRequest request);

    /**
     * 完善服务预约表单信息
     *
     * @param request 预约表单当前内容
     * @return 完善后的预约信息
     */
    ServiceAppointmentEnhanceResponse enhanceServiceAppointment(ServiceAppointmentEnhanceRequest request);

    /**
     * 提问
     *
     * @param question 问题
     * @return 回复
     */
    Flux<String> askQuestion(String question, Long userId, String username, String role);
}
