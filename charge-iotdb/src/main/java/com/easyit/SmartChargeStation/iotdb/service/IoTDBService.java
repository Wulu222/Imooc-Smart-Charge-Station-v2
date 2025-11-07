package com.easyit.SmartChargeStation.iotdb.service;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;

import java.util.List;

public interface IoTDBService {

    /**
     * author: EasyIT
     * description: 根据自定义sql语句查询数据
     * @param sql:
     * @return org.apache.iotdb.isession.pool.SessionDataSetWrapper
     */
    SessionDataSetWrapper executeQueryStatement(String sql);


    /**
     * author: EasyIT
     * description: 根据时间区间(左比右开)查询数据
     * @param path: 时间序列的命名
     * @param startTime: 开始时间戳
     * @param endTime: 结束时间戳 (不包含)
     * @param timeOut:  过期时间
     * @return org.apache.iotdb.isession.pool.SessionDataSetWrapper
     */
    SessionDataSetWrapper executeRawDataQuery(List<String> path, long startTime, long endTime, long timeOut);


}
