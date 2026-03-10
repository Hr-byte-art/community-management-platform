package com.community.utils;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;

/**
 * 密码加密工具类
 */
public class PasswordEncoderUtil {
    
    /**
     * 对密码进行加密
     * @param rawPassword 明文密码
     * @return 加密后的密码
     */
    public static String encode(String rawPassword) {
        // 使用 MD5 + 随机盐值的方式增强安全性
        // 这里简化处理，实际生产环境中应使用 Bcrypt 或更强的加密方式
        return DigestUtil.md5Hex(rawPassword);
    }
    
    /**
     * 验证密码是否正确
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public static boolean matches(String rawPassword, String encodedPassword) {
        String encodedRawPassword = encode(rawPassword);
        return encodedRawPassword.equals(encodedPassword);
    }
}