package com.easyit.chargeweb.repository;

import com.easyit.chargeweb.entity.ChargingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 充电记录数据访问层
 * 提供对MongoDB中充电记录数据的CRUD操作
 */
@Repository
public interface ChargingRecordRepository extends MongoRepository<ChargingRecord, String> {

    /**
     * 根据充电桩编号查询充电记录
     * @param chargerId 充电桩编号
     * @param pageable 分页参数
     * @return 分页的充电记录列表
     */
    Page<ChargingRecord> findByChargerId(String chargerId, Pageable pageable);

    /**
     * 根据充电桩编号和状态查询充电记录
     * @param chargerId 充电桩编号
     * @param status 充电状态
     * @param pageable 分页参数
     * @return 分页的充电记录列表
     */
    Page<ChargingRecord> findByChargerIdAndStatus(String chargerId, Integer status, Pageable pageable);

    /**
     * 查询充电桩的最新充电记录
     * @param chargerId 充电桩编号
     * @return 最近的充电记录列表
     */
    List<ChargingRecord> findTop10ByChargerIdOrderByStartTimeDesc(String chargerId);

    /**
     * 统计充电桩的总充电次数
     * @param chargerId 充电桩编号
     * @return 充电次数
     */
    long countByChargerId(String chargerId);

    /**
     * 统计充电桩的已完成充电次数
     * @param chargerId 充电桩编号
     * @param status 状态（已完成=1）
     * @return 已完成充电次数
     */
    long countByChargerIdAndStatus(String chargerId, Integer status);
    
    /**
     * 根据操作ID查询充电记录
     * @param opId 操作ID
     * @return 充电记录（如果存在）
     */
    Optional<ChargingRecord> findByOpId(String opId);
}