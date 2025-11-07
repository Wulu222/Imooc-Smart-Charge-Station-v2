package com.easyit.chargeweb.service;

import com.easyit.chargeweb.entity.ChargingPile;
import com.easyit.chargeweb.repository.ChargingPileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ChargingPileServiceTest {

    @Mock
    private ChargingPileRepository chargingPileRepository;

    @InjectMocks
    private ChargingPileService chargingPileService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetChargingPileById_Found() {
        // 准备测试数据
        String chargerId = "CP001";
        ChargingPile chargingPile = new ChargingPile();
        chargingPile.setId(chargerId);
        chargingPile.setLocation("测试位置");
        chargingPile.setPower(7);
        chargingPile.setStatus(0);
        chargingPile.setStatusDescription("空闲");

        // 模拟Repository层方法调用
        when(chargingPileRepository.findById(chargerId)).thenReturn(Optional.of(chargingPile));

        // 执行测试
        ChargingPile result = chargingPileService.getChargingPileById(chargerId);

        // 验证结果
        assertNotNull(result);
        assertEquals(chargerId, result.getId());
        assertEquals("测试位置", result.getLocation());

        // 验证Repository层方法调用
        verify(chargingPileRepository, times(1)).findById(chargerId);
    }

    @Test
    public void testGetChargingPileById_NotFound() {
        // 准备测试数据
        String chargerId = "CP999";

        // 模拟Repository层方法调用
        when(chargingPileRepository.findById(chargerId)).thenReturn(Optional.empty());

        // 执行测试
        ChargingPile result = chargingPileService.getChargingPileById(chargerId);

        // 验证结果
        assertNull(result);

        // 验证Repository层方法调用
        verify(chargingPileRepository, times(1)).findById(chargerId);
    }

    @Test
    public void testExists_True() {
        // 准备测试数据
        String chargerId = "CP001";

        // 模拟Repository层方法调用
        when(chargingPileRepository.existsById(chargerId)).thenReturn(true);

        // 执行测试
        boolean result = chargingPileService.exists(chargerId);

        // 验证结果
        assertTrue(result);

        // 验证Repository层方法调用
        verify(chargingPileRepository, times(1)).existsById(chargerId);
    }

    @Test
    public void testExists_False() {
        // 准备测试数据
        String chargerId = "CP999";

        // 模拟Repository层方法调用
        when(chargingPileRepository.existsById(chargerId)).thenReturn(false);

        // 执行测试
        boolean result = chargingPileService.exists(chargerId);

        // 验证结果
        assertFalse(result);

        // 验证Repository层方法调用
        verify(chargingPileRepository, times(1)).existsById(chargerId);
    }

    @Test
    public void testSaveChargingPile_WithStatusDescription() {
        // 准备测试数据
        ChargingPile chargingPile = new ChargingPile();
        chargingPile.setId("CP002");
        chargingPile.setLocation("新位置");
        chargingPile.setPower(11);
        chargingPile.setStatus(1);
        chargingPile.setStatusDescription("自定义充电中");

        // 模拟Repository层方法调用
        when(chargingPileRepository.save(chargingPile)).thenReturn(chargingPile);

        // 执行测试
        ChargingPile result = chargingPileService.saveChargingPile(chargingPile);

        // 验证结果
        assertNotNull(result);
        assertEquals("CP002", result.getId());
        assertEquals("新位置", result.getLocation());
        assertEquals("自定义充电中", result.getStatusDescription());

        // 验证Repository层方法调用
        verify(chargingPileRepository, times(1)).save(chargingPile);
    }

    @Test
    public void testSaveChargingPile_WithoutStatusDescription() {
        // 准备测试数据
        ChargingPile chargingPile = new ChargingPile();
        chargingPile.setId("CP002");
        chargingPile.setLocation("新位置");
        chargingPile.setPower(11);
        chargingPile.setStatus(0); // 0表示空闲
        // 不设置statusDescription，让系统自动设置

        // 模拟Repository层方法调用
        when(chargingPileRepository.save(any(ChargingPile.class))).thenAnswer(invocation -> {
            ChargingPile saved = invocation.getArgument(0);
            return saved;
        });

        // 执行测试
        ChargingPile result = chargingPileService.saveChargingPile(chargingPile);

        // 验证结果
        assertNotNull(result);
        assertEquals("CP002", result.getId());
        assertEquals("空闲", result.getStatusDescription()); // 验证自动设置了状态描述

        // 验证Repository层方法调用
        verify(chargingPileRepository, times(1)).save(any(ChargingPile.class));
    }

    @Test
    public void testSaveChargingPile_StatusMapping() {
        // 测试不同状态码的自动映射
        testStatusMapping(0, "空闲");
        testStatusMapping(1, "充电中");
        testStatusMapping(2, "故障");
        testStatusMapping(3, "离线");
        testStatusMapping(99, "未知"); // 无效状态码
    }

    private void testStatusMapping(Integer status, String expectedDescription) {
        // 准备测试数据
        ChargingPile chargingPile = new ChargingPile();
        chargingPile.setId("CP_TEST" + status);
        chargingPile.setLocation("测试位置");
        chargingPile.setPower(7);
        chargingPile.setStatus(status);

        // 模拟Repository层方法调用
        when(chargingPileRepository.save(any(ChargingPile.class))).thenAnswer(invocation -> {
            return invocation.getArgument(0);
        });

        // 执行测试
        ChargingPile result = chargingPileService.saveChargingPile(chargingPile);

        // 验证结果
        assertEquals(expectedDescription, result.getStatusDescription());
    }
}