package com.easyit.SmartChargeStation.demo.controller;

import com.easyit.SmartChargeStation.demo.MqttApplication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(classes = MqttApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class MqttFactoryCtlTest {

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("测试基于Spring Integration 发送信息")
    @Test
    public void testIntegrationPub() throws Exception {
        //构造Request
        MvcResult result = mockMvc.perform(
                        post("/factory/pub")
                                //设置内容类型
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                .param("topic","io_test")
                                .param("message","hello Spring Integration from IDEA")
                )
                //断言
                .andExpect(status().isOk())
                //打印请求信息
                .andDo(print())
                .andReturn();

        //打印接口返回信息
        System.out.println(result.getResponse().getContentAsString());

    }

}