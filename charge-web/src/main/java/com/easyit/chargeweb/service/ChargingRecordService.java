package com.easyit.chargeweb.service;

import com.easyit.chargeweb.entity.ChargingRecord;
import com.easyit.chargeweb.entity.ChargingRecordDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * 充电记录服务接口
 * 提供充电记录相关的业务逻辑
 */
public interface ChargingRecordService {

    /**
     * 获取充电桩的充电记录
     * @param chargerId 充电桩编号
     * @param pageable 分页参数
     * @return 分页的充电记录列表
     */
    Page<ChargingRecord> getChargingRecordsByChargerId(String chargerId, Pageable pageable);

    /**
     * 根据状态获取充电桩的充电记录
     * @param chargerId 充电桩编号
     * @param status 充电状态
     * @param pageable 分页参数
     * @return 分页的充电记录列表
     */
    Page<ChargingRecord> getChargingRecordsByChargerIdAndStatus(String chargerId, Integer status, Pageable pageable);

    /**
     * 获取充电桩的最新充电记录
     * @param chargerId 充电桩编号
     * @return 最近的充电记录列表
     */
    List<ChargingRecord> getLatestChargingRecords(String chargerId);

    /**
     * 获取充电桩的充电统计信息
     * @param chargerId 充电桩编号
     * @return 充电统计数据
     */
    ChargingStatistics getChargingStatistics(String chargerId);

    /**
     * 保存充电记录
     * @param record 充电记录
     * @return 保存后的充电记录
     */
    ChargingRecord saveChargingRecord(ChargingRecord record);
    
    /**
     * 根据操作ID查询充电记录的详细信息
     * @param opId 操作ID
     * @param deviceId 设备ID
     * @param status 状态参数（可选），如果提供则增加status列的判断条件
     * @return 充电记录详细信息
     */
    ChargingRecordDetail queryChargingDetail(String opId, String deviceId, String status);

    /**
     * 充电统计数据类
     */
    interface ChargingStatistics {
        long getTotalCount();
        long getCompletedCount();
        Double getTotalEnergy();
        Double getTotalRevenue();
        Double getAverageCurrent();
    }
}