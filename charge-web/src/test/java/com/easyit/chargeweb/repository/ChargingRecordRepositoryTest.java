package com.easyit.chargeweb.repository;

import com.easyit.chargeweb.entity.ChargingRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 充电记录数据访问层测试
 */
@DataMongoTest
@ActiveProfiles("test")
public class ChargingRecordRepositoryTest {

    @Autowired
    private ChargingRecordRepository chargingRecordRepository;

    /**
     * 测试按充电桩ID查询充电记录
     */
    @Test
    void testFindByChargerId() {
        // 创建测试数据
        ChargingRecord record1 = new ChargingRecord();
        record1.setChargerId("CP001");
        record1.setUserId("USER1");
        record1.setStatus(1);
        
        ChargingRecord record2 = new ChargingRecord();
        record2.setChargerId("CP001");
        record2.setUserId("USER2");
        record2.setStatus(1);
        
        ChargingRecord record3 = new ChargingRecord();
        record3.setChargerId("CP002");
        record3.setUserId("USER1");
        record3.setStatus(1);
        
        chargingRecordRepository.saveAll(List.of(record1, record2, record3));
        
        // 测试查询
        Page<ChargingRecord> page = chargingRecordRepository.findByChargerId("CP001", 
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));
        
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
        assertTrue(page.getContent().stream().allMatch(r -> "CP001".equals(r.getChargerId())));
    }

    /**
     * 测试按充电桩ID和状态查询充电记录
     */
    @Test
    void testFindByChargerIdAndStatus() {
        // 创建测试数据
        ChargingRecord record1 = new ChargingRecord();
        record1.setChargerId("CP001");
        record1.setUserId("USER1");
        record1.setStatus(0); // 进行中
        
        ChargingRecord record2 = new ChargingRecord();
        record2.setChargerId("CP001");
        record2.setUserId("USER2");
        record2.setStatus(1); // 已完成
        
        chargingRecordRepository.saveAll(List.of(record1, record2));
        
        // 测试查询已完成状态
        Page<ChargingRecord> completedPage = chargingRecordRepository.findByChargerIdAndStatus("CP001", 1, 
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));
        
        assertNotNull(completedPage);
        assertEquals(1, completedPage.getTotalElements());
        assertTrue(completedPage.getContent().stream().allMatch(r -> r.getStatus() == 1));
        
        // 测试查询进行中状态
        Page<ChargingRecord> ongoingPage = chargingRecordRepository.findByChargerIdAndStatus("CP001", 0, 
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "startTime")));
        
        assertNotNull(ongoingPage);
        assertEquals(1, ongoingPage.getTotalElements());
        assertTrue(ongoingPage.getContent().stream().allMatch(r -> r.getStatus() == 0));
    }

    /**
     * 测试查询最新的充电记录
     */
    @Test
    void testFindTop10ByChargerIdOrderByStartTimeDesc() {
        // 创建测试数据（实际测试时会使用真实的时间戳）
        for (int i = 0; i < 15; i++) {
            ChargingRecord record = new ChargingRecord();
            record.setChargerId("CP001");
            record.setUserId("USER" + (i % 3 + 1));
            record.setStatus(1);
            chargingRecordRepository.save(record);
        }
        
        // 查询最新的10条记录
        List<ChargingRecord> records = chargingRecordRepository.findTop10ByChargerIdOrderByStartTimeDesc("CP001");
        
        assertNotNull(records);
        assertEquals(10, records.size());
        assertTrue(records.stream().allMatch(r -> "CP001".equals(r.getChargerId())));
    }

    /**
     * 测试统计方法
     */
    @Test
    void testCountMethods() {
        // 创建测试数据
        for (int i = 0; i < 5; i++) {
            ChargingRecord record = new ChargingRecord();
            record.setChargerId("CP001");
            record.setUserId("USER1");
            record.setStatus(i % 2); // 交替设置状态
            chargingRecordRepository.save(record);
        }
        
        // 测试总记录数
        long totalCount = chargingRecordRepository.countByChargerId("CP001");
        assertEquals(5, totalCount);
        
        // 测试已完成状态记录数
        long completedCount = chargingRecordRepository.countByChargerIdAndStatus("CP001", 1);
        assertEquals(2, completedCount); // 5条记录中应该有2条状态为1
        
        // 测试进行中状态记录数
        long ongoingCount = chargingRecordRepository.countByChargerIdAndStatus("CP001", 0);
        assertEquals(3, ongoingCount); // 5条记录中应该有3条状态为0
    }
}