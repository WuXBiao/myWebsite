package com.recording.manager.util;

import org.springframework.util.StringUtils;

/**
 * 脱敏工具类
 * 用于处理敏感信息，如手机号、身份证、姓名等
 */
public class DesensitizedUtils {

    private DesensitizedUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 中文姓名脱敏
     * <p>只显示第一个汉字，其他隐藏为2个星号<br>
     * 李 -> 李*<br>
     * 李三 -> 李*<br>
     * 这里的逻辑可根据需求调整，例如：只显示姓，名脱敏
     */
    public static String chineseName(String fullName) {
        if (!StringUtils.hasText(fullName)) {
            return "";
        }
        String name = StringUtils.trimWhitespace(fullName);
        if (name.length() == 1) {
            return "*";
        }
        if (name.length() == 2) {
            return name.substring(0, 1) + "*";
        }
        return name.substring(0, 1) + "*" + name.substring(name.length() - 1);
    }

    /**
     * 身份证脱敏
     * <p>前6位，后4位，其他用星号隐藏，每位1个星号<br>
     * 110110199001011234 -> 110110********1234
     */
    public static String idCardNum(String idCardNum) {
        if (!StringUtils.hasText(idCardNum)) {
            return "";
        }
        if (idCardNum.length() < 15) {
            return idCardNum;
        }
        return idCardNum.replaceAll("(?<=\\w{6})\\w(?=\\w{4})", "*");
    }

    /**
     * 手机号脱敏
     * <p>前3位，后4位，中间隐藏<br>
     * 13800138000 -> 138****8000
     * 123456789012 -> 123****9012
     */
    public static String mobilePhone(String num) {
        if (!StringUtils.hasText(num)) {
            return "";
        }
        // 如果长度小于7位，无法保留前3后4，直接返回原值或根据需求处理
        if (num.length() < 7) {
            return num;
        }
        
        // 保留前3位，后4位，中间用4个星号代替
        return num.substring(0, 3) + "****" + num.substring(num.length() - 4);
    }

    /**
     * 银行卡号脱敏
     * <p>前6位，后4位，其他用星号隐藏每位1个星号<br>
     * 6222600000004123 -> 622260******4123
     */
    public static String bankCard(String cardNum) {
        if (!StringUtils.hasText(cardNum)) {
            return "";
        }
        if (cardNum.length() < 9) {
            return cardNum;
        }
        return cardNum.replaceAll("(?<=\\w{6})\\w(?=\\w{4})", "*");
    }
    
    /**
     * 邮箱脱敏
     * <p>前缀仅显示第一个字母，前缀其他隐藏，@及后缀保留<br>
     * test@gmail.com -> t***@gmail.com
     */
    public static String email(String email) {
        if (!StringUtils.hasText(email)) {
            return "";
        }
        int index = email.indexOf('@');
        if (index <= 1) {
            return email;
        }
        return email.replaceAll("(?<=.).(?=[^@]*@)", "*");
    }
}
