package com.easyit.SmartChargeStation.netty.server.handlers;

import com.alibaba.fastjson2.JSON;
import com.easyit.SmartChargeStation.message.ChargingData;
import com.easyit.SmartChargeStation.message.WebSocketMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.Timer;
import java.util.TimerTask;

/**
 * author: EasyIT
 * description: 推送消息到小程序
 * date: 2025
 */

@Slf4j
public class EasyitWebSocketOutboundHandler extends ChannelOutboundHandlerAdapter {

    private Timer timer;
    
    // 使用外部导入的消息类

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

        log.info(">>>>>>创建定时任务");
        // 当处理器添加到管道时启动定时任务
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    String msg = "message from netty " + System.currentTimeMillis();
                    
                    // 创建Java对象来表示消息结构
                    ChargingData chargingData = new ChargingData(0.0, 0.0, 0.0);
                    WebSocketMessage webSocketMessage = new WebSocketMessage("syn", msg, chargingData);
                    
                    // 使用FastJSON2将对象序列化为JSON字符串
                    String jsonMsg = JSON.toJSONString(webSocketMessage);
                    
                    ctx.writeAndFlush(new TextWebSocketFrame(jsonMsg));
                    log.info(">>>>>>推送消息到小程序：" + jsonMsg);
                } catch (Exception e) {
                    log.error(">>>>>>序列化消息失败：" + e.getMessage());
                }
            }
        }, 0, 5000);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 当处理器从管道移除时停止定时任务
        if (timer != null) {
            timer.cancel();
            log.info(">>>>>>定时任务已移除");
        }
    }

}
