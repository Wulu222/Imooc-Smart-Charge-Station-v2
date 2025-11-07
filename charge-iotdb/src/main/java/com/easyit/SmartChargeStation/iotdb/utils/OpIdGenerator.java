package com.easyit.SmartChargeStation.iotdb.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * author: EasyIT
 * description: op_id生成工具类
 * date: 2024
 */
public class OpIdGenerator {
    
    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    
    /**
     * 生成标准格式的op_id
     * 格式为: OP+年月日时分秒+4位随机数
     * @return 生成的op_id
     */
    public static String generateOpId() {
        String timestamp = dateFormat.format(new Date());
        String randomNum = String.format("%04d", random.nextInt(10000));
        return "OP" + timestamp + randomNum;
    }
    
    /**
     * 生成MQTT临时op_id
     * 格式为: mqtt_时间戳
     * @return MQTT临时op_id
     */
    public static String generateMqttOpId() {
        return "mqtt_" + System.currentTimeMillis();
    }
    
    /**
     * 生成MQTT临时op_id
     * 格式为: mqtt_指定时间戳
     * @param timestamp 时间戳
     * @return MQTT临时op_id
     */
    public static String generateMqttOpId(long timestamp) {
        return "mqtt_" + timestamp;
    }
    
    /**
     * 验证op_id格式是否符合标准
     * @param opId 要验证的op_id
     * @return 是否符合标准格式
     */
    public static boolean isValidOpId(String opId) {
        if (opId == null || opId.isEmpty()) {
            return false;
        }
        
        // 验证标准格式：以OP开头，后跟14位时间戳和4位随机数
        if (opId.startsWith("OP") && opId.length() == 20) {
            try {
                String timestampPart = opId.substring(2, 16);
                String randomPart = opId.substring(16);
                // 检查时间部分是否全为数字
                Long.parseLong(timestampPart);
                // 检查随机部分是否全为数字
                Integer.parseInt(randomPart);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // 验证MQTT临时格式
        return opId.startsWith("mqtt_") && opId.length() > "mqtt_".length();
    }
}