/**
 * 正则表达式工具类
 */
export const regexUtils = {
  /**
   * 验证字符串是否符合 "HHMMss_..._手机号" 格式
   * 
   * 规则：
   * 1. 以 "HHMMss_" 开头：6位数字 + 下划线
   * 2. 中间可以是任意字符（非贪婪匹配）
   * 3. 以 "_手机号" 结尾：下划线 + 11位数字
   * 
   * @param {string} str - 待验证的字符串
   * @returns {boolean} - 是否匹配
   */
  isValidTimePhoneFormat(str) {
    if (!str) return false;
    // ^\d{6}_       : 以6位数字+下划线开头 (HHMMss_)
    // .*?           : 中间任意字符 (非贪婪)
    // _\d{11}$      : 以下划线+11位数字结尾 (_手机号)
    const regex = /^\d{6}_.*?_\d{11}$/;
    return regex.test(str);
  },

  /**
   * 提取符合该格式的手机号
   * @param {string} str 
   * @returns {string|null} 返回手机号或 null
   */
  extractPhoneFromFormat(str) {
    if (!this.isValidTimePhoneFormat(str)) return null;
    return str.split('_').pop();
  },

  /**
   * 提取符合该格式的时间字符串 (HHMMss)
   * @param {string} str 
   * @returns {string|null} 返回时间字符串或 null
   */
  extractTimeFromFormat(str) {
    if (!this.isValidTimePhoneFormat(str)) return null;
    return str.substring(0, 6);
  }
};

export default regexUtils;
