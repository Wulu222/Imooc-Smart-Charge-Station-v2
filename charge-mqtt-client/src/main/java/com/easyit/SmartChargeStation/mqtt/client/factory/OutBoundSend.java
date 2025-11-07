package com.easyit.SmartChargeStation.mqtt.client.factory;

import com.easyit.SmartChargeStation.mqtt.client.conf.MqttProps;
import com.easyit.SmartChargeStation.mqtt.client.model.MqttConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.messaging.MessageChannel;

/**
 * author: EasyIT
 * description: 发送消息
 * date: 2024
 */

@Configuration
/* **********************
 *
 * @EnableIntegration的作用：
 *
 * 能够扫描到@ServiceActivator注解
 *
 * *********************/
@EnableIntegration
@Slf4j
public class OutBoundSend {

    @Resource
    private MqttProps props;

    @Resource
    private MqttPahoClientFactory factory;





    //=============== 信息通道 =================//


    /**
     * author: EasyIT
     * description: 发送通道
     * @param :
     * @return org.springframework.messaging.MessageChannel
     */
    @Bean(name = MqttConstants.OUT_CHANNEL)
    public MessageChannel outChannel() {

        return new DirectChannel();
    }


    //=============== 发送消息 =================//

    /* **********************
     *
     *
     * Spring Integration 发送消息的步骤：
     *
     * 1. 建立发送消息通道
     * 2. 创建@MessagingGateway注解，并指定发送通道
     * 3. @MessagingGateway会拦截发送的消息,
     *    并将消息投放到指定的发送消息通道
     * 4. 创建消息处理器
     * 5. @ServiceActivator将消息处理器投放到指定的消息通道
     *
     * *********************/


    @Bean
    /* **********************
     *
     * @ServiceActivator的作用：
     * 将消息处理器投放到指定的发送消息通道
     *
     * *********************/
    @ServiceActivator(inputChannel = MqttConstants.OUT_CHANNEL)
    public MqttPahoMessageHandler outHandler() {

        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(
                        //注意这里和视频有所改动
                        props.getClientId(),
                        factory);

        log.info(">>>>>工厂模式发送消息处理器生成状态 "+messageHandler.toString());

        return messageHandler;
    }


}
