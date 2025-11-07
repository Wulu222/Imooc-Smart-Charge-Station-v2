package com.easyit.chargeweb.controller;

import com.easyit.chargeweb.common.ResponseResult;
import com.easyit.chargeweb.entity.ChargingPile;
import com.easyit.chargeweb.entity.ChargingRecord;
import com.easyit.chargeweb.service.ChargingPileService;
import com.easyit.chargeweb.service.ChargingRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

/**
 * 充电桩控制器
 * 处理小程序发送的充电桩相关请求
 */
@RestController
@RequestMapping("/chargers")
public class ChargersController {

    private static final Logger logger = LoggerFactory.getLogger(ChargersController.class);

    @Autowired
    private ChargingPileService chargingPileService;
    
    @Autowired
    private ChargingRecordService chargingRecordService;

    /**
     * 根据充电桩编号获取充电桩信息
     * @param id 充电桩编号
     * @return 充电桩信息
     */
    @GetMapping("/{id}")
    public ResponseResult<ChargingPile> getChargerInfo(@PathVariable String id) {
        logger.info("获取充电桩信息请求，充电桩编号: {}", id);
        
        // 验证充电桩编号是否存在
        if (!chargingPileService.exists(id)) {
            logger.warn("获取充电桩信息失败，未找到编号为{}的充电桩", id);
            return ResponseResult.fail(404, "未找到编号为" + id + "的充电桩");
        }

        // 获取充电桩信息
        ChargingPile chargingPile = chargingPileService.getChargingPileById(id);
        logger.info("成功获取充电桩信息，充电桩编号: {}, 状态: {}", id, chargingPile.getStatus());
        
        // 返回成功响应
        return ResponseResult.success(chargingPile);
    }
    
    /**
     * 添加新的充电桩
     * @param chargingPile 充电桩信息
     * @return 添加后的充电桩信息
     */
    @PostMapping
    public ResponseResult<ChargingPile> addCharger(@RequestBody ChargingPile chargingPile) {
        logger.info("添加新充电桩请求，充电桩编号: {}, 位置: {}, 功率: {}kW", 
                chargingPile.getId(), chargingPile.getLocation(), chargingPile.getPower());
        
        // 验证必填字段
        if (chargingPile.getId() == null || chargingPile.getId().trim().isEmpty()) {
            logger.warn("添加充电桩失败：充电桩编号不能为空");
            return ResponseResult.fail(400, "充电桩编号不能为空");
        }
        
        if (chargingPile.getLocation() == null || chargingPile.getLocation().trim().isEmpty()) {
            logger.warn("添加充电桩失败：充电桩位置不能为空，充电桩编号: {}", chargingPile.getId());
            return ResponseResult.fail(400, "充电桩位置不能为空");
        }
        
        if (chargingPile.getPower() == null || chargingPile.getPower() <= 0) {
            logger.warn("添加充电桩失败：充电桩功率必须为正数，充电桩编号: {}, 功率: {}", 
                    chargingPile.getId(), chargingPile.getPower());
            return ResponseResult.fail(400, "充电桩功率必须为正数");
        }
        
        if (chargingPile.getStatus() == null || (chargingPile.getStatus() < 0 || chargingPile.getStatus() > 3)) {
            logger.warn("添加充电桩失败：充电桩状态无效，充电桩编号: {}, 状态: {}", 
                    chargingPile.getId(), chargingPile.getStatus());
            return ResponseResult.fail(400, "充电桩状态必须为0-3之间的整数");
        }
        
        // 检查充电桩编号是否已存在
        if (chargingPileService.exists(chargingPile.getId())) {
            logger.warn("添加充电桩失败：编号为{}的充电桩已存在", chargingPile.getId());
            return ResponseResult.fail(400, "编号为" + chargingPile.getId() + "的充电桩已存在");
        }
        
        // 保存充电桩信息
        ChargingPile savedPile = chargingPileService.saveChargingPile(chargingPile);
        logger.info("成功添加充电桩，充电桩编号: {}, 位置: {}, 功率: {}kW, 状态: {}", 
                savedPile.getId(), savedPile.getLocation(), savedPile.getPower(), savedPile.getStatus());
        
        // 返回成功响应
        return ResponseResult.success(savedPile);
    }
    
    /**
     * 获取充电桩的充电记录
     * @param chargerId 充电桩编号
     * @param page 页码，从0开始
     * @param size 每页记录数
     * @param status 可选的状态过滤参数
     * @return 分页的充电记录
     */
    @GetMapping("/{chargerId}/history")
    public ResponseResult<Page<ChargingRecord>> getChargerHistory(
            @PathVariable String chargerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status) {
        
        logger.info("获取充电桩充电记录，充电桩编号: {}, 页码: {}, 每页大小: {}, 状态过滤: {}", 
                chargerId, page, size, status);
        
        // 验证充电桩编号是否存在
        if (!chargingPileService.exists(chargerId)) {
            logger.warn("未找到编号为{}的充电桩", chargerId);
            return ResponseResult.fail(404, "未找到编号为" + chargerId + "的充电桩");
        }
        
        // 验证分页参数
        if (page < 0) {
            page = 0;
        }
        if (size < 1 || size > 100) {
            size = 10; // 默认每页10条记录，最大不超过100条
        }
        
        // 创建分页参数，按开始充电时间倒序排序
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "startTime"));
        
        // 查询充电记录
        Page<ChargingRecord> records;
        if (status != null) {
            // 验证状态参数
            if (status < 0 || status > 2) {
                return ResponseResult.fail(400, "无效的状态参数，状态必须为0-2之间的整数");
            }
            records = chargingRecordService.getChargingRecordsByChargerIdAndStatus(chargerId, status, pageable);
        } else {
            records = chargingRecordService.getChargingRecordsByChargerId(chargerId, pageable);
        }
        
        logger.info("查询完成，充电桩编号: {}, 总记录数: {}, 当前页记录数: {}", 
                chargerId, records.getTotalElements(), records.getContent().size());
        
        // 返回成功响应
        return ResponseResult.success(records);
    }
}