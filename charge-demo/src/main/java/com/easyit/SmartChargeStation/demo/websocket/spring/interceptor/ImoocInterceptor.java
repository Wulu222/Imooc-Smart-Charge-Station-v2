package com.easyit.SmartChargeStation.demo.websocket.spring.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * author: EasyIT
 * description: 自定义拦截器
 * date: 2024
 */

@Component
@Slf4j
public class ImoocInterceptor implements HandshakeInterceptor {

    /**
     * author: EasyIT
     * description: 握手之前触发
     * @param request:
     * @param response:
     * @param wsHandler:
     * @param attributes:
     * @return boolean
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.info(">>>>>前置拦截<<<<<");
        return true;
    }

    /**
     * author: EasyIT
     * description: 握手之后触发
     * @param request:
     * @param response:
     * @param wsHandler:
     * @param exception:
     * @return void
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

        log.info(">>>>>后置拦截<<<<<");
    }
}
