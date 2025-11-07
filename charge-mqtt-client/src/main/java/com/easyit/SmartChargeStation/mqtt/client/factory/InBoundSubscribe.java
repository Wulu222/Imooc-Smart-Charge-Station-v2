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
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * author: EasyIT
 * description: 主题订阅
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
public class InBoundSubscribe {

    //=============== 接收消息 =================//

    /* **********************
     *
     * Spring Integration 接收消息的步骤：
     *
     * 1. 创建接收消息的信息通道
     * 2. 创建消息处理器
     * 3. @ServiceActivator将消息处理器投放到指定的消息通道
     * 4. 设置订阅主题的适配器
     *
     * *********************/


    @Resource
    private MqttProps props;

    @Resource
    private MqttPahoClientFactory factory;

    //消息处理
    @Resource
    private InBoundMessageRev messageRev;


    /**
     * author: EasyIT
     * description: 接收通道
     * @param :
     * @return org.springframework.messaging.MessageChannel
     */
    @Bean(name = MqttConstants.In_CHANNEL)
    public MessageChannel inChannel() {

        return new DirectChannel();
    }

    /**
     * author: EasyIT
     * description: 订阅主题的适配器
     * @param :
     * @return org.springframework.integration.core.MessageProducer
     */
    @Bean
    public MessageProducer getAdapter() {

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        //注意这里和视频有所改动
                        props.getClientId(),
                        factory,
                        props.getTopic()
                );

        //设置转换器
        adapter.setConverter(new DefaultPahoMessageConverter());
        //设置订阅通道
        adapter.setOutputChannel(inChannel());

        log.info(">>>>>工厂模式订阅主题适配器生成状态 "+adapter.toString());

        return adapter;
    }




    /**
     * author: EasyIT
     * description: 接收消息的处理
     * @param :
     * @return org.springframework.messaging.MessageHandler
     */
    @Bean
    @ServiceActivator(inputChannel = MqttConstants.In_CHANNEL)
    public MessageHandler inHandler(){

        return this.messageRev;

    }

}
