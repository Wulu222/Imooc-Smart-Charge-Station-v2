package com.easyit.SmartChargeStation.netty.server.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * author: EasyIT
 * description: Netty 服务端固定模板
 * date: 2024
 */

@Component
@Slf4j
public class WebSocketServ implements CommandLineRunner {

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    private Channel channel;

    @Value("${Netty.server.port}")
    private int port;

    /**
     * author: EasyIT
     * description: Netty启动
     * @param :
     * @return void
     */
    public void start() {

        /* **********************
         *
         *
         * Netty 对于NIO(主从Reactor模型) 的深度封装
         *
         * 1. NioEventLoop : 网络指挥官
         * 2. Channel：快递小哥
         * 3. ChannelPipeline：工作流水线
         * 4. ChannelHandler: 流水线上员工
         * 5. ByteBuf：数据容器
         *
         * *********************/

        //处理网络请求
        boss = new NioEventLoopGroup();
        //处理网络IO
        worker = new NioEventLoopGroup();

        /* **********************
         *
         * Bootstrap中文名称是“引导”
         * ServerBootstrap对象起到的作用：
         * Netty整个程序的组件初始化，启动，服务器连接等等的一个引导作用，
         * ServerBootstrap相当于一条主线，把Netty的主要组件串联起来
         *
         *
         * *********************/

        ServerBootstrap bootstra = new ServerBootstrap();
        bootstra
                //配置NioEventLoop
                .group(boss,worker)
                /* **********************
                 *
                 * SocketChannel表示
                 * 基于Socket通讯的通道
                 * 监听TCP连接
                 *
                 * *********************/

                //配置Channel
                .channel(NioServerSocketChannel.class)
                //将ChannelHandler添加上ChannelPipeline
                .childHandler(new EasyitChannelInit());

        /* **********************
         *
         * Netty所有操作都是异步的
         * 会返回Future对象
         * 可以利用这个对象可以实现异步操作的通知
         *
         * *********************/

        //绑定服务器直到绑定成功为止
        ChannelFuture future = null;
        try {
            future = bootstra.bind(port).sync();
            log.info(">>>>>Netty (WebSocket) 开始监听端口: {}<<<<<", port);

            if(future.isSuccess()) {
                log.info(">>>>>Netty (WebSocket) 服务器启动成功");
            }

            //服务器等待通道关闭，因为使用sync()，所以关闭操作也会被阻塞
            future.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {

            //优雅关闭组件

            if(boss != null)
                boss.shutdownGracefully();
            if(worker != null)
                worker.shutdownGracefully();
            if(channel != null)
                channel.closeFuture();

        }


    }


    /**
     * author: EasyIT
     * description: Netty 关闭
     * @param :
     * @return void
     */
    @PreDestroy
    public void destory() {

        try {
            if(boss != null)
                boss.shutdownGracefully().sync();
            if(worker != null)
                worker.shutdownGracefully().sync();
            if(channel != null)
                channel.closeFuture().syncUninterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
