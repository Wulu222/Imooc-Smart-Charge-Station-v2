package com.easyit.SmartChargeStation.iotdb.controller;

import com.easyit.SmartChargeStation.iotdb.service.ChargingStationIotdbService;
import com.easyit.SmartChargeStation.iotdb.service.impl.ChargingStationIotdbServiceImpl;
import com.easyit.SmartChargeStation.iotdb.utils.ChargingStationIotdbUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.tsfile.read.common.Field;
import org.apache.iotdb.tsfile.read.common.RowRecord;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * author: EasyIT
 * description: 充电桩IoTDB数据控制器
 * date: 2024
 */
@RestController
@RequestMapping("/api/iotdb/charging-station")
@Slf4j
public class ChargingStationIotdbController {

    @Resource
    private ChargingStationIotdbService chargingStationIotdbService;

    @Resource
    private ChargingStationIotdbUtils chargingStationIotdbUtils;
    
    // 新增: 注入ServiceImpl以使用新增的MQTT数据处理方法
    @Resource
    private ChargingStationIotdbServiceImpl chargingStationIotdbServiceImpl;

    /**
     * 处理MQTT充电数据
     */
    @PostMapping("/mqtt-data")
    public ResponseEntity<Map<String, Object>> processMqttData(@RequestBody Map<String, Object> mqttData) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 验证必要字段
            if (!mqttData.containsKey("device_id") || !mqttData.containsKey("timestamp") || 
                !mqttData.containsKey("voltage") || !mqttData.containsKey("current") || 
                !mqttData.containsKey("state")) {
                result.put("success", false);
                result.put("message", "MQTT数据缺少必要字段");
                return ResponseEntity.badRequest().body(result);
            }
            
            // 处理MQTT数据
            boolean success = chargingStationIotdbServiceImpl.processMqttChargingData(mqttData);
            result.put("success", success);
            result.put("message", success ? "MQTT数据处理成功" : "MQTT数据处理失败");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("处理MQTT数据失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "处理MQTT数据失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }
    
    /**
     * 初始化指定充电桩的时间序列
     */
    @PostMapping("/initialize/{stationId}")
    public ResponseEntity<Map<String, Object>> initializeStation(@PathVariable String stationId) {
        Map<String, Object> result = new HashMap<>();
        
        if (!chargingStationIotdbUtils.isValidStationId(stationId)) {
            result.put("success", false);
            result.put("message", "无效的充电桩ID");
            return ResponseEntity.badRequest().body(result);
        }
        
        boolean success = chargingStationIotdbService.createChargingStationTimeSeries(stationId);
        if (success) {
            // 初始化为空闲状态
            chargingStationIotdbService.insertChargingStationStatus(stationId, "0", "system");
            result.put("success", true);
            result.put("message", "充电桩时间序列初始化成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "充电桩时间序列初始化失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 插入充电桩状态数据
     */
    @PostMapping("/{stationId}/status")
    public ResponseEntity<Map<String, Object>> insertStatus(
            @PathVariable String stationId,
            @RequestParam int status,
            @RequestParam String userId) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (!chargingStationIotdbUtils.isValidStationId(stationId)) {
            result.put("success", false);
            result.put("message", "无效的充电桩ID");
            return ResponseEntity.badRequest().body(result);
        }
        
        if (status < 0 || status > 2) {
            result.put("success", false);
            result.put("message", "无效的状态值，只能是0(空闲)、1(充电中)或2(故障)");
            return ResponseEntity.badRequest().body(result);
        }
        
        boolean success = chargingStationIotdbService.insertChargingStationStatus(stationId, Integer.toString(status), userId);
        if (success) {
            result.put("success", true);
            result.put("message", "状态数据插入成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "状态数据插入失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 插入充电桩电流数据
     */
    @PostMapping("/{stationId}/current")
    public ResponseEntity<Map<String, Object>> insertCurrent(
            @PathVariable String stationId,
            @RequestParam double current,
            @RequestParam String userId,
            @RequestParam(required = false) String opId) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (!chargingStationIotdbUtils.isValidStationId(stationId)) {
            result.put("success", false);
            result.put("message", "无效的充电桩ID");
            return ResponseEntity.badRequest().body(result);
        }
        
        boolean success = chargingStationIotdbService.insertChargingStationCurrent(stationId, current, userId, opId);
        if (success) {
            result.put("success", true);
            result.put("message", "电流数据插入成功");
            return ResponseEntity.ok(result);
        } else {
            result.put("success", false);
            result.put("message", "电流数据插入失败");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 查询充电桩状态历史记录
     */
    @GetMapping("/{stationId}/status-history")
    public ResponseEntity<Map<String, Object>> queryStatusHistory(
            @PathVariable String stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (!chargingStationIotdbUtils.isValidStationId(stationId)) {
            result.put("success", false);
            result.put("message", "无效的充电桩ID");
            return ResponseEntity.badRequest().body(result);
        }
        
        try {
            long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            
            SessionDataSetWrapper wrapper = chargingStationIotdbService.queryStatusHistory(stationId, startTimestamp, endTimestamp);
            List<Map<String, Object>> dataList = convertQueryResultToList(wrapper);
            
            result.put("success", true);
            result.put("data", dataList);
            result.put("message", "状态历史查询成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询状态历史失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "状态历史查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 查询充电桩电流历史记录
     */
    @GetMapping("/{stationId}/current-history")
    public ResponseEntity<Map<String, Object>> queryCurrentHistory(
            @PathVariable String stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        
        Map<String, Object> result = new HashMap<>();
        
        if (!chargingStationIotdbUtils.isValidStationId(stationId)) {
            result.put("success", false);
            result.put("message", "无效的充电桩ID");
            return ResponseEntity.badRequest().body(result);
        }
        
        try {
            long startTimestamp = startTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            long endTimestamp = endTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            
            SessionDataSetWrapper wrapper = chargingStationIotdbService.queryCurrentHistory(stationId, startTimestamp, endTimestamp);
            List<Map<String, Object>> dataList = convertQueryResultToList(wrapper);
            
            result.put("success", true);
            result.put("data", dataList);
            result.put("message", "电流历史查询成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("查询电流历史失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "电流历史查询失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取所有充电桩的最新状态
     */
    @GetMapping("/all/latest-status")
    public ResponseEntity<Map<String, Object>> getAllStationsLatestStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SessionDataSetWrapper wrapper = chargingStationIotdbService.queryAllStationsLatestStatus();
            List<Map<String, Object>> dataList = convertQueryResultToList(wrapper);
            
            // 处理结果，添加状态描述
            for (Map<String, Object> data : dataList) {
                if (data.containsKey("status")) {
                    int status = Integer.parseInt(data.get("status").toString());
                    data.put("status_desc", chargingStationIotdbUtils.getStatusDescription(status));
                }
            }
            
            result.put("success", true);
            result.put("data", dataList);
            result.put("message", "获取所有充电桩最新状态成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取所有充电桩最新状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取所有充电桩最新状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 获取指定充电桩的最新状态
     */
    @GetMapping("/{stationId}/latest-status")
    public ResponseEntity<Map<String, Object>> getStationLatestStatus(@PathVariable String stationId) {
        Map<String, Object> result = new HashMap<>();
        
        if (!chargingStationIotdbUtils.isValidStationId(stationId)) {
            result.put("success", false);
            result.put("message", "无效的充电桩ID");
            return ResponseEntity.badRequest().body(result);
        }
        
        try {
            SessionDataSetWrapper wrapper = chargingStationIotdbService.queryStationLatestStatus(stationId);
            List<Map<String, Object>> dataList = convertQueryResultToList(wrapper);
            
            // 添加状态描述
            if (!dataList.isEmpty()) {
                Map<String, Object> data = dataList.get(0);
                if (data.containsKey("status")) {
                    int status = Integer.parseInt(data.get("status").toString());
                    data.put("status_desc", chargingStationIotdbUtils.getStatusDescription(status));
                }
            }
            
            result.put("success", true);
            result.put("data", dataList.isEmpty() ? null : dataList.get(0));
            result.put("message", "获取充电桩最新状态成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("获取充电桩最新状态失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "获取充电桩最新状态失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 根据状态查询充电桩列表
     */
    @GetMapping("/by-status")
    public ResponseEntity<Map<String, Object>> getStationsByStatus(@RequestParam int status) {
        Map<String, Object> result = new HashMap<>();
        
        if (status < 0 || status > 2) {
            result.put("success", false);
            result.put("message", "无效的状态值，只能是0(空闲)、1(充电中)或2(故障)");
            return ResponseEntity.badRequest().body(result);
        }
        
        try {
            SessionDataSetWrapper wrapper = chargingStationIotdbService.queryStationsByStatus(status);
            List<Map<String, Object>> dataList = convertQueryResultToList(wrapper);
            
            result.put("success", true);
            result.put("data", dataList);
            result.put("status_desc", chargingStationIotdbUtils.getStatusDescription(status));
            result.put("message", "根据状态查询充电桩成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("根据状态查询充电桩失败: {}", e.getMessage());
            result.put("success", false);
            result.put("message", "根据状态查询充电桩失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 将查询结果转换为列表
     */
    private List<Map<String, Object>> convertQueryResultToList(SessionDataSetWrapper wrapper) throws StatementExecutionException, IoTDBConnectionException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<String> columnNames = wrapper.getColumnNames();
        
        while (wrapper.hasNext()) {
            RowRecord rowRecord = wrapper.next();
            Map<String, Object> rowMap = new HashMap<>();
            
            // 添加时间戳
            rowMap.put("timestamp", rowRecord.getTimestamp());
            
            // 添加各列数据
            List<Field> fields = rowRecord.getFields();
            for (int i = 0; i < fields.size() && i < columnNames.size(); i++) {
                Field field = fields.get(i);
                rowMap.put(columnNames.get(i), field == null ? null : field.getStringValue());
            }
            
            resultList.add(rowMap);
        }
        
        wrapper.close();
        return resultList;
    }
}