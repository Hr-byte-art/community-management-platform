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
import com.community.ai.util.AITraceUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Set;


@Service
public class AIServiceImpl implements AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIServiceImpl.class);

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

    @Value("classpath:/prompt/AIChatSystemPrompt.st")
    private org.springframework.core.io.Resource aiChatSystemPromptResource;

    private String workOrderTextPrompt;
    private String structuredWorkOrderPrompt;
    private String structuredHazardPrompt;
    private String structuredNeighborHelpPrompt;
    private String structuredAppointmentPrompt;
    private String aiChatSystemPrompt;

    @PostConstruct
    public void init() {
        workOrderTextPrompt = readPrompt(workOrderTextPromptResource);
        structuredWorkOrderPrompt = readPrompt(structuredWorkOrderPromptResource);
        structuredHazardPrompt = readPrompt(structuredHazardPromptResource);
        structuredNeighborHelpPrompt = readPrompt(structuredNeighborHelpPromptResource);
        structuredAppointmentPrompt = readPrompt(structuredAppointmentPromptResource);
        aiChatSystemPrompt = readPrompt(aiChatSystemPromptResource);
    }

    @Override
    public String getAnswers(String question) {
        return callModel("qa", workOrderTextPrompt, question);
    }

    @Override
    public WorkOrderEnhanceResponse enhanceWorkOrder(WorkOrderEnhanceRequest request) {
        String traceId = AITraceUtil.currentTraceId();
        long serviceStartTime = System.currentTimeMillis();
        try {
            WorkOrderEnhanceRequest safeRequest = normalizeWorkOrderRequest(request);
            String userPrompt = buildWorkOrderUserPrompt(safeRequest);
            String content = callModel("workorder", structuredWorkOrderPrompt, userPrompt);
            long parseStartTime = System.currentTimeMillis();
            WorkOrderEnhanceResponse response = parseModelResponse(content, WorkOrderEnhanceResponse.class);
            long parseCostMs = System.currentTimeMillis() - parseStartTime;
            long sanitizeStartTime = System.currentTimeMillis();
            WorkOrderEnhanceResponse sanitized = sanitizeWorkOrderResponse(response, safeRequest);
            long sanitizeCostMs = System.currentTimeMillis() - sanitizeStartTime;
            logger.info("[ai-complete][backend] traceId={} stage=service scene=workorder totalCostMs={} parseCostMs={} sanitizeCostMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    parseCostMs,
                    sanitizeCostMs);
            return sanitized;
        } catch (RuntimeException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=service scene=workorder totalCostMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    e.getMessage());
            throw e;
        }
    }

    @Override
    public SecurityHazardEnhanceResponse enhanceSecurityHazard(SecurityHazardEnhanceRequest request) {
        String traceId = AITraceUtil.currentTraceId();
        long serviceStartTime = System.currentTimeMillis();
        try {
            SecurityHazardEnhanceRequest safeRequest = normalizeSecurityHazardRequest(request);
            String userPrompt = buildSecurityHazardUserPrompt(safeRequest);
            String content = callModel("hazard", structuredHazardPrompt, userPrompt);
            long parseStartTime = System.currentTimeMillis();
            SecurityHazardEnhanceResponse response = parseModelResponse(content, SecurityHazardEnhanceResponse.class);
            long parseCostMs = System.currentTimeMillis() - parseStartTime;
            long sanitizeStartTime = System.currentTimeMillis();
            SecurityHazardEnhanceResponse sanitized = sanitizeSecurityHazardResponse(response, safeRequest);
            long sanitizeCostMs = System.currentTimeMillis() - sanitizeStartTime;
            logger.info("[ai-complete][backend] traceId={} stage=service scene=hazard totalCostMs={} parseCostMs={} sanitizeCostMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    parseCostMs,
                    sanitizeCostMs);
            return sanitized;
        } catch (RuntimeException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=service scene=hazard totalCostMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    e.getMessage());
            throw e;
        }
    }

    @Override
    public NeighborHelpEnhanceResponse enhanceNeighborHelp(NeighborHelpEnhanceRequest request) {
        String traceId = AITraceUtil.currentTraceId();
        long serviceStartTime = System.currentTimeMillis();
        try {
            NeighborHelpEnhanceRequest safeRequest = normalizeNeighborHelpRequest(request);
            String userPrompt = buildNeighborHelpUserPrompt(safeRequest);
            String content = callModel("neighborhelp", structuredNeighborHelpPrompt, userPrompt);
            long parseStartTime = System.currentTimeMillis();
            NeighborHelpEnhanceResponse response = parseModelResponse(content, NeighborHelpEnhanceResponse.class);
            long parseCostMs = System.currentTimeMillis() - parseStartTime;
            long sanitizeStartTime = System.currentTimeMillis();
            NeighborHelpEnhanceResponse sanitized = sanitizeNeighborHelpResponse(response, safeRequest);
            long sanitizeCostMs = System.currentTimeMillis() - sanitizeStartTime;
            logger.info("[ai-complete][backend] traceId={} stage=service scene=neighborhelp totalCostMs={} parseCostMs={} sanitizeCostMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    parseCostMs,
                    sanitizeCostMs);
            return sanitized;
        } catch (RuntimeException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=service scene=neighborhelp totalCostMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    e.getMessage());
            throw e;
        }
    }

    @Override
    public ServiceAppointmentEnhanceResponse enhanceServiceAppointment(ServiceAppointmentEnhanceRequest request) {
        String traceId = AITraceUtil.currentTraceId();
        long serviceStartTime = System.currentTimeMillis();
        try {
            ServiceAppointmentEnhanceRequest safeRequest = normalizeServiceAppointmentRequest(request);
            String userPrompt = buildAppointmentUserPrompt(safeRequest);
            String content = callModel("appointment", structuredAppointmentPrompt, userPrompt);
            long parseStartTime = System.currentTimeMillis();
            ServiceAppointmentEnhanceResponse response = parseModelResponse(content, ServiceAppointmentEnhanceResponse.class);
            long parseCostMs = System.currentTimeMillis() - parseStartTime;
            long sanitizeStartTime = System.currentTimeMillis();
            ServiceAppointmentEnhanceResponse sanitized = sanitizeServiceAppointmentResponse(response, safeRequest);
            long sanitizeCostMs = System.currentTimeMillis() - sanitizeStartTime;
            logger.info("[ai-complete][backend] traceId={} stage=service scene=appointment totalCostMs={} parseCostMs={} sanitizeCostMs={} success=true",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    parseCostMs,
                    sanitizeCostMs);
            return sanitized;
        } catch (RuntimeException e) {
            logger.warn("[ai-complete][backend] traceId={} stage=service scene=appointment totalCostMs={} success=false message={}",
                    traceId,
                    System.currentTimeMillis() - serviceStartTime,
                    e.getMessage());
            throw e;
        }
    }

    @Override
    public Flux<String> askQuestion(String question) {
        ChatClient chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem(aiChatSystemPrompt)
                .build();
        return chatClient.prompt()
                .user(question)
                .stream()
                .content();
    }

    private String callModel(String scene, String systemPrompt, String userPrompt) {
        String traceId = AITraceUtil.currentTraceId();
        long modelStartTime = System.currentTimeMillis();
        ChatClient chatClient = ChatClient.builder(openAiChatModel)
                .defaultSystem(systemPrompt)
                .build();

        try {
            String content = chatClient.prompt()
                    .user(userPrompt)
                    .call()
                    .content();
            logger.info("[ai-complete][backend] traceId={} stage=model-call scene={} costMs={} systemPromptChars={} userPromptChars={} responseChars={}",
                    traceId,
                    scene,
                    System.currentTimeMillis() - modelStartTime,
                    AITraceUtil.safeLength(systemPrompt),
                    AITraceUtil.safeLength(userPrompt),
                    AITraceUtil.safeLength(content));
            return content;
        } catch (RuntimeException e) {
            logger.error("[ai-complete][backend] traceId={} stage=model-call scene={} costMs={} systemPromptChars={} userPromptChars={} success=false",
                    traceId,
                    scene,
                    System.currentTimeMillis() - modelStartTime,
                    AITraceUtil.safeLength(systemPrompt),
                    AITraceUtil.safeLength(userPrompt),
                    e);
            throw e;
        }
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

    private WorkOrderEnhanceRequest normalizeWorkOrderRequest(WorkOrderEnhanceRequest request) {
        WorkOrderEnhanceRequest safeRequest = request == null ? new WorkOrderEnhanceRequest() : request;
        WorkOrderEnhanceRequest normalizedRequest = new WorkOrderEnhanceRequest();
        normalizedRequest.setTitle(normalizeText(safeRequest.getTitle()));
        normalizedRequest.setContent(normalizeText(safeRequest.getContent()));
        normalizedRequest.setOrderType(normalizeEnumValue(safeRequest.getOrderType()));
        normalizedRequest.setPriority(safeRequest.getPriority());
        return normalizedRequest;
    }

    private SecurityHazardEnhanceRequest normalizeSecurityHazardRequest(SecurityHazardEnhanceRequest request) {
        SecurityHazardEnhanceRequest safeRequest = request == null ? new SecurityHazardEnhanceRequest() : request;
        SecurityHazardEnhanceRequest normalizedRequest = new SecurityHazardEnhanceRequest();
        normalizedRequest.setTitle(normalizeText(safeRequest.getTitle()));
        normalizedRequest.setContent(normalizeText(safeRequest.getContent()));
        normalizedRequest.setHazardType(normalizeEnumValue(safeRequest.getHazardType()));
        normalizedRequest.setLocation(normalizeText(safeRequest.getLocation()));
        return normalizedRequest;
    }

    private NeighborHelpEnhanceRequest normalizeNeighborHelpRequest(NeighborHelpEnhanceRequest request) {
        NeighborHelpEnhanceRequest safeRequest = request == null ? new NeighborHelpEnhanceRequest() : request;
        NeighborHelpEnhanceRequest normalizedRequest = new NeighborHelpEnhanceRequest();
        normalizedRequest.setTitle(normalizeText(safeRequest.getTitle()));
        normalizedRequest.setContent(normalizeText(safeRequest.getContent()));
        normalizedRequest.setHelpType(normalizeEnumValue(safeRequest.getHelpType()));
        normalizedRequest.setCategory(normalizeEnumValue(safeRequest.getCategory()));
        return normalizedRequest;
    }

    private ServiceAppointmentEnhanceRequest normalizeServiceAppointmentRequest(ServiceAppointmentEnhanceRequest request) {
        ServiceAppointmentEnhanceRequest safeRequest = request == null ? new ServiceAppointmentEnhanceRequest() : request;
        ServiceAppointmentEnhanceRequest normalizedRequest = new ServiceAppointmentEnhanceRequest();
        normalizedRequest.setTitle(normalizeText(safeRequest.getTitle()));
        normalizedRequest.setContent(normalizeText(safeRequest.getContent()));
        normalizedRequest.setServiceType(normalizeEnumValue(safeRequest.getServiceType()));
        normalizedRequest.setAddress(normalizeText(safeRequest.getAddress()));
        normalizedRequest.setAppointmentTime(normalizeText(safeRequest.getAppointmentTime()));
        normalizedRequest.setRemark(normalizeText(safeRequest.getRemark()));
        return normalizedRequest;
    }

    private String safeValue(String value) {
        String normalizedValue = normalizeText(value);
        return StrUtil.isBlank(normalizedValue) ? "[未填写]" : normalizedValue;
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

        String trimmedContent = stripCodeFence(content.trim());
        int startIndex = trimmedContent.indexOf('{');
        int endIndex = trimmedContent.lastIndexOf('}');
        if (startIndex < 0 || endIndex <= startIndex) {
            throw new IllegalStateException("AI 返回结果格式不正确，请稍后重试");
        }
        return trimmedContent.substring(startIndex, endIndex + 1);
    }

    private String stripCodeFence(String content) {
        String normalizedContent = content;
        normalizedContent = normalizedContent.replace("```json", "");
        normalizedContent = normalizedContent.replace("```JSON", "");
        normalizedContent = normalizedContent.replace("```", "");
        return normalizedContent.trim();
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
        String title = normalizeTitle(firstNonBlank(aiTitle, originalTitle));
        if (StrUtil.isNotBlank(title)) {
            return title;
        }
        if (StrUtil.isNotBlank(originalContent)) {
            String content = normalizeText(originalContent);
            if (StrUtil.isNotBlank(content)) {
                return normalizeTitle(content.length() > 15 ? content.substring(0, 15) : content);
            }
        }
        return defaultTitle;
    }

    private String resolveContent(String aiContent, String originalContent, String originalTitle, String defaultContent) {
        String content = firstNonBlank(aiContent, originalContent, originalTitle);
        content = normalizeText(content);
        return StrUtil.isNotBlank(content) ? content : defaultContent;
    }

    private String resolveLocation(String aiLocation, String originalLocation) {
        String location = normalizeText(firstNonBlank(aiLocation, originalLocation));
        return StrUtil.isNotBlank(location) ? location : "[请补充具体位置]";
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
        String remark = normalizeText(firstNonBlank(aiRemark, originalRemark));
        return StrUtil.isNotBlank(remark) ? remark : "如有特殊要求，请补充方便上门时间和注意事项。";
    }

    private String normalizeEnumValue(String value) {
        if (StrUtil.isBlank(value)) {
            return "";
        }
        String compactValue = value.trim()
                .replace("：", "")
                .replace(":", "")
                .replace("_", "")
                .replace("-", "")
                .replace(" ", "")
                .toUpperCase(Locale.ROOT);

        return switch (compactValue) {
            case "REPAIR", "报修", "维修", "维修服务", "设施维修", "修理" -> "REPAIR";
            case "COMPLAINT", "投诉" -> "COMPLAINT";
            case "SUGGESTION", "建议", "意见建议" -> "SUGGESTION";
            case "OTHER", "其他", "其它", "未知" -> "OTHER";
            case "FIRE", "消防", "消防隐患", "火灾", "火灾隐患" -> "FIRE";
            case "THEFT", "盗窃", "治安", "治安隐患", "防盗" -> "THEFT";
            case "TRAFFIC", "交通", "交通隐患", "道路交通", "道路隐患" -> "TRAFFIC";
            case "SEEK", "求助", "寻求帮助", "需要帮助" -> "SEEK";
            case "OFFER", "提供帮助", "可提供帮助", "我来帮忙" -> "OFFER";
            case "DAILY", "日常", "生活", "日常帮助", "生活帮助" -> "DAILY";
            case "SKILL", "技能", "技术", "专业技能" -> "SKILL";
            case "ITEM", "物品", "物资", "闲置物品" -> "ITEM";
            case "CLEAN", "保洁", "清洁", "打扫", "卫生" -> "CLEAN";
            case "MEDICAL", "医疗", "医护", "健康", "就医" -> "MEDICAL";
            default -> value.trim().toUpperCase(Locale.ROOT);
        };
    }

    private String firstNonBlank(String... values) {
        if (values == null || values.length == 0) {
            return null;
        }
        for (String value : values) {
            String normalizedValue = normalizeText(value);
            if (StrUtil.isNotBlank(normalizedValue)) {
                return normalizedValue;
            }
        }
        return null;
    }

    private String normalizeText(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        String normalizedValue = value.replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("[ \t]{2,}", " ")
                .replaceAll("\n{3,}", "\n\n")
                .trim();
        if (isPlaceholderOnly(normalizedValue)) {
            return null;
        }
        return normalizedValue;
    }

    private String normalizeTitle(String value) {
        String normalizedValue = normalizeText(value);
        if (StrUtil.isBlank(normalizedValue)) {
            return null;
        }
        normalizedValue = normalizedValue.replaceAll("[，。；;、,.!?！？]+$", "").trim();
        return normalizedValue.length() > 20 ? normalizedValue.substring(0, 20) : normalizedValue;
    }

    private boolean isPlaceholderOnly(String value) {
        return "[未填写]".equals(value)
                || "未填写".equals(value)
                || "未提供".equals(value)
                || "暂无".equals(value)
                || "NULL".equalsIgnoreCase(value)
                || "N/A".equalsIgnoreCase(value);
    }
}
