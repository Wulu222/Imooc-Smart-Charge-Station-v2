package com.easyit.chargeweb.controller;

import com.easyit.SmartChargeStation.iotdb.utils.OpIdGenerator;
import com.easyit.chargeweb.common.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * op_id生成控制器
 * 用于生成标准格式的op_id并返回给前端
 */
@RestController
@RequestMapping("/op-id")
public class OpIdController {

    private static final Logger logger = LoggerFactory.getLogger(OpIdController.class);

    /**
     * 生成标准格式的op_id
     * @return 生成的op_id
     */
    @GetMapping("/generate")
    public ResponseResult<String> generateOpId() {
        logger.info("生成op_id请求");
        
        try {
            // 使用OpIdGenerator生成标准格式的op_id
            String opId = OpIdGenerator.generateOpId();
            logger.info("成功生成op_id: {}", opId);
            
            // 返回成功响应
            return ResponseResult.success(opId);
        } catch (Exception e) {
            logger.error("生成op_id失败", e);
            return ResponseResult.fail(500, "生成op_id失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证op_id格式是否有效
     * @param opId 要验证的op_id
     * @return 验证结果
     */
    @GetMapping("/validate")
    public ResponseResult<Boolean> validateOpId(String opId) {
        logger.info("验证op_id格式请求，op_id: {}", opId);
        
        try {
            boolean isValid = OpIdGenerator.isValidOpId(opId);
            logger.info("op_id验证结果，op_id: {}, 是否有效: {}", opId, isValid);
            
            // 返回验证结果
            return ResponseResult.success(isValid);
        } catch (Exception e) {
            logger.error("验证op_id失败", e);
            return ResponseResult.fail(500, "验证op_id失败: " + e.getMessage());
        }
    }
    
    /**
     * 生成MQTT临时op_id
     * @return MQTT临时op_id
     */
    @GetMapping("/generate-mqtt")
    public ResponseResult<String> generateMqttOpId() {
        logger.info("生成MQTT临时op_id请求");
        
        try {
            // 使用OpIdGenerator生成MQTT临时op_id
            String mqttOpId = OpIdGenerator.generateMqttOpId();
            logger.info("成功生成MQTT临时op_id: {}", mqttOpId);
            
            // 返回成功响应
            return ResponseResult.success(mqttOpId);
        } catch (Exception e) {
            logger.error("生成MQTT临时op_id失败", e);
            return ResponseResult.fail(500, "生成MQTT临时op_id失败: " + e.getMessage());
        }
    }
}