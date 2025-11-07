package com.easyit.chargeweb.init;

import com.easyit.chargeweb.entity.ChargingRecord;
import com.easyit.chargeweb.repository.ChargingRecordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 充电记录数据初始化类
 * 用于在应用启动时创建MongoDB集合并填充测试数据
 */
@Component
public class ChargingRecordDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ChargingRecordDataInitializer.class);
    
    // 用于生成随机数的安全随机数生成器
    private static final SecureRandom random = new SecureRandom();

    @Autowired
    private ChargingRecordRepository chargingRecordRepository;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始初始化充电记录测试数据...");

        // 检查是否已有数据
        if (chargingRecordRepository.count() == 0) {
            logger.info("未发现现有充电记录数据，开始创建测试数据...");
            
            // 创建测试数据
            List<ChargingRecord> records = generateTestData();
            
            // 批量保存
            chargingRecordRepository.saveAll(records);
            
            logger.info("测试数据创建完成，共创建 {} 条充电记录", records.size());
        } else {
            logger.info("发现现有充电记录数据，跳过初始化，当前记录数: {}", chargingRecordRepository.count());
        }
    }

    /**
     * 生成测试数据
     */
    private List<ChargingRecord> generateTestData() {
        List<ChargingRecord> records = new ArrayList<>();
        
        // 为充电桩 "CP001" 创建测试数据
        generateRecordsForCharger(records, "CP001");
        
        // 为充电桩 "CP002" 创建测试数据
        generateRecordsForCharger(records, "CP002");
        
        return records;
    }

    /**
     * 为指定充电桩生成测试记录
     */
    private void generateRecordsForCharger(List<ChargingRecord> records, String chargerId) {
        // 创建10条历史记录
        for (int i = 0; i < 10; i++) {
            ChargingRecord record = new ChargingRecord();
            record.setChargerId(chargerId);
            record.setUserId("USER" + (i % 5 + 1));
            
            // 设置唯一的充电操作编号
            record.setOpId(generateUniqueOpId());
            
            // 设置时间
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            cal.set(Calendar.HOUR_OF_DAY, 8 + i % 10);
            cal.set(Calendar.MINUTE, i * 5);
            cal.set(Calendar.SECOND, 0);
            Date startTime = cal.getTime();
            record.setStartTime(startTime);
            
            // 设置结束时间（已完成的记录）
            if (i % 3 != 0) { // 2/3的概率已完成
                cal.add(Calendar.MINUTE, 30 + i * 5);
                Date endTime = cal.getTime();
                record.setEndTime(endTime);
                record.setStatus(1); // 已完成
                
                // 计算充电时长（分钟）
                long duration = (endTime.getTime() - startTime.getTime()) / (1000 * 60);
                record.setDuration((int) duration);
            } else {
                record.setStatus(0); // 进行中
                record.setDuration(null);
            }
            
            // 随机生成充电电量（10-50度）
            double energy = 10 + Math.random() * 40;
            record.setChargedEnergy(Math.round(energy * 100) / 100.0); // 保留两位小数
            
            // 随机生成平均充电电流（10-30A）
            double current = 10 + Math.random() * 20;
            record.setAverageCurrent(Math.round(current * 10) / 10.0); // 保留一位小数
            
            // 计算费用（假设1.5元/度）
            if (record.getStatus() == 1) { // 只有已完成的记录才有费用
                double cost = record.getChargedEnergy() * 1.5;
                record.setCost(Math.round(cost * 100) / 100.0); // 保留两位小数
            }
            
            records.add(record);
        }
    }
    
    /**
     * 生成唯一的充电操作编号
     * 通过自定义函数方式生成，不超过20位
     * 格式：OP + 年月日时分秒 + 4位随机数
     */
    private String generateUniqueOpId() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        
        // 生成4位随机数确保唯一性，不使用计数方式
        int randomNum = 1000 + random.nextInt(9000); // 生成1000-9999之间的随机数
        
        // 确保总长度不超过20位
        String opId = "OP" + timestamp + randomNum;
        return opId; // 长度：OP(2) + timestamp(14) + randomNum(4) = 20位
    }
}