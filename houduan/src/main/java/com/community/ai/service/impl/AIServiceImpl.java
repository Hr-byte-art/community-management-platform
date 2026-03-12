package com.community.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.community.ai.dto.NeighborHelpEnhanceRequest;
import com.community.ai.dto.NeighborHelpEnhanceResponse;
import com.community.ai.dto.SecurityHazardEnhanceRequest;
import com.community.ai.dto.SecurityHazardEnhanceResponse;
import com.community.ai.dto.ServiceAppointmentEnhanceRequest;
import com.community.ai.dto.ServiceAppointmentEnhanceResponse;
import com.community.ai.dto.WorkOrderEnhanceRequest;
import com.community.ai.dto.WorkOrderEnhanceResponse;
import com.community.ai.service.AIService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;


@Service
public class AIServiceImpl implements AIService {

    private static final Set<String> WORK_ORDER_TYPES = Set.of("REPAIR", "COMPLAINT", "SUGGESTION", "OTHER");
    private static final Set<String> HAZARD_TYPES = Set.of("FIRE", "THEFT", "TRAFFIC", "OTHER");
    private static final Set<String> NEIGHBOR_HELP_TYPES = Set.of("SEEK", "OFFER");
    private static final Set<String> NEIGHBOR_HELP_CATEGORIES = Set.of("DAILY", "SKILL", "ITEM", "OTHER");
    private static final Set<String> APPOINTMENT_SERVICE_TYPES = Set.of("REPAIR", "CLEAN", "MEDICAL", "OTHER");

    @Resource
    private ChatModel openAiChatModel;

    @Resource
    private ObjectMapper objectMapper;

    @Value("classpath:/prompt/FillInWorkOrderSystemPrompt.st")
    private org.springframework.core.io.Resource workOrderTextPromptResource;

    @Value("classpath:/prompt/StructuredWorkOrderEnhancePrompt.st")
    private org.springframework.core.io.Resource structuredWorkOrderPromptResource;

    @Value("classpath:/prompt/StructuredHazardEnhancePrompt.st")
    private org.springframework.core.io.Resource structuredHazardPromptResource;

    @Value("classpath:/prompt/StructuredNeighborHelpEnhancePrompt.st")
    private org.springframework.core.io.Resource structuredNeighborHelpPromptResource;

    @Value("classpath:/prompt/StructuredAppointmentEnhancePrompt.st")
    private org.springframework.core.io.Resource structuredAppointmentPromptResource;

    private String workOrderTextPrompt;
    private String structuredWorkOrderPrompt;
    private String structuredHazardPrompt;
    private String structuredNeighborHelpPrompt;
    private String structuredAppointmentPrompt;

    @PostConstruct
    public void init() {
        workOrderTextPrompt = readPrompt(workOrderTextPromptResource);
        structuredWorkOrderPrompt = readPrompt(structuredWorkOrderPromptResource);
        structuredHazardPrompt = readPrompt(structuredHazardPromptResource);
        structuredNeighborHelpPrompt = readPrompt(structuredNeighborHelpPromptResource);
        structuredAppointmentPrompt = readPrompt(structuredAppointmentPromptResource);
    }

    @Override
    public String getAnswers(String question) {
        return callModel(workOrderTextPrompt, question);
    }

    @Override
    public WorkOrderEnhanceResponse enhanceWorkOrder(WorkOrderEnhanceRequest request) {
        String userPrompt = buildWorkOrderUserPrompt(request);
        String content = callModel(structuredWorkOrderPrompt, userPrompt);
        WorkOrderEnhanceResponse response = parseModelResponse(content, WorkOrderEnhanceResponse.class);
        return sanitizeWorkOrderResponse(response, request);
    }

    @Override
    public SecurityHazardEnhanceResponse enhanceSecurityHazard(SecurityHazardEnhanceRequest request) {
        String userPrompt = buildSecurityHazardUserPrompt(request);
        String content = callModel(structuredHazardPrompt, userPrompt);
        SecurityHazardEnhanceResponse response = parseModelResponse(content, SecurityHazardEnhanceResponse.class);
        return sanitizeSecurityHazardResponse(response, request);
    }

    @Override
    public NeighborHelpEnhanceResponse enhanceNeighborHelp(NeighborHelpEnhanceRequest request) {
        String userPrompt = buildNeighborHelpUserPrompt(request);
        String content = callModel(structuredNeighborHelpPrompt, userPrompt);
        NeighborHelpEnhanceResponse response = parseModelResponse(content, NeighborHelpEnhanceResponse.class);
        return sanitizeNeighborHelpResponse(response, request);
    }

    @Override
    public ServiceAppointmentEnhanceResponse enhanceServiceAppointment(ServiceAppointmentEnhanceRequest request) {
        String userPrompt = buildAppointmentUserPrompt(request);
        String content = callModel(structuredAppointmentPrompt, userPrompt);
        ServiceAppointmentEnhanceResponse response = parseModelResponse(content, ServiceAppointmentEnhanceResponse.class);
        return sanitizeServiceAppointmentResponse(response, request);
    }

    private String callModel(String systemPrompt, String userPrompt) {
        ChatClient chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .build();

        return chatClient.prompt()
                .user(userPrompt)
                .call()
                .content();
    }

    private String readPrompt(org.springframework.core.io.Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("AI 提示词加载失败", e);
        }
    }

    private String buildWorkOrderUserPrompt(WorkOrderEnhanceRequest request) {
        return "请基于下面的工单草稿完善表单信息，并严格按系统要求返回 JSON。\n"
                + "标题：" + safeValue(request.getTitle()) + "\n"
                + "工单类型：" + safeValue(request.getOrderType()) + "\n"
                + "优先级：" + (request.getPriority() == null ? "[未填写]" : request.getPriority()) + "\n"
                + "内容：" + safeValue(request.getContent());
    }

    private String buildSecurityHazardUserPrompt(SecurityHazardEnhanceRequest request) {
        return "请基于下面的安全隐患草稿完善表单信息，并严格按系统要求返回 JSON。\n"
                + "标题：" + safeValue(request.getTitle()) + "\n"
                + "隐患类型：" + safeValue(request.getHazardType()) + "\n"
                + "隐患位置：" + safeValue(request.getLocation()) + "\n"
                + "隐患描述：" + safeValue(request.getContent());
    }

    private String buildNeighborHelpUserPrompt(NeighborHelpEnhanceRequest request) {
        return "请基于下面的邻里互助草稿完善表单信息，并严格按系统要求返回 JSON。\n"
                + "标题：" + safeValue(request.getTitle()) + "\n"
                + "互助类型：" + safeValue(request.getHelpType()) + "\n"
                + "互助分类：" + safeValue(request.getCategory()) + "\n"
                + "内容：" + safeValue(request.getContent());
    }

    private String buildAppointmentUserPrompt(ServiceAppointmentEnhanceRequest request) {
        return "请基于下面的服务预约草稿完善表单信息，并严格按系统要求返回 JSON。\n"
                + "标题：" + safeValue(request.getTitle()) + "\n"
                + "服务类型：" + safeValue(request.getServiceType()) + "\n"
                + "服务地址：" + safeValue(request.getAddress()) + "\n"
                + "预约时间：" + safeValue(request.getAppointmentTime()) + "\n"
                + "预约内容：" + safeValue(request.getContent()) + "\n"
                + "备注：" + safeValue(request.getRemark());
    }

    private String safeValue(String value) {
        return StrUtil.isBlank(value) ? "[未填写]" : value.trim();
    }

    private <T> T parseModelResponse(String content, Class<T> clazz) {
        String json = extractJson(content);
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("AI 返回结果解析失败，请稍后重试", e);
        }
    }

    private String extractJson(String content) {
        if (StrUtil.isBlank(content)) {
            throw new IllegalStateException("AI 未返回有效内容，请稍后重试");
        }

        String trimmedContent = content.trim();
        int startIndex = trimmedContent.indexOf('{');
        int endIndex = trimmedContent.lastIndexOf('}');
        if (startIndex < 0 || endIndex <= startIndex) {
            throw new IllegalStateException("AI 返回结果格式不正确，请稍后重试");
        }
        return trimmedContent.substring(startIndex, endIndex + 1);
    }

    private WorkOrderEnhanceResponse sanitizeWorkOrderResponse(WorkOrderEnhanceResponse response,
                                                               WorkOrderEnhanceRequest request) {
        WorkOrderEnhanceResponse sanitized = new WorkOrderEnhanceResponse();
        sanitized.setTitle(resolveTitle(response == null ? null : response.getTitle(), request.getTitle(), request.getContent(), "工单信息待完善"));
        sanitized.setContent(resolveContent(response == null ? null : response.getContent(), request.getContent(), request.getTitle(), "请补充问题现象、发生位置和方便处理时间。"));
        sanitized.setOrderType(resolveWorkOrderType(response == null ? null : response.getOrderType(), request.getOrderType()));
        sanitized.setPriority(resolvePriority(response == null ? null : response.getPriority(), request.getPriority()));
        return sanitized;
    }

    private SecurityHazardEnhanceResponse sanitizeSecurityHazardResponse(SecurityHazardEnhanceResponse response,
                                                                         SecurityHazardEnhanceRequest request) {
        SecurityHazardEnhanceResponse sanitized = new SecurityHazardEnhanceResponse();
        sanitized.setTitle(resolveTitle(response == null ? null : response.getTitle(), request.getTitle(), request.getContent(), "安全隐患信息待完善"));
        sanitized.setContent(resolveContent(response == null ? null : response.getContent(), request.getContent(), request.getTitle(), "请补充隐患现象、影响范围和现场情况。"));
        sanitized.setHazardType(resolveHazardType(response == null ? null : response.getHazardType(), request.getHazardType()));
        sanitized.setLocation(resolveLocation(response == null ? null : response.getLocation(), request.getLocation()));
        return sanitized;
    }

    private NeighborHelpEnhanceResponse sanitizeNeighborHelpResponse(NeighborHelpEnhanceResponse response,
                                                                     NeighborHelpEnhanceRequest request) {
        NeighborHelpEnhanceResponse sanitized = new NeighborHelpEnhanceResponse();
        sanitized.setTitle(resolveTitle(response == null ? null : response.getTitle(), request.getTitle(), request.getContent(), "邻里互助信息待完善"));
        sanitized.setContent(resolveContent(response == null ? null : response.getContent(), request.getContent(), request.getTitle(), "请补充互助内容、时间要求和联系方式。"));
        sanitized.setHelpType(resolveNeighborHelpType(response == null ? null : response.getHelpType(), request.getHelpType()));
        sanitized.setCategory(resolveNeighborHelpCategory(response == null ? null : response.getCategory(), request.getCategory()));
        return sanitized;
    }

    private ServiceAppointmentEnhanceResponse sanitizeServiceAppointmentResponse(ServiceAppointmentEnhanceResponse response,
                                                                                 ServiceAppointmentEnhanceRequest request) {
        ServiceAppointmentEnhanceResponse sanitized = new ServiceAppointmentEnhanceResponse();
        sanitized.setTitle(resolveTitle(response == null ? null : response.getTitle(), request.getTitle(), request.getContent(), "服务预约信息待完善"));
        sanitized.setContent(resolveContent(response == null ? null : response.getContent(), request.getContent(), request.getTitle(), "请补充服务诉求、上门地址、预约时间和现场情况。"));
        sanitized.setServiceType(resolveAppointmentServiceType(response == null ? null : response.getServiceType(), request.getServiceType()));
        sanitized.setRemark(resolveRemark(response == null ? null : response.getRemark(), request.getRemark()));
        return sanitized;
    }

    private String resolveTitle(String aiTitle, String originalTitle, String originalContent, String defaultTitle) {
        String title = firstNonBlank(aiTitle, originalTitle);
        if (StrUtil.isNotBlank(title)) {
            return title.trim();
        }
        if (StrUtil.isNotBlank(originalContent)) {
            String content = originalContent.trim();
            return content.length() > 15 ? content.substring(0, 15) : content;
        }
        return defaultTitle;
    }

    private String resolveContent(String aiContent, String originalContent, String originalTitle, String defaultContent) {
        String content = firstNonBlank(aiContent, originalContent, originalTitle);
        return StrUtil.isNotBlank(content) ? content.trim() : defaultContent;
    }

    private String resolveLocation(String aiLocation, String originalLocation) {
        String location = firstNonBlank(aiLocation, originalLocation);
        return StrUtil.isNotBlank(location) ? location.trim() : "[请补充具体位置]";
    }

    private String resolveWorkOrderType(String aiType, String originalType) {
        String normalizedType = normalizeEnumValue(aiType);
        if (WORK_ORDER_TYPES.contains(normalizedType)) {
            return normalizedType;
        }
        normalizedType = normalizeEnumValue(originalType);
        return WORK_ORDER_TYPES.contains(normalizedType) ? normalizedType : "OTHER";
    }

    private String resolveHazardType(String aiType, String originalType) {
        String normalizedType = normalizeEnumValue(aiType);
        if (HAZARD_TYPES.contains(normalizedType)) {
            return normalizedType;
        }
        normalizedType = normalizeEnumValue(originalType);
        return HAZARD_TYPES.contains(normalizedType) ? normalizedType : "OTHER";
    }

    private String resolveNeighborHelpType(String aiType, String originalType) {
        String normalizedType = normalizeEnumValue(aiType);
        if (NEIGHBOR_HELP_TYPES.contains(normalizedType)) {
            return normalizedType;
        }
        normalizedType = normalizeEnumValue(originalType);
        return NEIGHBOR_HELP_TYPES.contains(normalizedType) ? normalizedType : "SEEK";
    }

    private String resolveNeighborHelpCategory(String aiCategory, String originalCategory) {
        String normalizedCategory = normalizeEnumValue(aiCategory);
        if (NEIGHBOR_HELP_CATEGORIES.contains(normalizedCategory)) {
            return normalizedCategory;
        }
        normalizedCategory = normalizeEnumValue(originalCategory);
        return NEIGHBOR_HELP_CATEGORIES.contains(normalizedCategory) ? normalizedCategory : "OTHER";
    }

    private String resolveAppointmentServiceType(String aiType, String originalType) {
        String normalizedType = normalizeEnumValue(aiType);
        if (APPOINTMENT_SERVICE_TYPES.contains(normalizedType)) {
            return normalizedType;
        }
        normalizedType = normalizeEnumValue(originalType);
        return APPOINTMENT_SERVICE_TYPES.contains(normalizedType) ? normalizedType : "OTHER";
    }

    private Integer resolvePriority(Integer aiPriority, Integer originalPriority) {
        if (aiPriority != null && aiPriority >= 0 && aiPriority <= 2) {
            return aiPriority;
        }
        if (originalPriority != null && originalPriority >= 0 && originalPriority <= 2) {
            return originalPriority;
        }
        return 1;
    }

    private String resolveRemark(String aiRemark, String originalRemark) {
        String remark = firstNonBlank(aiRemark, originalRemark);
        return StrUtil.isNotBlank(remark) ? remark.trim() : "如有特殊要求，请补充方便上门时间和注意事项。";
    }

    private String normalizeEnumValue(String value) {
        return StrUtil.isBlank(value) ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private String firstNonBlank(String... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        for (String value : values) {
            if (StrUtil.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
