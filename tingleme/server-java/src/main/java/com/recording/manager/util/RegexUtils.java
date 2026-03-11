package com.recording.manager.util;

import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * 提供常用的格式校验功能
 */
public class RegexUtils {

    /**
     * 邮箱正则
     */
    public static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";

    /**
     * 手机号正则 (中国大陆)
     * 支持 13, 14, 15, 16, 17, 18, 19 开头的 11 位号码
     */
    public static final String REGEX_MOBILE = "^1[3-9]\\d{9}$";

    /**
     * 身份证正则 (18位)
     * 简单校验：18位数字，或者17位数字+X
     */
    public static final String REGEX_ID_CARD_18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";

    /**
     * URL正则
     */
    public static final String REGEX_URL = "[a-zA-z]+://[^\\s]*";

    /**
     * 纯数字正则
     */
    public static final String REGEX_DIGIT = "^[0-9]+$";

    /**
     * 中文正则
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5]+$";

    /**
     * 自定义格式正则：以时间(HHMMss_)开头，以(_手机号.扩展名)结尾
     * 开头：([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9]_  (匹配 000000_ 到 235959_)
     * 中间：.*
     * 结尾：_\d+\..+ (匹配 _数字串.扩展名，兼容任意长度的手机号/工号)
     */
    public static final String REGEX_TIME_PREFIX_MOBILE_SUFFIX = "^([0-1][0-9]|2[0-3])[0-5][0-9][0-5][0-9]_.*_\\d+\\..+$";

    /**
     * 预编译正则模式，提高性能
     */
    private static final Pattern PATTERN_EMAIL = Pattern.compile(REGEX_EMAIL);
    private static final Pattern PATTERN_MOBILE = Pattern.compile(REGEX_MOBILE);
    private static final Pattern PATTERN_ID_CARD_18 = Pattern.compile(REGEX_ID_CARD_18);
    private static final Pattern PATTERN_URL = Pattern.compile(REGEX_URL);
    private static final Pattern PATTERN_DIGIT = Pattern.compile(REGEX_DIGIT);
    private static final Pattern PATTERN_CHINESE = Pattern.compile(REGEX_CHINESE);
    private static final Pattern PATTERN_TIME_PREFIX_MOBILE_SUFFIX = Pattern.compile(REGEX_TIME_PREFIX_MOBILE_SUFFIX);

    private RegexUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 校验邮箱
     *
     * @param email 待校验的邮箱
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isEmail(CharSequence email) {
        return isMatch(PATTERN_EMAIL, email);
    }

    /**
     * 校验手机号
     *
     * @param mobile 待校验的手机号
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isMobile(CharSequence mobile) {
        return isMatch(PATTERN_MOBILE, mobile);
    }

    /**
     * 校验身份证 (18位)
     *
     * @param idCard 待校验的身份证
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isIdCard18(CharSequence idCard) {
        return isMatch(PATTERN_ID_CARD_18, idCard);
    }

    /**
     * 校验URL
     *
     * @param url 待校验的URL
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isUrl(CharSequence url) {
        return isMatch(PATTERN_URL, url);
    }

    /**
     * 校验是否为纯数字
     *
     * @param digit 待校验的字符串
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isDigit(CharSequence digit) {
        return isMatch(PATTERN_DIGIT, digit);
    }

    /**
     * 校验是否为中文
     *
     * @param chinese 待校验的字符串
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isChinese(CharSequence chinese) {
        return isMatch(PATTERN_CHINESE, chinese);
    }

    /**
     * 校验是否符合特定格式：HHMMss_..._手机号
     * <p>例如：123059_用户A_13800138000</p>
     *
     * @param input 待校验的字符串
     * @return {@code true}: 校验通过<br>{@code false}: 校验不通过
     */
    public static boolean isTimePrefixMobileSuffix(CharSequence input) {
        return isMatch(PATTERN_TIME_PREFIX_MOBILE_SUFFIX, input);
    }

    /**
     * 判断是否匹配正则
     *
     * @param pattern 编译后的正则模式
     * @param input   待校验的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(Pattern pattern, CharSequence input) {
        return input != null && input.length() > 0 && pattern.matcher(input).matches();
    }
    
    /**
     * 判断是否匹配正则
     *
     * @param regex 正则表达式
     * @param input 待校验的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }
}
