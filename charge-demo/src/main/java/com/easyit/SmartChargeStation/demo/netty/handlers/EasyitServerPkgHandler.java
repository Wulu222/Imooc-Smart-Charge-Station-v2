package com.easyit.SmartChargeStation.demo.netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * author: EasyIT
 * description: 粘包半包场景复现(服务端接收)
 * date: 2024
 */

@Slf4j
public class EasyitServerPkgHandler extends SimpleChannelInboundHandler<String> {

    //计数器
    private int count = 0;

    @Override
    protected void channelRead0(
            ChannelHandlerContext channelHandlerContext,
            String msg) throws Exception {


        ++count;
        log.info("粘包半包>>>>>已接收第"+count+"个数据包:"+msg);

    }
}
