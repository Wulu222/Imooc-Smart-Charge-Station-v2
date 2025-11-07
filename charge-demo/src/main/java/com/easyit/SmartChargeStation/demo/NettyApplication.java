package com.easyit.SmartChargeStation.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * author: EasyIT
 * description: Netty启动类
 * date: 2024
 */

/* **********************
 *
 * Netty的启动方式：
 *
 * 1. 使用接口CommandLineRunner
 * 2. 使用@PostConStruct (Spring容器管理)
 *
 * CommandLineRunner:
 * SpringBoot提供的作为数据的预加载，
 * 当SpringBoot启动的时候，
 * 这个接口是会跟随SpringBoot执行代码逻辑，
 * 这个接口只会执行一次
 *
 *
 *
 * *********************/

@SpringBootApplication
//启动线程池
@EnableAsync
public class NettyApplication {//implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(NettyApplication.class,args);
        System.out.println("SpringBoot Start....(验证Netty是否阻塞SpringBoot主线程序)");
    }

    /**
     * author: EasyIT
     * description: Netty启动
     * @param args:
     * @return void
     */
    //Netty Server以另一个线程启动
//    @Async
//    @Override
//    public void run(String... a~rgs) throws Exception {
//        NettyServer server = new NettyServer(1919);
//        server.start();
//    }
}
