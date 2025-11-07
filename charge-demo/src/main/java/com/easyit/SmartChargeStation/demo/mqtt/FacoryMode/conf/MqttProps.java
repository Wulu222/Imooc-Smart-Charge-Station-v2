package com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.conf;

import lombok.Data;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * author: EasyIT
 * description: MQTT 配置类
 * date: 2024
 */
/* **********************
 *
 * SpringBoot2.2以上不需要@ConfigurationProperties和@Configuration一起使用，
 * SpringBoot类路径就会自动搜索到@ConfigurationProperties注解的类，
 *
 * *********************/


@ConfigurationProperties(prefix = "mqtt")
@Data
public class MqttProps {


    private String host;
    private String clientIdFactoryMode;
    private String topic;
    private MqttConnectOptions options;
}
