package com.easyit.SmartChargeStation.demo.websocket.spring.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * author: EasyIT
 * description: 自定义处理器
 * date: 2024
 */

@Component
@Slf4j
public class ImoocHandler implements WebSocketHandler {

    /**
     * author: EasyIT
     * description: 握手之后触发
     * @param session:
     * @return void
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info(">>>>>基于Spring注解 WebSocket连接建立成功，会话ID: {}, 客户端IP: {}",
                session.getId(), session.getRemoteAddress());
    }

    /**
     * author: EasyIT
     * description: 消息业务逻辑处理
     * @param session:
     * @param message:
     * @return void
     */
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        log.info("接收到WebSocket消息，会话ID: {}, 消息大小: {}字节",
                session.getId(), message.getPayloadLength());

        //取出消息内容
        String payload = message.getPayload().toString();
        Object token = session.getAttributes().get("Token");

        log.info("处理WebSocket消息内容: {}, Token: {}", payload, token);


    }

    /**
     * author: EasyIT
     * description: 发送错误触发
     * @param session:
     * @param exception:
     * @return void
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        log.error("WebSocket传输错误，会话ID: {}, 错误信息: {}",
                session.getId(), exception.getMessage(), exception);
    }
    /**
     * author: EasyIT
     * description: 连接断开触发
     * @param session:
     * @param closeStatus:
     * @return void
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session,CloseStatus closeStatus) throws Exception {
        log.info("WebSocket连接关闭，会话ID: {}, 关闭状态: {}, 状态码: {}",
                session.getId(), closeStatus.getReason(), closeStatus.getCode());

}
    /**
     * author: EasyIT
     * description: 是否支持内容切片处理
     * @param :
     * @return boolean
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
