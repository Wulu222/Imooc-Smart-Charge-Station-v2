# IoTDB数据库设计文档 - 充电桩监控系统

## 1. 数据库概述

### 1.1 设计目标
本文档描述基于Apache IoTDB 1.3.2版本的充电桩监控系统数据库设计，旨在高效存储和管理充电桩的时序数据，支持实时监控、历史查询和数据分析需求。

### 1.2 系统规模
- **监控设备**: 4个充电桩（CP001-CP004）
- **数据采集频率**: 电流数据实时采集，状态变化时更新
- **存储周期**: 根据业务需求可配置

## 2. 数据模型设计

### 2.1 物理路径设计
系统采用树形层级结构组织数据，按照地理位置和设备ID进行分区：

```
root.tianjin.xiqing.university.<stationId>
```

**层级说明**：
- `root`: IoTDB根节点
- `tianjin`: 城市层级
- `xiqing`: 区域层级
- `university`: 具体位置层级
- `<stationId>`: 充电桩ID（如CP001、CP002等）

### 2.2 时间序列设计
每个充电桩包含以下时间序列：

| 时间序列名称 | 数据类型 | 编码方式 | 描述 |
|------------|---------|---------|------|
| `status` | INT32 | RLE | 充电桩状态(0-空闲, 1-充电中, 2-充电开始, 3-充电结束, 4-故障) |
| `status_user_id` | TEXT | PLAIN | 状态变更的操作人ID |
| `status_time` | INT64 | RLE | 状态变更的时间戳 |
| `current` | DOUBLE | GORILLA | 实时电流值(单位:A) |
| `voltage` | DOUBLE | GORILLA | 实时电压值(单位:V) |
| `charging_user_id` | TEXT | PLAIN | 当前充电的用户ID |
| `charging_time` | INT64 | RLE | 充电数据记录的时间戳 |
| `op_id` | TEXT | PLAIN | 操作ID，用于关联充电记录，实际格式如"000000-000000-000000-000002"或"op001" |

### 2.3 编码策略选择
- **RLE编码**：适用于整型数据（状态码、时间戳），这些数据通常变化不频繁
- **GORILLA编码**：适用于双精度浮点型数据（电流、电压），具有高压缩率和查询性能
- **PLAIN编码**：适用于文本类型数据，保证数据完整性


## 4. 数据初始化

系统启动时，对每个充电桩进行初始状态设置：
- 初始状态为空闲（status=0）
- 初始操作人为系统（status_user_id='system'）
- 记录初始化时间戳
- 初始化op_id（如"op001"、"op002"等）

初始化SQL示例：
```sql
INSERT INTO root.tianjin.xiqing.university.CP001 (status, status_user_id, status_time, op_id) 
VALUES (0, 'system', 1620000000002, 'op001');
```

## 5. 数据操作模式

### 5.1 数据写入
- **状态更新**：当充电桩状态变更时触发
- **充电参数数据**：实时采集电流、电压数据并写入，可关联op_id
- **批量操作**：支持批量写入以提高性能
- **MQTT数据接收**：支持通过MQTT接收并处理设备发送的实时充电数据，数据格式包含device_id、timestamp、voltage、current、state字段
- **操作ID关联**：支持通过op_id字段关联不同系统间的充电记录，保证数据一致性

### 5.2 数据查询

#### 5.2.1 监控查询
- 单个充电桩最新状态查询
- 所有充电桩状态聚合查询
- 特定时间范围的历史数据查询

#### 5.2.2 分析查询
- 充电参数数据（电流、电压）聚合统计（均值、最大值等）
- 基于时间窗口的数据分析
- 状态转换历史记录
- 电压与电流关系分析

### 5.3 常用查询示例

```sql
-- 查询所有充电桩的最新状态
SELECT last_value(status) as status, last_value(status_user_id) as user_id 
FROM root.tianjin.xiqing.university.* 
GROUP BY device;

-- 查询特定充电桩的状态历史（过去24小时）
SELECT status, status_user_id, status_time 
FROM root.tianjin.xiqing.university.CP001 
WHERE time >= now() - 24h 
ORDER BY time;

-- 查询特定充电桩的电流历史（过去1小时，1分钟采样）
SELECT current, charging_user_id 
FROM root.tianjin.xiqing.university.CP001 
WHERE time >= now() - 1h 
ORDER BY time;

-- 查询所有空闲状态的充电桩
SELECT device 
FROM root.tianjin.xiqing.university.* 
WHERE status = 0 
GROUP BY device 
HAVING time = max(time);

-- 根据操作ID查询充电数据（基于实际CSV数据）
SELECT sum(voltage), max_value(charging_time), min_value(charging_time) 
FROM root.tianjin.xiqing.university.CP001 
WHERE status=1 AND op_id="000000-000000-000000-000002";

-- 查询特定充电桩在一次充电过程中的所有数据（过滤特殊值）
SELECT time, status, current, voltage, op_id 
FROM root.tianjin.xiqing.university.CP001 
WHERE op_id = "000000-000000-000000-000002" 
  AND current > -100 
  AND voltage > -100
ORDER BY time ASC;

-- 注意事项：
-- 1. 在IoTDB 1.3.2中，last()函数只能应用于原始时间序列
-- 2. 当使用通配符查询多个时间序列时，建议使用last(*)
-- 3. 对于特定字段查询，需要指定完整的时间序列路径

## 6. 性能优化策略

### 6.1 连接池配置
- 最大连接数：根据系统负载动态调整
- 连接超时：配置合理的超时时间
- 重连机制：自动重连确保系统稳定性

### 6.2 批量操作优化
- 批量写入大小：根据数据量调整（默认200条/批）
- 异步写入：减少同步操作对系统性能的影响

### 6.3 数据压缩
- 根据数据类型选择最优编码策略
- 利用IoTDB 1.3.2的压缩特性减少存储空间

## 7. 兼容性与版本说明

- **IoTDB版本**：Apache IoTDB 1.3.2
- **API兼容性**：使用insertRecord方法进行数据插入
- **SQL语法**：遵循IoTDB 1.3.2版本的SQL语法规范

## 8. 维护与管理

### 8.1 数据备份策略
- 定期快照备份
- 增量日志备份

### 8.2 数据清理策略
- 基于保留策略自动清理过期数据
- 定期检查存储空间使用情况

### 8.3 监控指标
- 连接池状态监控
- 查询性能监控
- 存储空间使用监控

## 9. 附录

### 9.1 状态码说明
| 状态码 | 描述 |
|-------|------|
| 0 | 空闲 |
| 1 | 充电中 |
| 2 | 开始充电 |
| 3 | 结束充电 |
| 4 | 故障 |

### 9.2 特殊值说明
根据CSV数据，系统使用以下特殊值表示特定状态：
| 特殊值 | 用途 | 出现字段 |
|-------|------|--------|
| -100.0 | 表示开始充电前的初始状态 | current, voltage |
| -99.0 | 表示充电结束状态 | current, voltage |
| -1.0 | 表示故障或异常状态 | current, voltage |
| 空字符串 | 表示未指定用户 | charging_user_id |

### 9.3 MQTT数据格式说明
硬件设备通过MQTT发送的实时充电数据格式：
```json
{
  "device_id": "CP001",
  "timestamp": 88528,
  "voltage": 0.278592,
  "current": 0,
  "state": "charging"
}
```

**字段说明**：
- `device_id`：充电桩设备ID（如CP001、CP002等）
- `timestamp`：数据采集时间戳
- `voltage`：电压值（单位：V）
- `current`：电流值（单位：A）
- `state`：充电状态（charging-充电中、idle-空闲、charging_started充电开始、charging_stopped充电结束、fault-故障）

### 9.4 初始化脚本位置
`src/main/resources/iotdb-init-script.sql`

### 9.5 配置文件位置
`src/main/resources/application.yml`