package com.easyit.SmartChargeStation.demo.netty.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * author: EasyIT
 * description: 自定义服务端(入站)处理器
 * date: 2024
 */

/* **********************
 *
 * 自定义入站处理器，是不会直接实现ChannelInboundHandler，
 * 通常有2种方法：
 * 1. 继承SimpleChannelInboundHandler
 * 2. 继承ChannelInboundHandlerAdapter
 *
 * SimpleChannelInboundHandler比ChannelInboundHandlerAdapter区别
 *
 * 1. SimpleChannelInboundHandler提供了泛型, 无需进行类型转换
 * 2. 消息的释放, SimpleChannelInboundHandler自动的释放引用计数对象,而ChannelInboundHandlerAdapter不会自动释放
 *
 *
 * *********************/

@Slf4j
public class EasyitServerHandler extends SimpleChannelInboundHandler<ByteBuf> {

    /**
     * author: EasyIT
     * description: 客户端连接成功触发
     * @param ctx:
     * @return void
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info(">>>>>SimpleChannelInboundHandler有新的客户端连接："+ctx.channel().id().asLongText());
    }

    /**
     * author: EasyIT
     * description: 接收到消息触发
     * @param ctx:
     * @param msg:
     * @return void
     */
    @Override
    protected void channelRead0(
            ChannelHandlerContext ctx,
            ByteBuf msg) throws Exception {

        /* **********************
         *
         * ChannelHandler由Context进行管理
         * Context提供了Channel,ChannlPipeline上下文信息
         *
         * *********************/

        String message = msg.toString(StandardCharsets.UTF_8);
        log.info(">>>>>SimpleChannelInboundHandler收到的消息："+message);

        /* **********************
         *
         * fireChannelRead将消息传递下一个处理器，
         * 如果不调用fireChannelRead，
         * 则消息不会传递到下一个处理器
         *
         * 对于处理入站事件
         * 如果处理器是最后一个处理器,
         * 就不要调用fireChannelRead()
         *
         * *********************/

        //将消息传递给下一个处理器
        //ctx.fireChannelRead(msg);

        /* **********************
         *
         * ByteBuf是一个引用计数对象,
         * 这个对象必须手动的释放掉
         *
         * Netty4开始对于对象的生命周期的管理使用引用计数,
         * 而不是垃圾回收器管理
         * 特别是对于ByteBuf对象,
         * ByteBuf对象使用引用计数, 去提高内存分配和内存释放的性能
         *、
         * 释放ByteBuf对象,
         * 1. 可以使用release();
         * 2. ReferenceCountUtil.release()
         *
         * ReferenceCountUtil.release()是release()的包装
         *
         *
         * 什么时候释放ByteBuf
         * 1. 原ByteBuf没有做任何的处理，只是通过fireChannelRead()传递到下一个处理器, 不需要释放
         * 2. 原ByteBuf经过处理产生了新的ByteBuf, 原来的ByteBuf要释放
         * 3. 在最后的处理器, ByteBuf要释放
         *
         * *********************/

       // msg.release();





    }



}
