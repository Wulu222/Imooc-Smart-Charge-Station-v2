package com.easyit.SmartChargeStation.demo.mqtt.PahoMode.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * author: EasyIT
 * description: 回调类
 * date: 2024
 */

@Slf4j
public class Callback implements MqttCallback {

    /**
     * author: EasyIT
     * description: 连接丢失会调用
     * @param throwable:
     * @return void
     */
    @Override
    public void connectionLost(Throwable throwable) {
        log.info(">>>>>连接断开，原因：" + throwable.getMessage());
    }

    /**
     * author: EasyIT
     * description: 接收到订阅的消息后调用
     * @param topic:
     * @param mqttMessage:
     * @return void
     */
    @Override
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        log.info(">>>>>接收主题"+topic+"的消息：" + mqttMessage.toString());
    }

    /**
     * author: EasyIT
     * description: 发送消息成功调用的方法
     * @param iMqttDeliveryToken:
     * @return void
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        log.info(">>>>>消息发送是否成功："+iMqttDeliveryToken.isComplete());
    }
}
