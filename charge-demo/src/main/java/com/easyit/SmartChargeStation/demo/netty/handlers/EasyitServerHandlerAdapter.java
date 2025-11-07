package com.easyit.SmartChargeStation.demo.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * author: EasyIT
 * description: 自定义服务端(入站)处理器
 * date: 2024
 */

@Slf4j
public class EasyitServerHandlerAdapter extends ChannelInboundHandlerAdapter {



    /**
     * author: EasyIT
     * description: 客户端连接进来触发
     * @param ctx:
     * @return void
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>ChannelInboundHandlerAdapter有新的客户端连接："+ctx.channel().id().asLongText());
    }

    /**
     * author: EasyIT
     * description: 接收到消息触发
     * @param ctx:
     * @param msg:
     * @return void
     */
    @Override
    public void channelRead(
            ChannelHandlerContext ctx,
            Object msg) throws Exception {
        //super.channelRead(ctx, msg);

        //强制转换为ByteBuf
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(StandardCharsets.UTF_8);

        log.info(">>>>>ChannelInboundHandlerAdapter收到的消息："+message);

        //将消息传递给下一个处理器
        ctx.fireChannelRead(msg);



    }
}
