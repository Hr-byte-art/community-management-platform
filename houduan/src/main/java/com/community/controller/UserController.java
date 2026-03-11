package com.community.controller;

import com.community.annotation.Auth;
import com.community.annotation.Log;
import com.community.common.Result;
import com.community.entity.SysUser;
import com.community.service.SysPermissionService;
import com.community.service.SysUserService;
import com.community.utils.PasswordEncoderUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private SysUserService userService;
    @Autowired
    private SysPermissionService sysPermissionService;

    @GetMapping("/info")
    public Result<?> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = userService.getUserById(userId);
        user.setPassword(null);
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("permissions", sysPermissionService.listPermissionCodesByRole(user.getRole()));
        return Result.success(data);
    }

    @Log("更新个人信息")
    @Auth(value = "", permissions = {"btn.profile.update"})
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody SysUser user, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        user.setId(userId);
        user.setPassword(null);
        user.setRole(null);
        userService.updateById(user);
        return Result.success();
    }

    @Log("修改密码")
    @Auth(value = "", permissions = {"btn.profile.password"})
    @PutMapping("/password")
    public Result<?> updatePassword(@RequestBody PasswordDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        SysUser user = userService.getUserById(userId);
        if (!PasswordEncoderUtil.matches(dto.oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        user.setPassword(PasswordEncoderUtil.encode(dto.newPassword));
        userService.updateById(user);
        return Result.success();
    }

    @Auth
    @GetMapping("/list")
    public Result<?> list(@RequestParam(defaultValue = "1") Integer pageNum,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          String username, String realName) {
        return Result.success(userService.pageQuery(pageNum, pageSize, username, realName));
    }

    @Log("新增用户")
    @Auth(permissions = {"btn.user.add"})
    @PostMapping
    public Result<?> add(@RequestBody SysUser user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        // 对密码进行加密
        user.setPassword(PasswordEncoderUtil.encode(user.getPassword()));
        user.setAvatar("https://tu.ltyuanfang.cn/api/fengjing.php");
        userService.save(user);
        return Result.success();
    }

    @Log("编辑用户")
    @Auth(permissions = {"btn.user.edit"})
    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody SysUser user) {
        user.setId(id);
        // 如果提供了密码，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordEncoderUtil.encode(user.getPassword()));
        } else {
            // 如果没提供密码，不更新密码字段
            SysUser existingUser = userService.getUserById(id);
            user.setPassword(existingUser.getPassword());
        }
        // 不能修改角色字段
        user.setRole(null);
        userService.updateById(user);
        return Result.success();
    }

    @Log("删除用户")
    @Auth(permissions = {"btn.user.delete"})
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success();
    }

    static class PasswordDTO {
        public String oldPassword;
        public String newPassword;
    }
}
