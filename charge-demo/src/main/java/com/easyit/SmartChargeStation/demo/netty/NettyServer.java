package com.easyit.SmartChargeStation.demo.netty;

import com.easyit.SmartChargeStation.demo.netty.handlers.EasyitServerHeartBeatHandler;
import com.easyit.SmartChargeStation.demo.netty.handlers.EasyitServerProtobufHandler;
import com.easyit.SmartChargeStation.demo.protobuf.UserProtobuf;
import com.easyit.SmartChargeStation.demo.netty.handlers.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.timeout.IdleStateHandler;
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
 * description: Netty 服务端固定模板
 * date: 2024
 */

@Component
@Order(1)
@Slf4j
public class NettyServer implements CommandLineRunner {

    private NioEventLoopGroup boss;
    private NioEventLoopGroup worker;

    private Channel channel;

    /* **********************
     *
     * @Value不起作用：
     *
     * 1. 没有加上@Component
     * 2. 变量是static类型
     *
     * *********************/
    @Value("${Netty.server.port}")
    private int port;

//    public NettyServer(int port) {
//        this.port = port;
//    }

    /* **********************
     *
     * @PostConstruct:
     * Spring容器在实例化一个对象，会第一个执行的方法
     *
     * *********************/

//    @Async
//    @PostConstruct
//    public void init() {
//        start();
//    }


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

                //设置指定大小的接收缓冲区（TCP）
                .option(ChannelOption.SO_RCVBUF, 3)

                //将ChannelHandler添加上ChannelPipeline
                .childHandler(new ChannelInitializer<>() {
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
                         * 对于处理入站事件，
                         * 处理器的执行顺序是按照添加到ChannelPipeline的顺序执行
                         *
                         * *********************/


                        //设置数据包边界
                        ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                        pipeline

                                //发送心跳包

                                /* **********************
                                 *
                                 * IdleStateHandler： 空闲状态监测
                                 * readerIdleTime:  读操作空闲时间(读超时) 在指定的时间间隔没有在Channel读到数据， 会触发名叫READ_IDLE叫做IdleStateEvent事件
                                 * writerIdleTime: 写操作空闲时间(写超时) 在指定的时间间隔没有在Channel写入数据， 会触发名叫WRITE_IDLE叫做IdleStateEvent事件
                                 * allIdleTime: 读写操作空闲时间(读/写超时) 在指定的时间间隔没有在Channel读写数据， 会触发名叫ALL_IDLE叫做IdleStateEvent事件
                                 * unit: 时间单位
                                 *
                                 *IdleStateHandler只是做空闲状态监测，具体的业务逻辑需要另外实现
                                 *
                                 * 当发生读,写超时
                                 * IdleStateHandler会调用fireUserEventTriggered(evt)，
                                 * 将IdleStateEvent事件传递到下一个Handler
                                 *
                                 * *********************/

                                .addLast(new IdleStateHandler(60,60,60, TimeUnit.SECONDS))
                                //自定义的超时处理逻辑
                                .addLast(new EasyitServerHeartBeatHandler())


                                //===== 解决粘包半包方案1: 添加数据包边界 =====//
                                //.addLast(new DelimiterBasedFrameDecoder(1024,delimiter))

                                //===== 解决粘包半包方案2: 数据包长度固定 =====//
                                //服务端接收的数据包长度固定为11个字符
                                //.addLast(new FixedLengthFrameDecoder(11))

                                //字符串解码器
                                //.addLast(new StringDecoder())
                                //添加ChannelInboundHandlerAdapter处理器
                                //.addLast(new EasyitServerHandlerAdapter())
                                //添加SimpleChannelInboundHandler处理器
                                //.addLast(new EasyitServerHandler())

                                //===== 解决粘包半包方案3: Protobuf序列 =====//
                                //取出Protobuf消息实例的数据包的包头的包长度
                                .addLast(new ProtobufVarint32FrameDecoder())
                                //Protobuf解码器
                                .addLast(new ProtobufDecoder(UserProtobuf.User.getDefaultInstance()))
                                //Protobuf消息实例接收
                                .addLast(new EasyitServerProtobufHandler())

                                //粘包半包场景复现
                                //.addLast(new EasyitServerPkgHandler())



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

        //绑定服务器直到绑定成功为止
        ChannelFuture future = null;
        try {
            /* **********************
             *
             * bind(port).sync()是阻塞方法，
             * 但是这个阻塞只是短暂的，
             * 它只是阻塞了Netty服务端的初始化的期间
             * Netty服务端的初始化完成，这个阻塞方法就完成了
             *
             * *********************/
            future = bootstra.bind(port).sync();
            log.info(">>>>>Netty 服务器监听的端口：{}",port);
            if(future.isSuccess()) {
                log.info(">>>>>Netty 服务器启动成功");
            }
            /* **********************
             *
             * closeFuture().sync()也是阻塞方法，
             * 这个阻塞方法起到的作用：
             * 将Netty 服务端的线程设置wait状态，
             * 那么SpringBoot的主线程就不会进入到finally,
             * 执行Netty 服务端关闭的代码
             *
             * *********************/
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {

            log.info(">>>>>Netty服务端关闭....");
            //优雅关闭组件

//            if(boss != null)
//                boss.shutdownGracefully();
//            if(worker != null)
//                worker.shutdownGracefully();
//            if(channel != null)
//                channel.closeFuture();

            destory();

        }

    }

    /* **********************
     *
     * @PreDestroy的作用：
     *
     * 对象销毁之前会执行@PreDestroy所修饰的方法
     *
     * *********************/


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

    /**
     * author: EasyIT
     * description: Netty Server以另一个线程启动
     * @param args:
     * @return void
     */
    @Async
    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
