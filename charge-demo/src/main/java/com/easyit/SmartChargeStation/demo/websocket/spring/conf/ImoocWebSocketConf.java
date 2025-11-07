package com.easyit.SmartChargeStation.demo.websocket.spring.conf;

import com.easyit.SmartChargeStation.demo.websocket.spring.handler.ImoocHandler;
import com.easyit.SmartChargeStation.demo.websocket.spring.interceptor.ImoocInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * author: EasyIT
 * description: WebSocket(基于Spring注解) 配置类
 * date: 2024
 */

@Configuration
public class ImoocWebSocketConf implements WebSocketConfigurer {

    //自定义拦截器
    @Resource
    private ImoocInterceptor interceptor ;

    //自定义处理器
    @Resource
    private ImoocHandler handler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        /* **********************
         *
         * 处理器：WebSocket握手之后触发
         *
         * 拦截器：WebSocket握手之后，以及握手之前都可以触发
         *

         * *********************/


        registry
                //处理器配置
                .addHandler(handler,"/ws/server")
                //拦截器配置
                .addInterceptors(interceptor)
                //关闭跨域
                .setAllowedOrigins("*");
    }
}
