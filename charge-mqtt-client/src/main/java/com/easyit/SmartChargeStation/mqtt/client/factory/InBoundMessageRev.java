package com.easyit.SmartChargeStation.mqtt.client.factory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Component;

/**
 * author: EasyIT
 * description: 接收消息的处理
 * date: 2024
 */

@Component
@Slf4j
public class InBoundMessageRev implements MessageHandler {


    @Override
    public void handleMessage(Message<?> message) throws MessagingException {

        //接收消息的处理业务逻辑
        log.info(message.getPayload().toString());
    }
}
