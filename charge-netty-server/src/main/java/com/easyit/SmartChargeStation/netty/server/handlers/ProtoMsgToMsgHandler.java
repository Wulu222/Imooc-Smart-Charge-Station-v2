package com.easyit.SmartChargeStation.netty.server.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * author: EasyIT
 * description: 消息类型转换：将WebSocketFrame转换为BinaryWebSocketFrame
 * date: 2025
 */

public class ProtoMsgToMsgHandler extends MessageToMessageDecoder<WebSocketFrame> {

    @Override
    protected void decode(
            ChannelHandlerContext channelHandlerContext,
            WebSocketFrame msg,
            //List<Object> 存放数据转换后的结果
            List<Object> list) throws Exception {

        //判断WebSocketFrame的类型是否二进制类型
        if( msg instanceof BinaryWebSocketFrame) {
            //将WebSocketFrame转换为BinaryWebSocketFrame
            ByteBuf buffer = ((BinaryWebSocketFrame) msg).content();
            //把转换后的结果存放到list
            list.add(buffer);

            //retain() 是 ByteBuf 的一个方法，用于增加引用计数，表示当前 ByteBuf 对象被引用的次数。
            //只要有一个引用，那么 ByteBuf 对象就不会被自动释放。
            buffer.retain();
        }
    }
}
