package com.easyit.chargeweb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 充电记录实体类
 * 用于存储充电桩的充电记录信息
 */
@Data
@Document(collection = "charging_records")
public class ChargingRecord {

    /**
     * 记录ID
     */
    @Id
    private String id;
    
    /**
     * 充电操作编号（唯一，每次充电重新生成）
     */
    private String opId;

    /**
     * 充电桩编号
     */
    private String chargerId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 开始充电时间
     */
    private Date startTime;

    /**
     * 结束充电时间
     */
    private Date endTime;

    /**
     * 充电电量（kWh）
     */
    private Double chargedEnergy;
    
    /**
     * 平均充电电流（A）
     */
    private Double averageCurrent;

    /**
     * 充电费用（元）
     */
    private Double cost;

    /**
     * 充电时长（分钟）
     */
    private Integer duration;

    /**
     * 充电状态
     * 0: 进行中
     * 1: 已完成
     * 2: 已取消
     */
    private Integer status;

    /**
     * 创建时间
     */
    @CreatedDate
    private Date createdAt;

    /**
     * 更新时间
     */
    @LastModifiedDate
    private Date updatedAt;
}