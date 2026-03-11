package com.community.ai.service.impl;

import com.community.ai.service.AIService;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class AIServiceImpl implements AIService {

    @Resource
    private ChatModel openAiChatModel;

    @Value("classpath:/prompt/FillInWorkOrderSystemPrompt.st")
    private String systemPrompt;

    @Override
    public String getAnswers(String question) {

        ChatClient chatClient = ChatClient.builder(openAiChatModel).defaultSystem(systemPrompt)
                .build();

        return chatClient.prompt()
                .user(question)
                .call()
                .content();
    }
}
