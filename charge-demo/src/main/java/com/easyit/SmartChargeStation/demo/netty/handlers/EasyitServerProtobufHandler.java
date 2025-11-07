package com.easyit.SmartChargeStation.demo.netty.handlers;

import com.easyit.SmartChargeStation.demo.protobuf.UserProtobuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * author: EasyIT
 * description: Protobuf (服务端) 处理器
 * date: 2024
 */

@Slf4j
public class EasyitServerProtobufHandler extends SimpleChannelInboundHandler<UserProtobuf.User> {

    //计数器
    private int count =0;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, UserProtobuf.User user) throws Exception {

        ++count;
        log.info("粘包半包>>>>>已接收第"+count+"个数据包:"+user.getName());

    }
}
