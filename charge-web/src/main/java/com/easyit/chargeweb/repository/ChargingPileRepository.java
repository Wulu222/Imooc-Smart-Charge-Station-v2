package com.easyit.chargeweb.repository;

import com.easyit.chargeweb.entity.ChargingPile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 充电桩数据访问层
 * 提供对MongoDB中充电粧数据的CRUD操作
 */
@Repository
public interface ChargingPileRepository extends MongoRepository<ChargingPile, String> {
    
    /**
     * 根据充电桩编号查询充电桩信息
     * @param id 充电桩编号
     * @return 充电桩信息
     */
    java.util.Optional<ChargingPile> findById(String id);
    
    /**
     * 判断充电桩是否存在
     * @param id 充电桩编号
     * @return 是否存在
     */
    boolean existsById(String id);
}