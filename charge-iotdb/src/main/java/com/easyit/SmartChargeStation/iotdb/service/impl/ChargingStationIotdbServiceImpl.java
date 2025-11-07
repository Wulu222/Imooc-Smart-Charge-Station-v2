package com.easyit.SmartChargeStation.iotdb.service.impl;

import com.easyit.SmartChargeStation.iotdb.config.IotDBSessionConf;
import com.easyit.SmartChargeStation.iotdb.service.ChargingStationIotdbService;
import com.easyit.SmartChargeStation.iotdb.utils.OpIdGenerator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * author: EasyIT
 * description: 充电桩IoTDB数据服务实现类
 * date: 2024
 */
@Service
@Slf4j
public class ChargingStationIotdbServiceImpl implements ChargingStationIotdbService {

    @Resource
    private IotDBSessionConf conf;

    // 物理位置路径前缀
    private static final String ROOT_PATH = "root.tianjin.xiqing.university";

    @Override
    public boolean createChargingStationTimeSeries(String stationId) {
        SessionPool pool = conf.initSessionPool();
        try {
            // 创建充电桩的时间序列
            String deviceId = ROOT_PATH + "." + stationId;
            
            // 状态相关时间序列
            List<String> statusMeasurements = Arrays.asList(
                    "status",          // 状态：0-空闲，1-充电中，2-故障
                    "status_user_id",  // 状态变化时的用户ID
                    "status_time"      // 状态变化时间
            );
            
            // 充电数据相关时间序列
            List<String> chargingMeasurements = Arrays.asList(
                    "current",         // 电流值
                    "voltage",         // 电压值 - 新增字段，适配MQTT数据格式
                    "charging_user_id",// 充电用户ID
                    "charging_time",   // 充电数据时间
                    "op_id"            // 操作ID - 新增字段，用于关联充电记录
            );
            
            // 创建所有测量值的时间序列
            List<TSDataType> dataTypes = new ArrayList<>();
            // 状态相关数据类型
            dataTypes.add(TSDataType.INT32);  // status
            dataTypes.add(TSDataType.TEXT);   // status_user_id
            dataTypes.add(TSDataType.INT64);  // status_time
            // 充电数据相关数据类型
            dataTypes.add(TSDataType.DOUBLE); // current
            dataTypes.add(TSDataType.DOUBLE); // voltage - 新增字段
            dataTypes.add(TSDataType.TEXT);   // charging_user_id
            dataTypes.add(TSDataType.INT64);  // charging_time
            dataTypes.add(TSDataType.TEXT);   // op_id - 新增字段
            
            List<String> allMeasurements = new ArrayList<>();
            allMeasurements.addAll(statusMeasurements);
            allMeasurements.addAll(chargingMeasurements);
            
            // 批量创建时间序列：使用更优的编码方式，适应IoTDB 1.3.2版本
            for (int i = 0; i < allMeasurements.size(); i++) {
                String measurementName = allMeasurements.get(i);
                TSDataType dataType = dataTypes.get(i);
                
                // 根据数据类型选择更合适的编码方式
                org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding encoding;
                if (measurementName.equals("status") || measurementName.contains("time")) {
                    // 状态值和时间戳使用RLE编码更高效
                    encoding = org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding.RLE;
                } else if (measurementName.equals("current")) {
                    // 电流值使用GORILLA编码更高效
                    encoding = org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding.GORILLA;
                } else {
                    // 文本类型使用PLAIN编码
                    encoding = org.apache.iotdb.tsfile.file.metadata.enums.TSEncoding.PLAIN;
                }
                
                pool.createTimeseries(
                        deviceId + "." + measurementName,
                        dataType,
                        encoding,
                        org.apache.iotdb.tsfile.file.metadata.enums.CompressionType.SNAPPY
                );
            }
            log.info("成功创建充电桩[{}]的时间序列", stationId);
            return true;
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.error("创建充电桩[{}]时间序列失败: {}", stationId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertChargingStationStatus(String stationId, String status, String userId) {
        SessionPool pool = conf.initSessionPool();
        try {
            String deviceId = ROOT_PATH + "." + stationId;
            long currentTime = System.currentTimeMillis();
            
            // 准备测量值名称列表 - 符合IoTDB 1.3.2 insertRecord API要求
            List<String> measurements = Arrays.asList("status", "status_user_id", "status_time");
            // 准备对应的值列表 - 注意值的顺序必须与测量值名称列表一一对应
            List<String> values = Arrays.asList(status, userId, Long.toString(currentTime));
            
            // 使用insertRecord方法插入记录 - IoTDB 1.3.2推荐的API
            pool.insertRecord(deviceId, currentTime, measurements, values);

            log.info("成功插入充电桩[{}]状态数据: 状态={}, 用户ID={}", stationId, status, userId);
            return true;
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.error("插入充电桩[{}]状态数据失败: {}", stationId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertChargingStationCurrent(String stationId, double current, String userId) {
        // 默认调用带opId的方法，传入null作为opId
        return insertChargingStationCurrent(stationId, current, userId, null);
    }
    
    @Override
    public boolean insertChargingStationCurrent(String stationId, double current, String userId, String opId) {
        SessionPool pool = conf.initSessionPool();
        try {
            String deviceId = ROOT_PATH + "." + stationId;
            long currentTime = System.currentTimeMillis();
            
            // 准备测量值名称列表 - 符合IoTDB 1.3.2 insertRecord API要求
            List<String> measurements = new ArrayList<>();
            List<Object> values = new ArrayList<>();
            
            // 添加基础字段
            measurements.add("current");
            measurements.add("charging_user_id");
            measurements.add("charging_time");
            values.add(current);
            values.add(userId);
            values.add(currentTime);
            
            // 添加op_id字段，如果传入null则自动生成标准格式的op_id
            String finalOpId = (opId != null) ? opId : OpIdGenerator.generateOpId();
            measurements.add("op_id");
            values.add(finalOpId);
            
            // 使用insertRecord方法插入记录 - IoTDB 1.3.2推荐的API
            // 将Object列表转为String列表，满足insertRecord签名要求
            List<String> stringValues = values.stream()
                    .map(Object::toString)
                    .toList();
            pool.insertRecord(deviceId, currentTime, measurements, stringValues);
        
            log.info("成功插入充电桩[{}]电流数据: 电流={}, 用户ID={}, 操作ID={}", 
                     stationId, current, userId, finalOpId);
            return true;
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.error("插入充电桩[{}]电流数据失败: {}", stationId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean insertBatchData(String stationId, String[] measurements, Object[] values) {
        SessionPool pool = conf.initSessionPool();
        try {
            String deviceId = ROOT_PATH + "." + stationId;
            long currentTime = System.currentTimeMillis();
            
            // 将数组转换为List，符合IoTDB 1.3.2 insertRecord API的参数要求
            List<String> measurementsList = Arrays.asList(measurements);
            List<Object> valuesList = Arrays.asList(values);
            
            // 验证参数长度匹配
            if (measurementsList.size() != valuesList.size()) {
                log.error("批量插入参数长度不匹配: 测量值数量={}, 值数量={}", measurementsList.size(), valuesList.size());
                return false;
            }
            
            // 使用insertRecord方法插入批量数据 - IoTDB 1.3.2推荐的API
            List<String> stringValues = valuesList.stream()
                    .map(Object::toString)
                    .toList();
            pool.insertRecord(deviceId, currentTime, measurementsList, stringValues);
            log.info("成功批量插入充电桩[{}]数据，共{}个测量点", stationId, measurementsList.size());
            return true;
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.error("批量插入充电桩[{}]数据失败: {}", stationId, e.getMessage());
            return false;
        }
    }

    @Override
    public SessionDataSetWrapper queryStatusHistory(String stationId, long startTime, long endTime) {
        String sql = String.format("SELECT status, status_user_id, status_time FROM %s.%s WHERE time >= %d AND time < %d ORDER BY time",
                ROOT_PATH, stationId, startTime, endTime);
        return executeQuery(sql);
    }

    @Override
    public SessionDataSetWrapper queryCurrentHistory(String stationId, long startTime, long endTime) {
        String sql = String.format("SELECT current, charging_user_id, charging_time, op_id FROM %s.%s WHERE time >= %d AND time < %d ORDER BY time",
                ROOT_PATH, stationId, startTime, endTime);
        return executeQuery(sql);
    }

    @Override
    public SessionDataSetWrapper queryAllStationsLatestStatus() {
        // 查询所有充电桩的最新状态
        String sql = String.format("SELECT last_value(status) as status, last_value(status_user_id) as user_id FROM %s.* GROUP BY device",
                ROOT_PATH);
        return executeQuery(sql);
    }

    @Override
    public SessionDataSetWrapper queryStationLatestStatus(String stationId) {
        String sql = String.format("SELECT last_value(status) as status, last_value(status_user_id) as user_id FROM %s.%s",
                ROOT_PATH, stationId);
        return executeQuery(sql);
    }

    @Override
    public SessionDataSetWrapper queryStationsByStatus(int status) {
        // 注意：这个查询在IoTDB中可能需要调整，因为它需要实时计算每个设备的最新状态
        String sql = String.format(
                "SELECT device FROM %s.* WHERE status = %d GROUP BY device HAVING time = max(time)",
                ROOT_PATH, status);
        return executeQuery(sql);
    }
    
    @Override
    public SessionDataSetWrapper queryChargingDataByOpId(String opId, String deviceId, String status) {
        // 根据操作ID和设备ID查询充电数据，包括电流、电压、充电用户ID等信息
        // 使用deviceId替换SQL语句中的*，实现精确查询
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append(String.format(
                "SELECT current, voltage, charging_user_id, charging_time, status FROM %s.%s WHERE op_id = '%s'",
                ROOT_PATH, deviceId, opId));
        
        // 如果status参数有值，则添加status列的判断条件
        if (status != null && !status.isEmpty()) {
            sqlBuilder.append(String.format(" AND status = %s", status));
        }
        
        sqlBuilder.append(" ORDER BY time ASC");
        String sql = sqlBuilder.toString();
        return executeQuery(sql);
    }

    /**
     * 处理MQTT发送的充电实时数据
     * 数据格式: {"device_id":"CP001","timestamp":88528,"voltage":0.278592,"current":0,"state":"charging"}
     * @param mqttData MQTT数据Map
     * @return 是否处理成功
     */
    public boolean processMqttChargingData(Map<String, Object> mqttData) {
        SessionPool pool = conf.initSessionPool();
        try {
            // 提取数据
            String deviceId = mqttData.get("device_id").toString();
            long timestamp = Long.parseLong(mqttData.get("timestamp").toString());
            double voltage = Double.parseDouble(mqttData.get("voltage").toString());
            double current = Double.parseDouble(mqttData.get("current").toString());
            String state = mqttData.get("state").toString();
            
            // 构建IoTDB设备路径
            String iotdbDeviceId = ROOT_PATH + "." + deviceId;
            
            // 1. 处理充电状态数据
            // 状态映射: charging -> 1, idle -> 0, fault -> 2
            String statusValue = "0"; // 默认空闲
            if ("charging".equals(state)) {
                statusValue = "1";
            } else if ("fault".equals(state)) {
                statusValue = "2";
            }
            
            // 插入状态数据
            List<String> statusMeasurements = Arrays.asList("status", "status_user_id", "status_time");
            List<String> statusValues = Arrays.asList(statusValue, "mqtt_device", String.valueOf(timestamp));
            pool.insertRecord(iotdbDeviceId, timestamp, statusMeasurements, statusValues);
            
            // 2. 处理充电参数数据（电流、电压）
            // 使用OpIdGenerator生成MQTT临时op_id
            String mqttOpId = com.easyit.SmartChargeStation.iotdb.utils.OpIdGenerator.generateMqttOpId(timestamp);
            
            List<String> chargingMeasurements = Arrays.asList("current", "voltage", "charging_user_id", "charging_time", "op_id");
            List<String> chargingValues = Arrays.asList(
                String.valueOf(current), 
                String.valueOf(voltage), 
                "mqtt_device", 
                String.valueOf(timestamp),
                mqttOpId
            );
            pool.insertRecord(iotdbDeviceId, timestamp, chargingMeasurements, chargingValues);
            
            log.info("成功处理MQTT充电数据: 设备={}, 状态={}, 电流={}, 电压={}", 
                     deviceId, state, current, voltage);
            return true;
        } catch (Exception e) {
            log.error("处理MQTT充电数据失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 执行SQL查询的通用方法
     */
    private SessionDataSetWrapper executeQuery(String sql) {
        SessionPool pool = conf.initSessionPool();
        try {
            log.info("执行IoTDB查询: {}", sql);
            return pool.executeQueryStatement(sql);
        } catch (IoTDBConnectionException | StatementExecutionException e) {
            log.error("IoTDB查询失败: {}", e.getMessage());
            throw new RuntimeException("IoTDB查询失败: " + e.getMessage(), e);
        }
    }
}