package com.easyit.SmartChargeStation.mqtt.client.utils;

import com.alibaba.fastjson2.JSON;

import java.nio.charset.StandardCharsets;

/**
 * author: EasyIT
 * description: 转换工具类
 * date: 2025
 */

public class TransformerUtils {

    /**
     * author: EasyIT
     * description: 将Java对象转换为16进制字符串
     * @param obj:
     * @return java.lang.String
     */
    public static String objectToHex(Object obj) {

        //将Java对象转换Json字符串
        String json = JSON.toJSONString(obj);
        //将Json字符串转换字节数组
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        //将字节数组转换为16进制字符串
        return bytesToHex(bytes);

    }

    /**
     * author: EasyIT
     * description: 将16进制字符串转换为字节数组 (参考DeepSeek)
     * @param hex:
     * @return byte[]
     */
    public static byte[] hexStringToByteArray(String hex) {

        // 预处理：移除所有空白字符‌:ml-citation{ref="1,2" data="citationList"}
        String trimmed = hex.replaceAll("\\s+", "");

        // 处理奇数长度情况：首部补零‌:ml-citation{ref="2,7" data="citationList"}
        if (trimmed.length() % 2 != 0) {
            trimmed = "0" + trimmed;
        }

        // 验证有效性：检测非法字符‌:ml-citation{ref="3" data="citationList"}
        if (!trimmed.matches("^[0-9a-fA-F]+$")) {
            throw new IllegalArgumentException("包含非十六进制字符");
        }

        byte[] result = new byte[trimmed.length() / 2];
        for (int i = 0; i < result.length; i++) {
            // 逐字符解析代替substring，减少对象创建‌:ml-citation{ref="2,3" data="citationList"}
            int high = Character.digit(trimmed.charAt(2*i), 16);   // 高位字符转换
            int low = Character.digit(trimmed.charAt(2*i+1), 16); // 低位字符转换

            // 带符号处理：通过位运算组合字节‌:ml-citation{ref="3,4" data="citationList"}
            result[i] = (byte) ((high << 4) + low);
        }
        return result;
    }

    /**
     * author: EasyIT
     * description: 将字节数组转换为16进制字符串
     * @param bytes:
     * @return java.lang.String
     */
    public static String bytesToHex(byte[] bytes) {

        StringBuilder str = new StringBuilder();

        //取出字节数组的每个字节
        for(byte b : bytes) {
            //通过"%02x"将字节格式化16进制
            //str.append对每个字节组装成字符串
           str.append(String.format("%02x",b));
        }

        return str.toString();
    }

}
