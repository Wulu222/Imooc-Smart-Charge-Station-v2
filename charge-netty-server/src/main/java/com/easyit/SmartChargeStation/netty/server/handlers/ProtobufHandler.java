package com.easyit.SmartChargeStation.netty.server.handlers;

import com.easyit.SmartChargeStation.iotdb.utils.OpIdGenerator;
import com.easyit.SmartChargeStation.netty.server.utils.HTTPUtils;
import com.easyit.SmartChargeStation.protocol.mqtt.message.ChargePayload;
import com.easyit.SmartChargeStation.protocol.protobuf.ChargingCmdProtobuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

/**
 * author: EasyIT
 * description: 专门处理Protobuf反序列之后的数据
 * date: 2025
 */

@Slf4j
public class ProtobufHandler extends SimpleChannelInboundHandler<ChargingCmdProtobuf.ChargingCmd> {
    @Override
    protected void channelRead0(
            ChannelHandlerContext channelHandlerContext,
            ChargingCmdProtobuf.ChargingCmd chargingCmd) throws Exception {

        log.info(">>>>>微信小程序发送的充电指令："+chargingCmd.getCmd());

        //采用OKHTTP类库去调用mqtt客户端模块的发送消息API

        String cmd = chargingCmd.getCmd();
        //取第一个字符
        char chatStr = cmd.charAt(0);
        log.info(">>>>>充电指令的第1个字符："+chatStr);
        //转换为byte
        byte cmdByte = (byte)chatStr;
        log.info(">>>>>充电指令转换为byte："+cmdByte);

        //获取充电点ID
        String piles = chargingCmd.getPiles();

        //{"cmds":"OFF","device_id":"CP001","op_id":"000000-000000-000000-000002"}

        //私有协议的实体类
        ChargePayload chargePayload =  new ChargePayload();
        //chargePayload.setCmd(cmdByte);
        //chargePayload.setCharge_id(piles);
        chargePayload.setCmds(cmd.equals("2")?"ON":"OFF");//操作指令
        chargePayload.setDevice_id(piles);//设备id
        chargePayload.setOp_id(chargingCmd.getOpid());//操作ID




        //调用charge-web项目的web的添加充电接口
        String chargingApiResult = HTTPUtils.callAddChargingApi(chargePayload);
        log.info(">>>>>调用charge-web添加充电接口结果：" + chargingApiResult);

        /* **********************
         *
         * 16进制相比于json：
         *
         * 1. 比Json更加紧凑，传输效率更高，占用带宽更少
         * 2. 比Json编解码的速度更快
         * 3. 嵌入式硬件设备内存比较小，16进制比Json占用内存更少
         * 4. 嵌入式硬件设备性能不高，16进制比Json对于设备的计算能力要求更低
         *
         *
         *  如何发送16进制报文：
         *
         * 思路：将16进制的字符串转换为字节组
         *      MQTT客户端将字节组发送出去
         *
         * *********************/

        //请求mqtt发送api
        String res = HTTPUtils.doPOST(chargePayload);
        channelHandlerContext.writeAndFlush(new TextWebSocketFrame(res));
                //.addListener(ChannelFutureListener.CLOSE);


    }
}
