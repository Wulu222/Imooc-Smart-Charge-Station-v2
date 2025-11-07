package com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.conf;

import com.easyit.SmartChargeStation.demo.MqttApplication;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MqttApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MqttPropsTest {

    @Resource
    private MqttProps props;


    @DisplayName("打印@ConfigurationProperties注解的配置信息")
    @Test
    void testConfProps() {

        System.out.println(props);
    }

}