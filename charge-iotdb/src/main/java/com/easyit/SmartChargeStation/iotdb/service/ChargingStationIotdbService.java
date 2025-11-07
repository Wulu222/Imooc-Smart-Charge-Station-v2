package com.easyit.SmartChargeStation.iotdb.service;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;

import java.util.Map;

/**
 * author: EasyIT
 * description: 充电桩IoTDB数据服务接口 - 基于IoTDB 1.3.2 API设计
 * date: 2024
 * 注：所有数据插入方法均基于IoTDB 1.3.2的insertRecord API实现
 */
public interface ChargingStationIotdbService {

    /**
     * 创建充电桩数据存储的时间序列
     * 注：使用IoTDB 1.3.2优化的编码方式，根据数据类型选择不同的编码策略
     * @param stationId 充电桩ID
     * @return 是否创建成功
     */
    boolean createChargingStationTimeSeries(String stationId);

    /**
     * 插入充电桩状态数据
     * 注：基于IoTDB 1.3.2的insertRecord API实现
     * @param stationId 充电桩ID
     * @param status 状态（0:空闲, 1:充电中, 2:故障）
     * @param userId 用户ID，状态变化时记录
     * @return 是否插入成功
     */
    boolean insertChargingStationStatus(String stationId, String status, String userId);

    /**
     * 插入充电桩实时电流数据
     * 注：基于IoTDB 1.3.2的insertRecord API实现
     * @param stationId 充电桩ID
     * @param current 当前电流值
     * @param userId 用户ID
     * @return 是否插入成功
     */
    boolean insertChargingStationCurrent(String stationId, double current, String userId);
    
    /**
     * 插入充电桩实时电流数据（带操作ID）
     * 注：基于IoTDB 1.3.2的insertRecord API实现，支持op_id字段
     * @param stationId 充电桩ID
     * @param current 当前电流值
     * @param userId 用户ID
     * @param opId 操作ID
     * @return 是否插入成功
     */
    boolean insertChargingStationCurrent(String stationId, double current, String userId, String opId);

    /**
     * 批量插入充电桩数据
     * 注：基于IoTDB 1.3.2的insertRecord API实现，要求measurements和values数组长度必须一致
     * @param stationId 充电桩ID
     * @param measurements 测量值名称列表
     * @param values 对应的值列表
     * @return 是否插入成功
     */
    boolean insertBatchData(String stationId, String[] measurements, Object[] values);

    /**
     * 查询充电桩指定时间段内的状态变化记录
     * 注：基于IoTDB 1.3.2的SQL查询语法
     * @param stationId 充电桩ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 查询结果
     */
    SessionDataSetWrapper queryStatusHistory(String stationId, long startTime, long endTime);

    /**
     * 查询充电桩指定时间段内的电流变化数据
     * 注：基于IoTDB 1.3.2的SQL查询语法
     * @param stationId 充电桩ID
     * @param startTime 开始时间戳
     * @param endTime 结束时间戳
     * @return 查询结果
     */
    SessionDataSetWrapper queryCurrentHistory(String stationId, long startTime, long endTime);

    /**
     * 查询所有充电桩的最新状态
     * 注：基于IoTDB 1.3.2的last_value函数和GROUP BY device语法
     * @return 查询结果
     */
    SessionDataSetWrapper queryAllStationsLatestStatus();

    /**
     * 查询特定充电桩的最新状态
     * 注：基于IoTDB 1.3.2的last_value函数
     * @param stationId 充电桩ID
     * @return 查询结果
     */
    SessionDataSetWrapper queryStationLatestStatus(String stationId);

    /**
     * 根据状态查询充电桩
     * 注：基于IoTDB 1.3.2的GROUP BY和HAVING语法
     * @param status 状态值
     * @return 查询结果
     */
    SessionDataSetWrapper queryStationsByStatus(int status);
    
    /**
     * 根据操作ID查询充电数据
     * 注：基于IoTDB 1.3.2的SQL查询语法，查询特定op_id的所有充电数据（电流、电压等）
     * @param opId 操作ID
     * @param deviceId 设备ID
     * @param status 状态参数（可选），如果提供则增加status列的判断条件
     * @return 查询结果
     */
    SessionDataSetWrapper queryChargingDataByOpId(String opId, String deviceId, String status);
}