package com.easyit.SmartChargeStation.mqtt.client.api;

import com.alibaba.fastjson2.JSON;
import com.easyit.SmartChargeStation.message.ChargingData;
import com.easyit.SmartChargeStation.message.WebSocketMessage;
import com.easyit.SmartChargeStation.mqtt.client.conf.MqttProps;
import com.easyit.SmartChargeStation.mqtt.client.service.MqttService;
import com.easyit.SmartChargeStation.mqtt.client.utils.TransformerUtils;
import com.easyit.SmartChargeStation.protocol.mqtt.message.ChargePayload;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: EasyIT
 * description: 发送消息API
 * date: 2024
 */

@RestController
@Slf4j
public class SendApi {

    @Resource
    private MqttProps props;

    @Resource
    private MqttService gateWay;

    /**
     * author: EasyIT
     * description: 发送MQTT消息(payload)
     * @param message:
     * @return void
     */
    @RequestMapping(path = "/mqtt/send", method = RequestMethod.POST)
    String doSend(@RequestBody ChargePayload message) {

        /* **********************
         *
         * 发送16进制报文：
         *
         * 思路：将16进制的字符串转换为字节数组
         *      MQTT客户端将字节数组发送出去
         *
         *
         * 1. 将ChargePayload转换为16进制字符串
         * 2. 将16进制的字符串转换为字节数组
         *
         *
         * *********************/

        //将ChargePayload转换为16进制字符串
        String hex = TransformerUtils.objectToHex(message);
        //将16进制的字符串转换为字节数组
        byte[] bytes = TransformerUtils.hexStringToByteArray(hex);

        Message<byte[]> bytesArray =  MessageBuilder.withPayload(bytes).build();

        //MQTT发送16进制报文
        gateWay.send(props.getTopic(),bytesArray);
        //gateWay.send(props.getTopic(),message);
        //"{"cmds":"OFF","device_id":"CP001","op_id":"000000-000000-000000-000002"}"

        String msg="mqtt cmd action success !";
        // 创建Java对象来表示消息结构
        ChargingData chargingData = new ChargingData(0.0, 0.0, 0.0);
        WebSocketMessage webSocketMessage = new WebSocketMessage("syn", msg, chargingData);
        // 使用FastJSON2将对象序列化为JSON字符串
        return JSON.toJSONString(webSocketMessage);
    }
}
