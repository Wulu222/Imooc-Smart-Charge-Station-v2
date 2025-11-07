package com.easyit.chargeweb.entity;

import java.io.Serializable;

/**
 * 充电记录详细信息实体类
 * 用于存储从IoT数据库获取的充电记录详细信息
 */
public class ChargingRecordDetail implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private Double chargedEnergy;     // 充电电量（度）
    private Double averageCurrent;    // 平均电流（A）
    private Double totalCost;         // 总费用（元）
    private Double averageVoltage;    // 平均电压（V）
    private Double maxPower;          // 最大功率（W）
    private long chargingDuration;    // 充电时长（秒）
    
    // getter和setter方法
    public Double getChargedEnergy() {
        return chargedEnergy;
    }
    
    public void setChargedEnergy(Double chargedEnergy) {
        this.chargedEnergy = chargedEnergy;
    }
    
    public Double getAverageCurrent() {
        return averageCurrent;
    }
    
    public void setAverageCurrent(Double averageCurrent) {
        this.averageCurrent = averageCurrent;
    }
    
    public Double getTotalCost() {
        return totalCost;
    }
    
    public void setTotalCost(Double totalCost) {
        this.totalCost = totalCost;
    }
    
    public Double getAverageVoltage() {
        return averageVoltage;
    }
    
    public void setAverageVoltage(Double averageVoltage) {
        this.averageVoltage = averageVoltage;
    }
    
    public Double getMaxPower() {
        return maxPower;
    }
    
    public void setMaxPower(Double maxPower) {
        this.maxPower = maxPower;
    }
    
    public long getChargingDuration() {
        return chargingDuration;
    }
    
    public void setChargingDuration(long chargingDuration) {
        this.chargingDuration = chargingDuration;
    }
}