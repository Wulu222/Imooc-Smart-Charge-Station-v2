package com.easyit.SmartChargeStation.protocol.mqtt.message;

import lombok.Data;

/**
 * author: EasyIT
 * description: 自定义私有协议 MQTT有效负载 (payload)
 * date: 2024
 */
//{"cmd":"OFF","device_id":"CP001","op_id":"000000-000000-000000-000002"}
@Data
public class ChargePayload {

    //开始标志 : 占1个字节
    private final static byte start_tag=0x76;

    //协议版本: 占1个字节
    private byte version;

    //充电桩ID : 占4个字节
    //private short charge_id;
    private String charge_id;

    //充电桩ID : 占4个字节
    private String device_id;

    //消息类型：占1个字节 1=操作指令消息，2=充电状态消息
    private byte type;

    //充电状态数据长度 : 占2个字节
    private short charge_stat_data_len;

    //充电状态数据 : 最多500个字节
    private byte[] charge_stat_data;

    //操作指令: 占1个字节
    private byte cmd;

    private String cmds;

    //操作ID 每次充电开始更新此字段
    private String op_id;

    //校验码: 占1个字节
    private byte checksum;

}
