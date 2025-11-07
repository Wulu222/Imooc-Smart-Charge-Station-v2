
CREATE STORAGE GROUP root.tianjin.xiqing.university;


CREATE TIMESERIES root.tianjin.xiqing.university.CP001.status WITH DATATYPE=INT32, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.status_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.status_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.current WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.voltage WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.charging_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.charging_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP001.op_id WITH DATATYPE=TEXT, ENCODING=PLAIN;

CREATE TIMESERIES root.tianjin.xiqing.university.CP002.status WITH DATATYPE=INT32, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.status_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.status_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.current WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.voltage WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.charging_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.charging_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP002.op_id WITH DATATYPE=TEXT, ENCODING=PLAIN;

CREATE TIMESERIES root.tianjin.xiqing.university.CP003.status WITH DATATYPE=INT32, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.status_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.status_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.current WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.voltage WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.charging_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.charging_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP003.op_id WITH DATATYPE=TEXT, ENCODING=PLAIN;

CREATE TIMESERIES root.tianjin.xiqing.university.CP004.status WITH DATATYPE=INT32, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.status_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.status_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.current WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.voltage WITH DATATYPE=DOUBLE, ENCODING=GORILLA;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.charging_user_id WITH DATATYPE=TEXT, ENCODING=PLAIN;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.charging_time WITH DATATYPE=INT64, ENCODING=RLE;
CREATE TIMESERIES root.tianjin.xiqing.university.CP004.op_id WITH DATATYPE=TEXT, ENCODING=PLAIN;



INSERT INTO root.tianjin.xiqing.university.CP001 (status, status_user_id, status_time,op_id) 
VALUES ( 0, 'system', 1620000000002,'op001');

INSERT INTO root.tianjin.xiqing.university.CP002 (status, status_user_id, status_time,op_id) 
VALUES (0, 'system', 1620000000003,'op002');

INSERT INTO root.tianjin.xiqing.university.CP003 ( status, status_user_id, status_time,op_id) 
VALUES (0, 'system', 1620000000003,'op003');

INSERT INTO root.tianjin.xiqing.university.CP004 ( status, status_user_id, status_time,op_id) 
VALUES ( 0, 'system', 1620000000003,'op004');




SELECT sum(voltage),max_value(charging_time),min_value(charging_time) 
FROM root.tianjin.xiqing.university.CP001 
WHERE status=1 AND op_id="000000-000000-000000-000002"

-- ===============================================================
-- 数据聚合查询示例（IoTDB 1.3.2版本）
-- ===============================================================

-- 查询5分钟电流平均值（使用IoTDB 1.3.2支持的时间序列聚合语法）
-- SELECT avg(current) 
-- FROM root.tianjin.xiqing.university.CP001 
-- WHERE time >= now() - 5m 
-- GROUP BY time(5m);

-- 对于需要连续计算的场景，可以在应用程序层面实现定时任务
-- 或使用IoTDB的Trigger功能（在1.3.2版本中已支持）

-- ===============================================================
-- 5. 数据查询示例
-- ===============================================================

-- 查询所有充电桩的最新状态
-- SELECT last_value(status) as status, last_value(status_user_id) as user_id 
-- FROM root.tianjin.xiqing.university.* 
-- GROUP BY device;

-- 查询特定充电桩的状态历史（过去24小时）
-- SELECT status, status_user_id, status_time 
-- FROM root.tianjin.xiqing.university.CP001 
-- WHERE time >= now() - 24h 
-- ORDER BY time;

-- 查询特定充电桩的电流历史（过去1小时，1分钟采样）
-- SELECT current, charging_user_id 
-- FROM root.tianjin.xiqing.university.CP001 
-- WHERE time >= now() - 1h 
-- ORDER BY time;

-- 查询所有空闲状态的充电桩
-- SELECT device 
-- FROM root.tianjin.xiqing.university.* 
-- WHERE status = 0 
-- GROUP BY device 
-- HAVING time = max(time);

-- ===============================================================
-- 脚本结束
-- ===============================================================