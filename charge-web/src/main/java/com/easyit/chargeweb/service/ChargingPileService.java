package com.easyit.chargeweb.service;

import com.easyit.chargeweb.entity.ChargingPile;
import com.easyit.chargeweb.repository.ChargingPileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 充电桩业务服务类
 * 处理充电桩相关的业务逻辑
 */
@Service
public class ChargingPileService {
    
    @Autowired
    private ChargingPileRepository chargingPileRepository;
    
    /**
     * 初始化方法，在应用启动时添加一些测试数据
     * 仅用于演示，生产环境应通过其他方式导入数据
     */
    @PostConstruct
    public void initData() {
        // 检查是否已有数据，如果没有则初始化
        List<ChargingPile> existingPiles = chargingPileRepository.findAll();
        if (existingPiles.isEmpty()) {
            // 创建并添加第一个充电桩
            ChargingPile pile1 = new ChargingPile();
            pile1.setId("CP001");
            pile1.setLocation("A区1号车位");
            pile1.setPower(7);
            pile1.setEnvironmentImage("http://example.com/images/a1.jpg");
            pile1.setStatus(0);
            pile1.setStatusDescription("空闲");
            chargingPileRepository.save(pile1);
            
            // 创建并添加第二个充电桩
            ChargingPile pile2 = new ChargingPile();
            pile2.setId("CP002");
            pile2.setLocation("A区2号车位");
            pile2.setPower(22);
            pile2.setEnvironmentImage("http://example.com/images/a2.jpg");
            pile2.setStatus(1);
            pile2.setStatusDescription("充电中");
            chargingPileRepository.save(pile2);
            
            // 创建并添加第三个充电桩
            ChargingPile pile3 = new ChargingPile();
            pile3.setId("CP003");
            pile3.setLocation("B区1号车位");
            pile3.setPower(7);
            pile3.setEnvironmentImage("http://example.com/images/b1.jpg");
            pile3.setStatus(2);
            pile3.setStatusDescription("故障");
            chargingPileRepository.save(pile3);
            
            // 创建并添加第四个充电桩
            ChargingPile pile4 = new ChargingPile();
            pile4.setId("CP004");
            pile4.setLocation("B区2号车位");
            pile4.setPower(44);
            pile4.setEnvironmentImage("http://example.com/images/b2.jpg");
            pile4.setStatus(3);
            pile4.setStatusDescription("离线");
            chargingPileRepository.save(pile4);
        }
    }
    
    /**
     * 根据充电桩编号获取充电桩信息
     * @param id 充电桩编号
     * @return 充电桩信息，如果不存在则返回null
     */
    public ChargingPile getChargingPileById(String id) {
        return chargingPileRepository.findById(id).orElse(null);
    }
    
    /**
     * 判断充电桩是否存在
     * @param id 充电桩编号
     * @return 是否存在
     */
    public boolean exists(String id) {
        return chargingPileRepository.existsById(id);
    }
    
    /**
     * 保存充电桩信息
     * @param chargingPile 充电桩信息
     * @return 保存后的充电桩信息
     */
    public ChargingPile saveChargingPile(ChargingPile chargingPile) {
        // 如果状态描述为空，根据状态码设置默认描述
        if (chargingPile.getStatusDescription() == null) {
            switch (chargingPile.getStatus()) {
                case 0:
                    chargingPile.setStatusDescription("空闲");
                    break;
                case 1:
                    chargingPile.setStatusDescription("充电中");
                    break;
                case 2:
                    chargingPile.setStatusDescription("故障");
                    break;
                case 3:
                    chargingPile.setStatusDescription("离线");
                    break;
                default:
                    chargingPile.setStatusDescription("未知");
            }
        }
        
        // 保存到MongoDB
        return chargingPileRepository.save(chargingPile);
    }
}