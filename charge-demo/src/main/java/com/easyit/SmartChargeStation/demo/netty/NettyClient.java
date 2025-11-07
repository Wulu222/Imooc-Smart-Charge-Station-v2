package com.easyit.SmartChargeStation.demo.netty;

import com.easyit.SmartChargeStation.demo.netty.handlers.EasyitClientProtobufHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * author: EasyIT
 * description: Netty 客户端固定模板
 * date: 2024
 */

/* **********************
 *
 * Netty客户端引导器和服务端的不同
 *
 * 1. 客户端只需要1个NioEventLoopGroup
 *    服务端需要2个NioEventLoopGroup
 *
 * 2. 客户端的引导器是Bootstrap
 *    服务端的引导器是ServerBootstrap
 *
 * 3. 客户端的Channel类型是 NioSocketChannel
 *    服务端的Channel类型是 NioServerSocketChannel
 *
 * 4. 客户端的Channel初始化方法是handler()
 *    服务端的Channel初始化方法是childHandler()
 *
 * 5. 客户端的bind()需要传入服务端的地址和端口号
 *    服务端的bind()只需要传入服务端的端口号
 *
 *
 *
 * *********************/

@Component
@Order(2)
@Slf4j
public class NettyClient implements CommandLineRunner {


    /* **********************
     *
     * Netty客户端只需要1个NioEventLoopGroup
     *
     * *********************/
    private NioEventLoopGroup eventLoop;

    private Channel channel;

    @Value("${Netty.server.port}")
    private int port;

    @Value("${Netty.server.host}")
    private String host;

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


        //处理网络IO
        eventLoop = new NioEventLoopGroup();

        /* **********************
         *
         * Bootstrap中文名称是“引导”
         * ServerBootstrap对象起到的作用：
         * Netty整个程序的组件初始化，启动，服务器连接等等的一个引导作用，
         * ServerBootstrap相当于一条主线，把Netty的主要组件串联起来
         *
         *
         * ServerBootstrap是服务端的引导器
         * Bootstrap是客户端的引导器
         *
         * *********************/

        //Netty 引导器
        Bootstrap bootstra = new Bootstrap();
        bootstra
                //配置NioEventLoop
                .group(eventLoop)
                /* **********************
                 *
                 * SocketChannel表示
                 * 基于Socket通讯的通道
                 * 监听TCP连接
                 *
                 * *********************/

                //配置Channel
                .channel(NioSocketChannel.class)

                //将ChannelHandler添加上ChannelPipeline
                .handler(new ChannelInitializer<>() {
                    //Channel初始化
                    @Override
                    protected void initChannel(Channel channel) throws Exception {

                        /* **********************
                         *
                         * Channel初始化是伴随ChannelPipeline的初始化
                         *
                         * *********************/

                        //取出ChannelPipeline
                        ChannelPipeline pipeline = channel.pipeline();

                        /* **********************
                         *
                         * 真正进行业务逻辑处理
                         *
                         * *********************/


                        //设置数据包边界
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        pipeline
                                //解决粘包半包: 添加数据包边界
                                //.addLast(new DelimiterBasedFrameDecoder(1024,delimiter))

                                //解决粘包半包: 固定数据包长度
                                //客户端发送的数据包的长度固定为11个字符
                                //.addLast(new FixedLengthFrameDecoder(11))
                                //字符串编码器
                               // .addLast(new StringEncoder())
                                //添加处理器
                                //.addLast(new EasyitClientHandler())

                                //解决Protobuf粘包半包错误
                                //为Protobuf消息实例添加了一个包头，包头是包含了数据包的长度
                                .addLast(new ProtobufVarint32LengthFieldPrepender())
                                //Protobuf编码器
                                .addLast(new ProtobufEncoder())
                                //Protobuf消息实例
                                .addLast(new EasyitClientProtobufHandler())

                                //粘包半包场景复现处理器
                                //.addLast(new EasyitClientPkgHandler())

                        ;

                    }
                });

        /* **********************
         *
         * Netty所有操作都是异步的
         * 会返回Future对象
         * 可以利用这个对象可以实现异步操作的通知
         *
         * *********************/


        //封装连接方法
        doConnect(bootstra);


    }

    public void doConnect(Bootstrap bootstra) {

        log.info(">>>>>Netty 客户端启动中...");

        ChannelFuture future = null;
        try {
            //连接服务器直到成功为止
            future = bootstra.connect(host,port).sync();
            log.info(">>>>>Netty客户端开始监听: {}:{}<<<<<", host,port);
            //注册监听器, 监听客户端启动的时候是否连接上服务端
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()) {
                        log.info(">>>>>Netty 客户端连接成功");
                    }else {
                        log.info(">>>>>Netty 客户端和服务端断开了连接, 重连中...");
                        //若连接不上, 单独启动一条线程不断的重新连接
                        final EventLoop loop = channelFuture.channel().eventLoop();
                        //周期的执行重新连接
                        loop.schedule(new Runnable() {
                            //单独启动线程
                            @Override
                            public void run() {
                                doConnect(bootstra);
                            }
                        },10, TimeUnit.SECONDS);

                    }
                }
            });

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {

            log.info(">>>>>Netty客户端关闭....");

            //优雅关闭组件

//            if(eventLoop != null)
//                eventLoop.shutdownGracefully();
//            if(channel != null)
//                channel.closeFuture();

            destory();

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
            if(eventLoop != null)
                eventLoop.shutdownGracefully().sync();
            if(channel != null)
                channel.closeFuture().syncUninterruptibly();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * author: EasyIT
     * description: Netty Client以另一个线程启动
     * @param args:
     * @return void
     */
    @Async
    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
