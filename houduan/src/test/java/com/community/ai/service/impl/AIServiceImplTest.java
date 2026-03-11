package com.community.ai.service.impl;

import com.community.ai.service.AIService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AIServiceImplTest {

    @Resource
    private AIService aiService;

    @Test
    void getAnswers() {
        String answers = aiService.getAnswers("3栋楼下的那个路灯这几天都不亮，晚上黑漆漆的，老人走夜路容易摔跤，赶紧找人看看吧。");
        System.out.println(answers);
    }
}