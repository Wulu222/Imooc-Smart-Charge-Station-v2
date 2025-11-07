package com.easyit.SmartChargeStation.iotdb.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.session.pool.SessionPool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * author: EasyIT
 * description: iotdb Session配置类
 * date: 2024
 */

@Configuration
@Slf4j
public class IotDBSessionConf {

    @Value("${iotdb.username}")
    private String username;

    @Value("${iotdb.password}")
    private String password;

    @Value("${iotdb.ip}")
    private String ip;

    @Value("${iotdb.port}")
    private int port;

    @Value("${iotdb.maxSize}")
    private int maxSize;
    
    @Value("${iotdb.connectionTimeoutInMs:60000}")
    private long connectionTimeoutInMs;
    
    @Value("${iotdb.retryIntervalInMs:1000}")
    private long retryIntervalInMs;

    //Session线程池
    private static SessionPool pool;

    @Bean
    public SessionPool initSessionPool() {

        if(pool==null) {

            log.info(">>>>>SessionPool初始化....");

            pool = new SessionPool.Builder()
                    .user(username)
                    .password(password)
                    .host(ip)
                    .port(port)
                    .maxSize(maxSize)
                    .connectionTimeoutInMs((int) connectionTimeoutInMs)
                    .retryIntervalInMs((int) retryIntervalInMs)
                    .build();

                    
            log.info("SessionPool初始化成功 - IoTDB 1.3.2 配置: 主机={}:{}, 连接池大小={}", ip, port, maxSize);
        }


        return pool;

    }
}
