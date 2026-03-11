package com.community.controller;

import com.community.annotation.Auth;
import com.community.common.Result;
import com.community.service.MessageNoticeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageNoticeController {

    private final MessageNoticeService messageNoticeService;

    public MessageNoticeController(MessageNoticeService messageNoticeService) {
        this.messageNoticeService = messageNoticeService;
    }

    @GetMapping("/my")
    public Result<?> myMessages(@RequestParam(defaultValue = "1") Integer pageNum,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                @RequestParam(required = false) Integer isRead,
                                @RequestParam(required = false) String messageType,
                                HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageNoticeService.pageByUser(userId, pageNum, pageSize, isRead, messageType));
    }

    @GetMapping("/unread/count")
    public Result<?> unreadCount(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.success(messageNoticeService.unreadCount(userId));
    }

    @Auth(value = "", permissions = {"btn.message.read"})
    @PutMapping("/{id}/read")
    public Result<?> markRead(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        if (!messageNoticeService.markRead(id, userId)) {
            return Result.error("消息不存在或无权限操作");
        }
        return Result.success();
    }

    @Auth(value = "", permissions = {"btn.message.read_all"})
    @PutMapping("/read-all")
    public Result<?> markAllRead(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        messageNoticeService.markAllRead(userId);
        return Result.success();
    }

    @Auth(permissions = {"btn.message.admin_list", "scope.message.all"})
    @GetMapping("/admin/list")
    public Result<?> adminList(@RequestParam(defaultValue = "1") Integer pageNum,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               @RequestParam(required = false) Long userId,
                               @RequestParam(required = false) Integer isRead,
                               @RequestParam(required = false) String messageType) {
        return Result.success(messageNoticeService.pageAll(pageNum, pageSize, userId, isRead, messageType));
    }
}
