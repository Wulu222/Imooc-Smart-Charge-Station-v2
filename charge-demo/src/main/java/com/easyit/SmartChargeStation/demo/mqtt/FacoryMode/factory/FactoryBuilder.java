package com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.factory;

import com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.conf.MqttProps;
import com.easyit.SmartChargeStation.demo.mqtt.FacoryMode.model.MqttConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;

/**
 * author: EasyIT
 * description: MQTT 工厂类
 * date: 2024
 */


//@Configuration
/* **********************
 *
 * @EnableIntegration的作用：
 *
 * 能够扫描到@ServiceActivator注解
 *
 * *********************/
@EnableIntegration
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
                        props.getClientIdFactoryMode(),getFactory());

        log.info(">>>>>工厂模式发送消息处理器生成状态 "+messageHandler.toString());

        return messageHandler;
    }


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


    @Bean
    @ServiceActivator(inputChannel = MqttConstants.In_CHANNEL)
    public MessageHandler inHandler(){

       return new MessageHandler() {
            @Override
            public void handleMessage(Message<?> message) throws MessagingException {

                //接收消息的处理业务逻辑
                log.info(message.getPayload().toString());
            }
        };
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
                    props.getClientIdFactoryMode(),
                    getFactory(),
                    props.getTopic()
            );

        //设置转换器
        adapter.setConverter(new DefaultPahoMessageConverter());
        //设置订阅通道
        adapter.setOutputChannel(inChannel());

        log.info(">>>>>工厂模式订阅主题适配器生成状态 "+adapter.toString());

        return adapter;
    }



}
