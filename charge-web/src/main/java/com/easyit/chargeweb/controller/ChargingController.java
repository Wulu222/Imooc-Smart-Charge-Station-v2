package com.easyit.chargeweb.controller;

import com.easyit.chargeweb.common.ResponseResult;
import com.easyit.chargeweb.entity.ChargingRecord;
import com.easyit.chargeweb.entity.ChargingRecordDetail;
import com.easyit.chargeweb.repository.ChargingRecordRepository;
import com.easyit.chargeweb.service.ChargingRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

/**
 * 充电控制器
 * 处理充电相关的API请求
 */
@RestController
@RequestMapping("/api/charging")
public class ChargingController {

    private static final Logger logger = LoggerFactory.getLogger(ChargingController.class);

    @Autowired
    private ChargingRecordService chargingRecordService;
    
    @Autowired
    private ChargingRecordRepository chargingRecordRepository;

    /**
     * 添加充电记录
     * 从netty-server接收充电请求并创建充电记录
     * 符合MongoDB初始化说明中的数据结构要求
     * @param chargeRequest 充电请求数据
     * @return 操作结果
     */
    @PostMapping("/add")
    public ResponseResult<ChargingRecord> addCharging(@RequestBody ChargeRequest chargeRequest) {
        logger.info("接收到添加充电记录请求，设备ID: {}, 操作命令: {}, 操作ID: {}", 
                chargeRequest.getDevice_id(), chargeRequest.getCmds(), chargeRequest.getOp_id());

        // 参数验证
        if (chargeRequest.getDevice_id() == null || chargeRequest.getDevice_id().trim().isEmpty()) {
            logger.warn("添加充电记录失败：设备ID不能为空");
            return ResponseResult.fail(400, "设备ID不能为空");
        }

        // 处理操作ID：如果提供了过长的操作ID则截断
        String opId = chargeRequest.getOp_id();
        if (opId != null && opId.length() > 20) {
            // 确保操作ID不超过20位，符合MongoDB初始化说明中的要求
            opId = opId.substring(0, 20);
            logger.warn("操作ID过长，已截断为: {}", opId);
        }

        // 根据命令处理充电请求
        String command = chargeRequest.getCmds();
        ChargingRecord record;
        Date now = new Date();
        
        if ("ON".equals(command)) {
            // 开始充电：创建新的充电记录
            record = new ChargingRecord();
            record.setChargerId(chargeRequest.getDevice_id());
            record.setOpId(opId); // 空操作ID会在服务层自动生成
            record.setUserId("SYSTEM"); // 操作记录userId默认为SYSTEM
            record.setStatus(0); // 进行中
            record.setStartTime(now);
            // 使用正确的字段名，删除不存在的字段设置
            record.setChargedEnergy(0.0);
            record.setAverageCurrent(0.0);
            record.setCost(0.0);
            record.setDuration(0);
            
            // 保存充电记录（服务层会自动处理空操作ID的生成）
            record = chargingRecordService.saveChargingRecord(record);
        } else if ("OFF".equals(command)) {
            // 停止充电：查找并更新现有充电记录
            if (opId == null || opId.trim().isEmpty()) {
                logger.warn("停止充电失败：操作ID不能为空");
                return ResponseResult.fail(400, "停止充电时操作ID不能为空");
            }
            
            // 调用服务层方法根据opId查询记录
            Optional<ChargingRecord> optionalRecord = chargingRecordRepository.findByOpId(opId);
            if (optionalRecord.isPresent()) {
                record = optionalRecord.get();
                
                // 更新充电记录
                record.setStatus(2); // 已取消
                record.setEndTime(now);
                
                // 计算充电时长（秒）
                if (record.getStartTime() != null) {
                    long durationInSeconds = (now.getTime() - record.getStartTime().getTime()) / 1000;
                    record.setDuration((int) durationInSeconds);
                }

                //查询iotdb数据库，根据opId查询充电记录的详细信息，包括充电电量、平均电流和费用

                // 从iotdb数据库查询充电记录的详细信息 - 这里不指定status参数，使用null表示不添加status条件
                ChargingRecordDetail detail = chargingRecordService.queryChargingDetail(opId, record.getChargerId(), "1");

                // 优化：完整更新MongoDB中的充电记录数据，使用正确的字段名
                if (detail != null) {
                    record.setChargedEnergy(detail.getChargedEnergy());
                    record.setAverageCurrent(detail.getAverageCurrent());
                    // 更新费用字段，确保使用正确的字段名
                    if (detail.getTotalCost() != null) {
                        record.setCost(detail.getTotalCost());
                    }
                    // 转换充电时长单位（从秒转换为分钟）
                    if (detail.getChargingDuration() > 0) {
                        // 向上取整，确保充电1秒也算1分钟
                        int durationMinutes = (int) Math.ceil((double) detail.getChargingDuration() / 60.0);
                        record.setDuration(durationMinutes);
                    }
                }
                
                logger.info("使用IoT数据库数据更新充电记录，操作ID: {}, 充电电量: {}度, 平均电流: {}A, 费用: {}元", 
                        opId, detail != null ? detail.getChargedEnergy() : "未获取", 
                        detail != null ? detail.getAverageCurrent() : "未获取", 
                        detail != null && detail.getTotalCost() != null ? detail.getTotalCost() : "未获取");
                
                // 避免可能的空指针异常，添加空值检查
                if (detail != null) {
                    logger.info("使用IoT数据库数据更新充电记录，操作ID: {}, 充电电量: {}度, 平均电流: {}A, 费用: {}元", 
                            opId, detail.getChargedEnergy(), detail.getAverageCurrent(), detail.getTotalCost());
                }
                
                // 保存更新后的记录
                record = chargingRecordService.saveChargingRecord(record);
            } else {
                logger.warn("未找到对应的充电记录，操作ID: {}", opId);
                return ResponseResult.fail(404, "未找到对应的充电记录");
            }
        } else {
            logger.warn("无效的操作命令: {}", command);
            return ResponseResult.fail(400, "无效的操作命令，请使用'ON'或'OFF'");
        }
        logger.info("充电记录保存成功，记录ID: {}, 充电桩ID: {}, 操作ID: {}, 状态: {}", 
                record.getId(), record.getChargerId(), record.getOpId(), 
                getStatusText(record.getStatus()));

        // 返回成功响应，确保与历史记录查询接口返回格式一致
        return ResponseResult.success(record);
    }
    
    /**
     * 获取状态文本描述
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "进行中";
            case 1: return "已完成";
            case 2: return "已取消";
            default: return "未知";
        }
    }

    /**
     * 充电请求实体类
     * 用于接收来自netty-server的ProtobufHandler中ChargePayload的数据格式
     */
    private static class ChargeRequest {
        private String cmds;      // 操作命令，如"ON"或"OFF"
        private String device_id; // 设备ID，对应MongoDB文档中的chargerId字段
        private String op_id;     // 操作ID，对应MongoDB文档中的opId字段

        // getter和setter方法
        public String getCmds() {
            return cmds;
        }

        public void setCmds(String cmds) {
            this.cmds = cmds;
        }

        public String getDevice_id() {
            return device_id;
        }

        public void setDevice_id(String device_id) {
            this.device_id = device_id;
        }

        public String getOp_id() {
            return op_id;
        }

        public void setOp_id(String op_id) {
            this.op_id = op_id;
        }
    }
}