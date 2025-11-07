package com.easyit.chargeweb.controller;

import com.easyit.chargeweb.common.ResponseResult;
import com.easyit.chargeweb.entity.ChargingPile;
import com.easyit.chargeweb.service.ChargingPileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChargersControllerTest {

    @Mock
    private ChargingPileService chargingPileService;

    @InjectMocks
    private ChargersController chargersController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(chargersController).build();
    }

    @Test
    public void testGetChargerInfo_Success() throws Exception {
        // 准备测试数据
        String chargerId = "CP001";
        ChargingPile chargingPile = new ChargingPile();
        chargingPile.setId(chargerId);
        chargingPile.setLocation("测试位置");
        chargingPile.setPower(7);
        chargingPile.setStatus(0);
        chargingPile.setStatusDescription("空闲");

        // 模拟Service层方法调用
        when(chargingPileService.exists(chargerId)).thenReturn(true);
        when(chargingPileService.getChargingPileById(chargerId)).thenReturn(chargingPile);

        // 执行测试
        mockMvc.perform(get("/chargers/{id}", chargerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value(chargerId))
                .andExpect(jsonPath("$.data.location").value("测试位置"));

        // 验证Service层方法调用
        verify(chargingPileService, times(1)).exists(chargerId);
        verify(chargingPileService, times(1)).getChargingPileById(chargerId);
    }

    @Test
    public void testGetChargerInfo_NotFound() throws Exception {
        // 准备测试数据
        String chargerId = "CP999";

        // 模拟Service层方法调用
        when(chargingPileService.exists(chargerId)).thenReturn(false);

        // 执行测试
        mockMvc.perform(get("/chargers/{id}", chargerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("未找到编号为CP999的充电桩"));

        // 验证Service层方法调用
        verify(chargingPileService, times(1)).exists(chargerId);
        verify(chargingPileService, never()).getChargingPileById(chargerId);
    }

    @Test
    public void testAddCharger_Success() throws Exception {
        // 准备测试数据
        String requestBody = "{\"id\":\"CP002\",\"location\":\"新位置\",\"power\":11,\"status\":0}";
        ChargingPile savedPile = new ChargingPile();
        savedPile.setId("CP002");
        savedPile.setLocation("新位置");
        savedPile.setPower(11);
        savedPile.setStatus(0);
        savedPile.setStatusDescription("空闲");

        // 模拟Service层方法调用
        when(chargingPileService.exists("CP002")).thenReturn(false);
        when(chargingPileService.saveChargingPile(any(ChargingPile.class))).thenReturn(savedPile);

        // 执行测试
        mockMvc.perform(post("/chargers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").value("CP002"))
                .andExpect(jsonPath("$.data.location").value("新位置"));

        // 验证Service层方法调用
        verify(chargingPileService, times(1)).exists("CP002");
        verify(chargingPileService, times(1)).saveChargingPile(any(ChargingPile.class));
    }

    @Test
    public void testAddCharger_ExistingId() throws Exception {
        // 准备测试数据
        String requestBody = "{\"id\":\"CP001\",\"location\":\"新位置\",\"power\":11,\"status\":0}";

        // 模拟Service层方法调用
        when(chargingPileService.exists("CP001")).thenReturn(true);

        // 执行测试
        mockMvc.perform(post("/chargers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("编号为CP001的充电桩已存在"));

        // 验证Service层方法调用
        verify(chargingPileService, times(1)).exists("CP001");
        verify(chargingPileService, never()).saveChargingPile(any(ChargingPile.class));
    }

    @Test
    public void testAddCharger_InvalidData() throws Exception {
        // 准备测试数据 - 缺少必填字段
        String requestBody = "{\"id\":\"\",\"location\":\"\",\"power\":-5,\"status\":5}";

        // 执行测试
        mockMvc.perform(post("/chargers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(400));

        // 验证Service层方法调用
        verify(chargingPileService, never()).exists(anyString());
        verify(chargingPileService, never()).saveChargingPile(any(ChargingPile.class));
    }
}