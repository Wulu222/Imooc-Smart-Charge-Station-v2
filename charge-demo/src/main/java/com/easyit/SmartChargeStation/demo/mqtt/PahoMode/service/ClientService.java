package com.easyit.SmartChargeStation.demo.mqtt.PahoMode.service;

/**
 * author: EasyIT
 * description: 业务类
 * date: 2024
 */

public interface ClientService {

    //发送
    public void publish(String topic, String message);

    //订阅
    public void subScribe(String topic);

    public void disconnct();
}
