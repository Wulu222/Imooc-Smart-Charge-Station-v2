package com.easyit.SmartChargeStation.demo.netty.handlers;

import com.easyit.SmartChargeStation.demo.netty.util.ConnectUtil;
import com.easyit.SmartChargeStation.demo.netty.util.ConstantUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * author: EasyIT
 * description: 掉线重连 (客户端) 处理器
 * date: 2024
 */

@Slf4j
public class EasyitClientDisconnHandler extends ChannelInboundHandlerAdapter {

    //连接工具类
    private ConnectUtil util = new ConnectUtil();


    /**
     * author: EasyIT
     * description: Channel 连接成功会触发
     * @param ctx:  
     * @return void 
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    /**
     * author: EasyIT
     * description: Channel 断开连接会触发
     * @param ctx:  
     * @return void 
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      
        //客户端执行重新连接机制

        log.info(">>>>>Netty 客户端和服务端断开了连接, 重连中...");

        //若连接不上, 单独启动一条线程不断的重新连接
        final EventLoop loop = ctx.channel().eventLoop();
        //周期的执行重新连接
        loop.schedule(new Runnable() {
            //单独启动线程
            @Override
            public void run() {
                util.connect(ConstantUtil.HOST,ConstantUtil.PORT);
            }
        },10, TimeUnit.SECONDS);
        
    }

    /**
     * author: EasyIT
     * description: 收到消息会触发
     * @return void 
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }
}
