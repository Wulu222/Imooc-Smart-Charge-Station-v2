package com.easyit.SmartChargeStation.netty.server.websocket;

import com.easyit.SmartChargeStation.netty.server.handlers.*;
import com.easyit.SmartChargeStation.protocol.protobuf.ChargingCmdProtobuf;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;


/**
 * author: EasyIT
 * description: 自定义的通道初始化
 * date: 2024
 */

@Slf4j
public class EasyitChannelInit extends ChannelInitializer<SocketChannel> {
    /**
     * author: EasyIT
     * description: 通道初始化
     * @param socketChannel:
     * @return void
     */
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        /* **********************
         *
         * Channel初始化是伴随ChannelPipeline的初始化
         *
         * *********************/

        //取出ChannelPipeline
        ChannelPipeline pipeline = socketChannel.pipeline();

        /* **********************
         *
         * 真正进行业务逻辑处理
         *
         * *********************/

        pipeline
                //超时监听
                .addLast(new IdleStateHandler(300,300,300, TimeUnit.SECONDS))
                //自定义的超时处理逻辑
                .addLast(new EasyitServerHeartBeatHandler())
                /* **********************
                 *
                 * Netty内置的处理器
                 *
                 * 1. HttpServerCodec
                 *   Codec作为结尾，既可以做编码器，也可以做解码器
                 *
                 * 2. HttpObjectAggregator
                 *
                 * 3. WebSocketServerProtocolHandler
                 *    自动的将HTTP升级为WebSocket协议
                 *    自动的完成握手过程
                 *    以及后续数据帧的编码和解码工作
                 *
                 *
                 * *********************/
                //对HTTP协议的解析
                .addLast(new HttpServerCodec())

                /* **********************
                 *
                 * HTTP分为GET和POST
                 * GET的参数放在URL
                 * POST的参数放在Body
                 *
                 * HttpServerCodec只能解析URL参数
                 * 不能解析Body参数
                 *
                 * HttpObjectAggregator能够
                 * 将HTTPMessage和HTTPContent
                 * 合并为FullHttpRequest或者FullHttpResponse
                 *
                 * *********************/

                //处理HTTP 的POST请求
                .addLast(new HttpObjectAggregator(65536))

                //对WebSocket协议解析
                .addLast(new WebSocketServerProtocolHandler(
                        "/ws"))

                /* **********************
                 *
                 * MessageToMessageDecoder
                 * MessageToMessageEncoder
                 *
                 * 消息类型的转换
                 *
                 * SimpleChannelInboundHandler
                 * 消息内容的业务处理
                 *
                 *
                 * *********************/

                /* **********************
                 *
                 * 为什么要添加消息类型的转换处理器 ?
                 *
                 * 思路：
                 *
                 * 通过消息类型的转换处理器将
                 * WebSocketFrame 转换为 BinaryWebSocketFrame，
                 * 将BinaryWebSocketFrame流转到下一个处理器，
                 * 也就是Protobuf处理器，去进行Protobuf序列数据的处理，
                 * Protobuf序列数据的处理完成之后的数据，
                 * 再流转到下一个处理器，也就是自定义的业务逻辑的处理器
                 *
                 *
                 * *********************/

                //消息类型转换：将WebSocketFrame转换为BinaryWebSocketFrame
                .addLast(new ProtoMsgToMsgHandler())

                /* **********************
                 *
                 * MessageLite：轻量级的Message
                 * Message
                 *
                 * 是com.google.protobuf的两个类
                 * 为protobuf的操作提供了API
                 *
                 * 经过ProtobufDecoder对Protobuf反序列化之后
                 * 返回是Java对象
                 *
                 * *********************/

                //(接收数据) Protobuf反序列化
                .addLast(new ProtobufDecoder(ChargingCmdProtobuf.ChargingCmd.getDefaultInstance()))

                //(发送数据) Protobuf序列化
                //.addLast(new ProtobufEncoder())

                /* **********************
                 *
                 * 自定义处理器
                 *
                 * *********************/

                //添加处理器
                .addLast(new EasyitWebSocketInboundHandler())

                //对Protobuf反序列化进行业务处理
                .addLast(new ProtobufHandler())

                //给小程序推送WebSocket消息
                .addLast(new EasyitWebSocketOutboundHandler())
        ;
    }
}
