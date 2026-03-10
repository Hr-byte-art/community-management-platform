package com.community.controller;

import com.community.annotation.Log;
import com.community.common.Result;
import com.community.common.Constants;
import com.community.entity.SysUser;
import com.community.service.SysUserService;
import com.community.utils.JwtUtil;
import com.community.utils.PasswordEncoderUtil;
import com.community.utils.ValidationUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private SysUserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @Log("用户登录")
    @PostMapping("/login")
    public Result<?> login(@RequestBody LoginDTO dto) {
        SysUser user = userService.findByUsername(dto.getUsername());
        if (user == null || !PasswordEncoderUtil.matches(dto.getPassword(), user.getPassword())) {
            return Result.error("用户名或密码错误");
        }
        if (Constants.UserStatus.DISABLED.equals(user.getStatus())) {
            return Result.error("账号已被禁用");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);
        return Result.success(data);
    }

    @Log("用户注册")
    @PostMapping("/register")
    public Result<?> register(@RequestBody SysUser user) {
        if (userService.findByUsername(user.getUsername()) != null) {
            return Result.error("用户名已存在");
        }
        // 验证手机号格式
        if (!ValidationUtil.isValidPhone(user.getPhone())) {
            return Result.error("手机号格式不正确");
        }
        // 检查手机号是否已存在
        if (userService.existsByPhone(user.getPhone(), null)) {
            return Result.error("手机号已存在");
        }
        // 对密码进行加密
        user.setPassword(PasswordEncoderUtil.encode(user.getPassword()));
        user.setRole(Constants.Role.USER); // 使用常量
        user.setStatus(Constants.UserStatus.ENABLED); // 使用常量
        user.setAvatar("https://tu.ltyuanfang.cn/api/fengjing.php");
        userService.save(user);
        return Result.success();
    }

    @Data
    static class LoginDTO {
        private String username;
        private String password;
    }
}
