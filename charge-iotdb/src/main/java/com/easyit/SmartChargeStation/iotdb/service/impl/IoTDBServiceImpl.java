package com.easyit.SmartChargeStation.iotdb.service.impl;

import com.easyit.SmartChargeStation.iotdb.config.IotDBSessionConf;
import com.easyit.SmartChargeStation.iotdb.service.IoTDBService;
import jakarta.annotation.Resource;
import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;

import java.util.List;

/**
 * author: EasyIT
 * description: iotDB Session Service实现类
 * date: 2024
 */

public class IoTDBServiceImpl implements IoTDBService {

    @Resource
    private IotDBSessionConf conf;


    @Override
    public SessionDataSetWrapper executeQueryStatement(String sql) {

        SessionPool pool = conf.initSessionPool();
        SessionDataSetWrapper wrapper = null;
        try {
            wrapper=  pool.executeQueryStatement(sql);
        } catch (IoTDBConnectionException e) {
            throw new RuntimeException(e);
        } catch (StatementExecutionException e) {
            throw new RuntimeException(e);
        }

        return wrapper;
    }

    @Override
    public SessionDataSetWrapper executeRawDataQuery(List<String> path, long startTime, long endTime, long timeOut) {
        SessionPool pool = conf.initSessionPool();
        SessionDataSetWrapper wrapper = null;
        try {
           wrapper = pool.executeRawDataQuery(path,startTime,endTime,timeOut);
        } catch (IoTDBConnectionException e) {
            throw new RuntimeException(e);
        } catch (StatementExecutionException e) {
            throw new RuntimeException(e);
        }

        return wrapper;
    }
}
