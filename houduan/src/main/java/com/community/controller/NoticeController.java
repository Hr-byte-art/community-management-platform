package com.community.controller;

import com.community.annotation.Log;
import com.community.common.Result;
import com.community.common.Constants;
import com.community.entity.Notice;
import com.community.entity.NoticeReadRecord;
import com.community.entity.SysUser;
import com.community.service.NoticeService;
import com.community.service.NoticeReadRecordService;
import com.community.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @Autowired
    private NoticeReadRecordService readRecordService;
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String title, String noticeType, Integer status) {
        return Result.success(noticeService.pageQuery(pageNum, pageSize, title, noticeType, status));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Notice notice = noticeService.getById(id);
        notice.setViewCount(notice.getViewCount() + 1);
        noticeService.updateById(notice);
        if (!readRecordService.hasRead(id, userId)) {
            NoticeReadRecord record = new NoticeReadRecord();
            record.setNoticeId(id);
            record.setUserId(userId);
            record.setReadTime(LocalDateTime.now());
            readRecordService.save(record);
        }
        return Result.success(notice);
    }

    @GetMapping("/{id}/stats")
    public Result<?> getStats(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        // 只有管理员才能查看统计数据
        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限查看统计数据");
        }
        
        Notice notice = noticeService.getById(id);
        long readCount = readRecordService.countByNoticeId(id);
        Map<String, Object> stats = new HashMap<>();
        stats.put("viewCount", notice.getViewCount());
        stats.put("readCount", readCount);
        return Result.success(stats);
    }

    @Log("发布公告")
    @PostMapping
    public Result<?> add(@RequestBody Notice notice, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        // 只有管理员可以发布公告
        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限发布公告");
        }
        
        // 如果状态为已发布，则调用发布并通知方法
        if (Constants.NoticeStatus.PUBLISHED.equals(notice.getStatus())) {
            noticeService.publishAndNotify(notice, userId);
            return Result.success();
        } else {
            // 否则作为草稿保存
            notice.setPublisherId(userId);
            notice.setViewCount(0);
            noticeService.save(notice);
            return Result.success();
        }
    }

    @Log("编辑公告")
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Notice notice, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        // 权限检查：只有管理员可以修改公告
        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限修改公告");
        }
        
        Notice originalNotice = noticeService.getById(id);
        if (originalNotice == null) {
            return Result.error("公告不存在");
        }
        
        notice.setId(id);
        
        // 如果状态变为已发布且之前未发布，则调用发布并通知方法
        if (Constants.NoticeStatus.PUBLISHED.equals(notice.getStatus())) {
            if (originalNotice.getPublishTime() == null) {
                // 这是从草稿变成已发布的操作
                noticeService.publishAndNotify(notice, userId);
                return Result.success();
            }
        }
        
        // 否则只是普通的更新
        noticeService.updateById(notice);
        return Result.success();
    }

    @Log("删除公告")
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        
        // 只有管理员可以删除公告
        if (!Constants.Role.ADMIN.equals(role)) {
            return Result.error("无权限删除公告");
        }
        
        noticeService.removeById(id);
        return Result.success();
    }
}
