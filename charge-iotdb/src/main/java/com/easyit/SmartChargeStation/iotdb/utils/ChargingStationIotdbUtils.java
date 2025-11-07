package com.easyit.SmartChargeStation.iotdb.utils;

import com.easyit.SmartChargeStation.iotdb.service.ChargingStationIotdbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * author: EasyIT
 * description: 充电桩IoTDB工具类，用于初始化数据和提供通用方法
 * date: 2024
 */
@Component
@Slf4j
public class ChargingStationIotdbUtils implements CommandLineRunner {

    @Autowired
    private ChargingStationIotdbService chargingStationIotdbService;

    // 所有充电桩ID列表
    private static final List<String> ALL_STATION_IDS = Arrays.asList(
            "CP001", "CP002", "CP003", "CP004"
    );

    /**
     * 初始化所有充电桩的时间序列
     */
    public void initializeAllStations() {
        log.info("开始初始化充电桩IoTDB时间序列...");
        for (String stationId : ALL_STATION_IDS) {
            boolean result = chargingStationIotdbService.createChargingStationTimeSeries(stationId);
            if (result) {
                // 初始化每个充电桩为空闲状态
                chargingStationIotdbService.insertChargingStationStatus(stationId, "0", "system");
                log.info("充电桩[{}]初始化完成", stationId);
            } else {
                log.warn("充电桩[{}]初始化失败，可能已存在", stationId);
            }
        }
        log.info("所有充电桩IoTDB时间序列初始化完成");
    }

    /**
     * 根据状态码获取状态描述
     */
    public String getStatusDescription(int status) {
        switch (status) {
            case 0: return "空闲";
            case 1: return "充电中";
            case 2: return "故障";
            default: return "未知";
        }
    }

    /**
     * 检查充电桩ID是否有效
     */
    public boolean isValidStationId(String stationId) {
        return ALL_STATION_IDS.contains(stationId);
    }

    /**
     * 获取所有充电桩ID
     */
    public List<String> getAllStationIds() {
        return new java.util.ArrayList<>(ALL_STATION_IDS);
    }

    /**
     * 应用启动时初始化充电桩数据
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            // 初始化所有充电桩的时间序列
            initializeAllStations();
        } catch (Exception e) {
            log.error("充电桩IoTDB初始化异常: {}", e.getMessage());
            // 初始化失败不应阻止应用启动，记录异常后继续
        }
    }
}