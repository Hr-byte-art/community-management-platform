package com.community.ai.service;

import reactor.core.publisher.Flux;

public interface AIService {


    /**
     * 扩写/完善输入信息
     *
     * @param question 问题
     * @return 答案
     */
    String getAnswers(String question);
}
