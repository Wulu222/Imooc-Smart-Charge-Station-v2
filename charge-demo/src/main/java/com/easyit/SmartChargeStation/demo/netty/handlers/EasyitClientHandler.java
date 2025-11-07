package com.easyit.SmartChargeStation.demo.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * author: EasyIT
 * description: 客户端自定义处理器
 * date: 2024
 */

@Slf4j
public class EasyitClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {

    }


    /**
     * author: EasyIT
     * description: 通道准备就绪的时候触发
     * @param ctx:
     * @return void
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
       String str = "你好服务端, 这条信息发自客户端";

       /* **********************
        *
        * Unpooled创建非池化ByteBuf对象,
        * 适用快速的创建ByteBuf对象并快速销毁缓冲区的场景
        *
        * writeAndFlush()包含了两个方法：
        * 1。 write: 将ByteBuf对象写入到ChannelOutboundBuffer (缓冲区)
        * 2. flush：将ChannelOutboundBuffer的数据写入到Channel, 然后Channel才把数据发送出去
        *
        *
        * *********************/

       ctx.writeAndFlush(Unpooled.copiedBuffer(str.getBytes()));

    }
}
