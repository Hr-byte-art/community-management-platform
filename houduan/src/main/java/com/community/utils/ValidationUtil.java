package com.community.utils;

import java.util.regex.Pattern;

/**
 * 验证工具类
 */
public class ValidationUtil {
    
    // 身份证号正则表达式（18位）- 只检查位数和基本格式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^\\d{17}[0-9Xx]$");
    
    // 手机号正则表达式（11位，1开头）
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    /**
     * 验证身份证号格式
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCard(String idCard) {
        if (idCard == null || idCard.trim().isEmpty()) {
            return false;
        }
        String trimmed = idCard.trim();
        // 检查长度必须是18位
        if (trimmed.length() != 18) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(trimmed).matches();
    }
    
    /**
     * 验证手机号格式
     * @param phone 手机号
     * @return 是否有效
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }
    
    /**
     * 验证身份证号校验位
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValidIdCardChecksum(String idCard) {
        if (!isValidIdCard(idCard)) {
            return false;
        }
        
        // 权重因子
        int[] weights = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
        // 校验码
        char[] checkCodes = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
        
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += Character.getNumericValue(idCard.charAt(i)) * weights[i];
        }
        
        int mod = sum % 11;
        char expectedCheckCode = checkCodes[mod];
        char actualCheckCode = Character.toUpperCase(idCard.charAt(17));
        
        return expectedCheckCode == actualCheckCode;
    }
}