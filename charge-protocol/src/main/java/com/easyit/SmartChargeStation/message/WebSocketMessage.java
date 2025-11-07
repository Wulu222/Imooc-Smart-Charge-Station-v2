package com.easyit.SmartChargeStation.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * WebSocket响应消息类，用于定义WebSocket消息的标准格式
 * @author EasyIT
 * @date 2025
 */
@Data
@AllArgsConstructor
public class WebSocketMessage {
    private String code;
    private String status;
    private ChargingData chargingData;
}