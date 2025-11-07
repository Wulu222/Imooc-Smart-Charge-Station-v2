package com.easyit.SmartChargeStation.netty.server.utils;

import com.alibaba.fastjson2.JSON;
import com.easyit.SmartChargeStation.protocol.mqtt.message.ChargePayload;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

/**
 * author: EasyIT
 * description: OKHTTP工具类
 * date: 2025
 */

@Slf4j
public class HTTPUtils {

    /**
     * author: EasyIT
     * description: post请求
     * @param body:  私有协议的实体类
     * @return void
     */
    public static String doPOST(ChargePayload body) {

        //把Java对象转换为Json对象
        String json = JSON.toJSONString(body);

       OkHttpClient client =   new OkHttpClient();

       //把媒体类型设为application/json
       MediaType mediaType = MediaType.get("application/json; charset=utf-8");
       RequestBody reqBody =  RequestBody.create(json, mediaType);

       Request request = new Request.Builder()
                        //注意host文件的配置,否则不认识mqtt域名
                           .url("http://mqtt:8080/mqtt/send")
                        //注意要Java对象转换为Json对象
                        .post(reqBody)
                        .build();

       //请求成功还是失败
       String res = isOk(client, request);
       log.info("mqtt发送Api返回信息New："+res);

       return res;

    }

    /**
     * author: EasyIT
     * description: 请求是否成功
     * @param client: OKHTTP客户端对象
     * @param request:  请求对象
     * @return void
     */
    private static String isOk(
            OkHttpClient client,
            Request request) {
        String res="";
        try {
           Response response = client.newCall(request).execute();
           if(response.isSuccessful()) {
                res = response.body().string();
               log.info("HTTP请求返回信息："+res);
           }
        } catch (IOException e) {
            log.error("HTTP请求调用失败");
            e.printStackTrace();
        }

        return res;

    }
    
    /**
     * author: EasyIT
     * description: 调用charge-web项目的添加充电接口
     * @param chargePayload: 充电数据负载
     * @return String: 接口返回结果
     */
    public static String callAddChargingApi(ChargePayload chargePayload) {
        // 把Java对象转换为Json对象
        String json = JSON.toJSONString(chargePayload);
        
        OkHttpClient client = new OkHttpClient();
        
        // 设置媒体类型为application/json
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody reqBody = RequestBody.create(json, mediaType);

        //http://localhost:7090/api/charging/add
        // 创建请求对象，调用charge-web的添加充电接口
        Request request = new Request.Builder()
                .url("http://localhost:7090/api/charging/add") // 假设charge-web运行在本地8080端口
                .post(reqBody)
                .build();
        
        // 发送请求并获取响应
        String res = isOk(client, request);
        log.info("调用charge-web添加充电接口返回：" + res);
        
        return res;
    }

}
