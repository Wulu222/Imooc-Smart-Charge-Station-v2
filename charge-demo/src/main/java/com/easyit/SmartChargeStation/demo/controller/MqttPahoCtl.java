package com.easyit.SmartChargeStation.demo.controller;

import com.easyit.SmartChargeStation.demo.mqtt.PahoMode.service.impl.ClientServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * author: EasyIT
 * description: mqtt(基于Eclipse Paho) web控制器
 * date: 2024
 */

@RestController
public class MqttPahoCtl {

    @Resource
    private ClientServiceImpl clientService;


    //mqtt客户端发送消息

    @RequestMapping(value = "/paho/pub")
    public void pahoPub(
            @RequestParam String topic,
            @RequestParam String message) {
        clientService.publish(topic,message);

        clientService.disconnct();
    }

    //mqtt客户端订阅主题
    @RequestMapping(value = "/paho/sub")
    public void pahoSub(@RequestParam String topic) {
        clientService.subScribe(topic);
        clientService.disconnct();
    }

}
