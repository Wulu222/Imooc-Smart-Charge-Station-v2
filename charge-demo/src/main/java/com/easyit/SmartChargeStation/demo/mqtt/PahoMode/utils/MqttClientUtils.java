package com.easyit.SmartChargeStation.demo.mqtt.PahoMode.utils;

import com.easyit.SmartChargeStation.demo.mqtt.PahoMode.conf.MqttConf;
import com.easyit.SmartChargeStation.demo.mqtt.PahoMode.service.impl.Callback;
import com.easyit.SmartChargeStation.demo.mqtt.PahoMode.service.impl.MessageListener;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * author: EasyIT
 * description: mqtt客户端工具类
 * date: 2024
 */

@Component
@Slf4j
public class MqttClientUtils {

    @Resource
    private MqttConf conf;

    /**
     * 监听器
     */
    @Resource
    private MessageListener listener;

    private MqttClient client;

    private MqttConnectOptions options;

    //@PostConstruct
    public void init(){
        createClient();
        getOptions();
        connect();
        subScribe(conf.getTopic());

    }

    private void createClient(){

        if(client == null) {
            try {
                client = new MqttClient(conf.getHost(), conf.getClientId());
                log.info(">>>>>paho模式 MqttClient生成成功<<<<<");
            } catch (MqttException e) {
                log.info(">>>>>paho模式 MqttClient生成失败<<<<<");
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * author: EasyIT
     * description: 连接
     * @param :
     * @return void
     */
    private void connect(){

        try {
            if(!client.isConnected()) {
                client.connect(options);
                //设置回调类
                client.setCallback(new Callback());
                log.info(">>>>>paho模式 mqtt连接成功<<<<<");
            }
        } catch (MqttException e) {
            log.error(">>>>>paho模式 mqtt连接失败<<<<<");
            throw new RuntimeException(e);

        }
    };

    /**
     * author: EasyIT
     * description: 发送
     * @param topic: 主题
     * @param message:  消息体
     * @return void
     */
    public void publish(String topic, String message){

        MqttMessage mqttMessage =
                new MqttMessage(message.getBytes(StandardCharsets.UTF_8));


        try {
            client.publish(topic,mqttMessage);
            log.info(">>>>>paho模式 mqtt发送消息成功 <<<<<");
        } catch (MqttException e) {
            log.info(">>>>>paho模式 mqtt发送消息失败 <<<<<");
            throw new RuntimeException(e);
        }
    };

    /**
     * author: EasyIT
     * description: 订阅
     * @param topic:  主题
     * @return void
     */
    public void subScribe(String topic){

        try {
            client.subscribe(topic,listener);
            log.info(">>>>>paho模式 mqtt订阅主题成功 <<<<<");
        } catch (MqttException e) {
            log.info(">>>>>paho模式 mqtt订阅消息失败 <<<<<");
            throw new RuntimeException(e);
        }
    };

    /**
     * author: EasyIT
     * description: 断开
     * @param :
     * @return void
     */
    public void disconnct() {

        try {
            client.disconnect();
            log.info(">>>>>paho模式 mqtt断开连接 <<<<<");
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }

    }



    /**
     * author: EasyIT
     * description: 生成mqtt配置对象
     * @param :
     * @return org.eclipse.paho.client.mqttv3.MqttConnectOptions
     */
    private void getOptions() {
        options = new MqttConnectOptions();
        options.setUserName(conf.getUsername());

       /* **********************
        *
        * 因为.setPassword()需要传参是char[],
        * 因此.toCharArray()将String转换为char[]
        *
        * *********************/

        options.setPassword(conf.getPassword().toCharArray());

    }

}