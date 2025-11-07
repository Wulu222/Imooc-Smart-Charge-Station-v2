package com.easyit.SmartChargeStation.demo.mqtt.PahoMode.conf;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * author: EasyIT
 * description: MQTT 配置类
 * date: 2024
 */

@Configuration
@Data
public class MqttConf {


    @Value("${mqtt.options.user_name}")
    private String username;
    @Value("${mqtt.options.password}")
    private String password;
    @Value("${mqtt.host}")
    private String host;
    @Value("${mqtt.client_id_pahoMode}")
    private String clientId;
    @Value("${mqtt.topic}")
    private String topic;
    @Value("${mqtt.options.clean_session}")
    private String clean_session;
}
