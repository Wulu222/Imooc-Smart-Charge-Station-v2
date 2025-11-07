package com.easyit.chargeweb.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * 充电桩实体类
 * 用于存储和传输充电桩信息
 */
@Data
@Document(collection = "charging_piles")
public class ChargingPile {
    
    /**
     * 充电桩编号
     */
    @Id
    private String id;
    
    /**
     * 充电桩位置
     */
    private String location;
    
    /**
     * 充电桩功率
     */
    private Integer power;
    
    /**
     * 环境图片URL
     */
    private String environmentImage;
    
    /**
     * 充电状态
     * 0: 空闲
     * 1: 充电中
     * 2: 故障
     * 3: 离线
     */
    private Integer status;
    
    /**
     * 状态描述
     */
    private String statusDescription;
    
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