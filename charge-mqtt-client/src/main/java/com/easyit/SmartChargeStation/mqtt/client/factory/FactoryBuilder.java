package com.easyit.SmartChargeStation.mqtt.client.factory;

import com.easyit.SmartChargeStation.mqtt.client.conf.MqttProps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;

import java.util.UUID;

/**
 * author: EasyIT
 * description: MQTT 工厂类
 * date: 2024
 */


@Configuration
@Slf4j
public class FactoryBuilder {

    @Resource
    private MqttProps props;


    /**
     * author: EasyIT
     * description: 获取Mqtt客户端
     * @param :
     * @return org.springframework.integration.mqtt.core.MqttPahoClientFactory
     */
    @Bean
    public MqttPahoClientFactory getFactory() {

        /* **********************
         *
         * 为什么要使用工厂模式创建对象
         *
         * 1. 对象创建和使用分开
         * 2. 方便维护
         *
         *
         * *********************/

        DefaultMqttPahoClientFactory client =
                new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = props.getOptions();
        props.setClientId(props.getClientId() + UUID.randomUUID());
        //配置连接地址
        options.setServerURIs(new String[]{props.getHost()});

        client.setConnectionOptions(options);

        return client;

    }

    /* **********************
     *
     *
     * Spring Integration的角色
     *
     * 1. 消息的生产者()
     * 2. 消息处理器(MqttPahoMessagingHandler)
     * 3. 消息通道(MessageChannel)
     *
     * Spring Integration发送以及接收信息
     * 都需要通过消息通道
     *
     *注意：发送和接收的消息通道并不是同一个通道
     *
     *
     * *********************/


}
