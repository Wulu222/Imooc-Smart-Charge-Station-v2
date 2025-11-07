package com.easyit.SmartChargeStation.demo;

import com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.conf.MqttProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * author: EasyIT
 * description: Mqtt 启动类
 * date: 2024
 */

@SpringBootApplication
@EnableConfigurationProperties({MqttProps.class})
public class MqttApplication {
    public static void main(String[] args) {
        SpringApplication.run(MqttApplication.class, args);

    }
}
