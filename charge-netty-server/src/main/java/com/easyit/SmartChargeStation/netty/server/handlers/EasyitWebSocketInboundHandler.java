package com.easyit.SmartChargeStation.netty.server.handlers;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author: EasyIT
 * description: 自定义WebSocket处理器
 * date: 2024
 */

/* **********************
 *
 * WebSocketFrame
 * 是WebSocket数据帧的格式
 *
 * *********************/

@Slf4j
public class EasyitWebSocketInboundHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    //保存连接进来的Channel
    //用于发信息给指定的用户
    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<String, Channel>();
    
    /**
     * author: EasyIT
     * description: 异常处理
     * @param ctx: 通道上下文
     * @param cause: 异常原因
     * @return void
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        SocketAddress remoteAddress = channel.remoteAddress();

        log.error(">>>>>客户端连接发生异常: 通道ID={}, 远程地址={}, 错误类型={}, 错误信息={}",
                channelId, remoteAddress, cause.getClass().getSimpleName(), cause.getMessage(), cause);

        // 关闭连接
        log.info(">>>>>关闭异常连接: 通道ID={}", channelId);
        ctx.close();
    }

    /**
     * author: EasyIT
     * description: 有新的客户端连接进来触发
     * @param ctx:
     * @return void
     */
    @Override
    public void handlerAdded (ChannelHandlerContext ctx) throws Exception {

        /* **********************
         *
         * ChannelHandhandlerAddedlerContext保存了
         * Channel以及ChannelPipeline的上下文信息
         *
         * *********************/

        //通过Context对象获取当前连接进来的Channel
        Channel channel =  ctx.channel();
        log.info(">>>>>有新的客户端连接进来: "+channel.id());
        //保存当前连接进来的Channel
        channelMap.put(channel.id().asLongText(), channel);
    }

    /**
     * author: EasyIT
     * description: 连接断开触发
     * @param ctx:
     * @return void
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();
        SocketAddress remoteAddress = channel.remoteAddress();

        //移除Channel
        channelMap.remove(channelId);
        log.info(">>>>>客户端连接断开，移除通道: 通道ID={}, 远程地址={}", channelId, remoteAddress);
        log.info(">>>>>通道映射更新 - 当前连接数: {}", channelMap.size());
    }

    /**
     * author: EasyIT
     * description: 接收到消息触发
     * @param ctx:
     * @param frame:
     * @return void
     */
    @Override
    protected void channelRead0(
            ChannelHandlerContext ctx,
            WebSocketFrame frame) throws Exception {
        Channel channel = ctx.channel();
        String channelId = channel.id().asLongText();

        /* **********************
         *
         * 1. 客户端发送过来的WebSocketFrame对象
         * 2. WebSocketFrame是一个抽象类
         * 3. 根据不同的类型，WebSocketFrame有不同的子类
         * 4. 常用的有：
         *    TextWebSocketFrame: 文本类型的WebSocket消息
         *    BinaryWebSocketFrame: 二进制类型的WebSocket消息
         *    PingWebSocketFrame: 心跳检测消息
         *    PongWebSocketFrame: 心跳响应消息
         *    CloseWebSocketFrame: 关闭连接消息
         *
         * *********************/

        //处理TextWebSocketFrame类型的消息
        if(frame instanceof TextWebSocketFrame){
            //获取消息内容
            String message = ((TextWebSocketFrame) frame).text();
            int messageLength = message != null ? message.length() : 0;

            log.info(">>>>>收到客户端消息: 通道ID={}, 消息类型=TextWebSocketFrame, 消息长度={}字符",
                    channelId, messageLength);

            // 避免记录过长消息导致日志过大
            if (messageLength > 0 && messageLength <= 200) {
                log.debug(">>>>>消息内容: {}", message);
            } else if (messageLength > 200) {
                log.debug(">>>>>消息内容过长({}字符)，仅显示前50字符: {}",
                        messageLength, message.substring(0, 50) + "...");
            }

            //回显消息
            log.info(">>>>>向客户端发送响应: 通道ID={}", channelId);
            ctx.channel().writeAndFlush(new TextWebSocketFrame("服务端已收到消息:"+message));
        } else if (frame instanceof PingWebSocketFrame) {
            log.debug(">>>>>收到心跳消息: 通道ID={}", channelId);
            // 响应心跳
            ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            log.debug(">>>>>响应心跳消息: 通道ID={}", channelId);
        } else if (frame instanceof CloseWebSocketFrame) {
            CloseWebSocketFrame closeFrame = (CloseWebSocketFrame) frame;
            int closeCode = closeFrame.statusCode();
            String reasonText = closeFrame.reasonText();
            log.info(">>>>>收到关闭连接请求: 通道ID={}, 关闭代码={}, 原因={}",
                    channelId, closeCode, reasonText);
            ctx.close();
        } else if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryFrame = (BinaryWebSocketFrame) frame;
            int dataSize = binaryFrame.content().readableBytes();
            log.info(">>>>>收到二进制消息: 通道ID={}, 数据大小={}字节", channelId, dataSize);
        } else {
            log.warn(">>>>>收到未知类型消息: 通道ID={}, 类型={}",
                    channelId, frame.getClass().getSimpleName());
        }
    }
}
