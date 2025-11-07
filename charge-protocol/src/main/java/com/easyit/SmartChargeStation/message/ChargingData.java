package com.easyit.SmartChargeStation.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 充电数据类，用于WebSocket消息中的充电数据部分
 * @author EasyIT
 * @date 2025
 */
@Data
@AllArgsConstructor
public class ChargingData {
    private double current;
    private double voltage;
    private double power;
}