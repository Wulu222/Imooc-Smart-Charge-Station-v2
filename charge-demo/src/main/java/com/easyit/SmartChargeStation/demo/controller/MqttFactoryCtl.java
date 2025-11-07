package com.easyit.SmartChargeStation.demo.controller;

import com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.service.MqttService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: EasyIT
 * description: mqtt(基于Spring Integration) web控制器
 * date: 2024
 */

@RestController
public class MqttFactoryCtl {

    @Resource
    private MqttService service;


    //mqtt客户端发送消息

    @RequestMapping(value = "/factory/pub")
    public void pahoPub(
            @RequestParam("topic") String topic,
            @RequestParam("message") String message) {

        service.send(topic,message);

    }

}
