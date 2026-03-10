package com.community.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.community.entity.NoticeSubscription;
import com.community.mapper.NoticeSubscriptionMapper;
import org.springframework.stereotype.Service;

@Service
public class NoticeSubscriptionService extends ServiceImpl<NoticeSubscriptionMapper, NoticeSubscription> {
}