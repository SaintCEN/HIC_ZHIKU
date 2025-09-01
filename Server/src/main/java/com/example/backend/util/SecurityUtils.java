package com.example.backend.util;

import java.security.SecureRandom;
import java.util.regex.Pattern;

/**
 * 安全工具类
 */
public class SecurityUtils {
    
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    
    // 用户名格式正则表达式
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    
    /**
     * 验证密码长度（简化版本，只需要6位即可）
     * @param password 密码
     * @return 是否符合长度要求
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 6 || password.length() > 128) {
            return false;
        }
        return true;
    }
    
    /**
     * 验证用户名格式
     * @param username 用户名
     * @return 是否符合格式要求
     */
    public static boolean isUsernameValid(String username) {
        if (username == null) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 生成安全的随机字符串
     * @param length 长度
     * @return 随机字符串
     */
    public static String generateSecureRandomString(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(SECURE_RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }
    
    /**
     * 清理敏感字符串（用于日志记录）
     * @param sensitive 敏感字符串
     * @return 清理后的字符串
     */
    public static String sanitizeForLog(String sensitive) {
        if (sensitive == null || sensitive.length() <= 4) {
            return "***";
        }
        return sensitive.substring(0, 2) + "***" + sensitive.substring(sensitive.length() - 2);
    }
    
    /**
     * 验证输入是否包含潜在的XSS攻击
     * @param input 输入字符串
     * @return 是否安全
     */
    public static boolean isSafeInput(String input) {
        if (input == null) {
            return true;
        }
        
        String lowerInput = input.toLowerCase();
        String[] dangerousPatterns = {
            "<script", "</script>", "javascript:", "onload=", "onerror=", 
            "onclick=", "onmouseover=", "onfocus=", "onblur=", "onchange=",
            "eval(", "expression(", "vbscript:", "data:text/html"
        };
        
        for (String pattern : dangerousPatterns) {
            if (lowerInput.contains(pattern)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 清理HTML标签（简单实现）
     * @param input 输入字符串
     * @return 清理后的字符串
     */
    public static String sanitizeHtml(String input) {
        if (input == null) {
            return null;
        }
        
        return input.replaceAll("<[^>]*>", "")
                   .replaceAll("&lt;", "<")
                   .replaceAll("&gt;", ">")
                   .replaceAll("&amp;", "&")
                   .replaceAll("&quot;", "\"")
                   .replaceAll("&#x27;", "'")
                   .replaceAll("&#x2F;", "/");
    }
}