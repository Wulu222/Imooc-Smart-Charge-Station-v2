package com.easyit.SmartChargeStation.demo.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * author: EasyIT
 * description: 超时处理(服务端) 处理器
 * date: 2024
 */

public class EasyitServerHeartBeatHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        //因为userEventTriggered接收了IdleStateHandler传递过来的IdleStateEvent事件
        //所以Object evt可以强制转换为IdleStateEvent事件


        if(evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;

            switch (event.state()) {

                //读超时处理
                case READER_IDLE:
                    ctx.channel().close();
                    break;

                //写超时的处理
                case WRITER_IDLE:
                    break;

                //读或写超时的处理
                case ALL_IDLE:
                    break;

            }
        }


    }
}
