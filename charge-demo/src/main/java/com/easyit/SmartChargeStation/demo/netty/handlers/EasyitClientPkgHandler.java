package com.easyit.SmartChargeStation.demo.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * author: EasyIT
 * description: 粘包半包场景复现(客户端发送)
 * date: 2024
 */
@Slf4j
public class EasyitClientPkgHandler extends SimpleChannelInboundHandler<String> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String byteBuf) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        //计数器
        int count = 0;
        //发送数据包
        for (int i = 0; i < 100; i++) {
            String str = "hello world$_";
            ++count;
            //发送
            ctx.writeAndFlush(str);
            log.info("粘包半包>>>>>第"+count+"次的发送数据包:"+str);
        }

    }
}
