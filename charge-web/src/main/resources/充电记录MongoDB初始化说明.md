# 充电记录MongoDB初始化说明

## 概述

本文档提供了智能充电站管理系统中MongoDB数据库初始化方法、数据结构及使用说明，包括充电桩集合和充电记录集合的配置。

## MongoDB连接配置

系统使用以下MongoDB连接配置：
- 主机：localhost
- 端口：27017
- 数据库：charging_station
- 集合：charging_piles（充电桩信息）、charging_records（充电记录）

## 初始化方法

### 方法：使用MongoDB客户端工具执行脚本

直接执行以下命令运行初始化脚本：

```bash
# 使用mongosh（MongoDB 6.0+推荐）
mongosh mongodb-init.js

# 或使用传统的mongo命令
mongo mongodb-init.js
```

初始化脚本会：
1. 创建或切换到charging_station数据库
2. 删除已存在的集合（如果需要重新初始化）
3. 创建必要的索引
4. 插入充电桩初始数据
5. 插入充电历史记录模拟数据
6. 执行查询示例验证数据

## 索引说明

### 充电桩集合索引

系统为charging_piles集合创建了以下索引：
1. `id`（唯一索引）: 为充电桩编号创建唯一索引
2. `location`: 为位置创建索引，加速位置查询
3. `status`: 为状态创建索引，加速状态过滤

### 充电记录集合索引

系统为charging_records集合创建了以下索引：
1. `chargerId`: 充电桩ID的索引，加速按充电桩查询
2. `userId`: 用户ID的索引，加速按用户查询
3. `startTime`（降序）: 开始时间的索引，加速按时间排序
4. `status`: 状态索引，加速按状态过滤
5. `chargerId, status`（复合索引）: 加速按充电桩和状态组合查询
6. `opId`（唯一索引）: 充电操作编号的唯一索引，确保每个充电操作编号的唯一性

## 数据结构

### 充电桩集合（charging_piles）

| 字段名 | 类型 | 描述 | 示例值 |
|-------|------|------|-------|
| `_id` | String | 文档主键（与业务id保持一致） | "CP001" |
| `id` | String | 充电桩编号 | "CP001" |
| `location` | String | 充电桩位置 | "A区1号充电桩" |
| `power` | Integer | 充电功率（kW） | 7 |
| `environmentImage` | String | 环境图片URL | "https://example.com/images/pile1.jpg" |
| `status` | Integer | 充电桩状态（0=空闲，1=充电中，2=故障） | 0 |
| `statusDescription` | String | 状态描述文本 | "空闲" |
| `createdAt` | Date | 创建时间 | ISODate("2024-01-16T09:10:00Z") |
| `updatedAt` | Date | 更新时间 | ISODate("2024-01-16T09:10:00Z") |

### 充电记录集合（charging_records）

| 字段名 | 类型 | 描述 | 示例值 |
|-------|------|------|-------|
| `_id` | ObjectId | MongoDB自动生成的主键 | 自动生成 |
| `chargerId` | String | 充电桩编号 | "CP001" |
| `opId` | String | 充电操作编号（唯一，格式：OP+年月日时分秒+4位随机数） | "OP2024011609100001" |
| `userId` | String | 用户ID | "USER003" |
| `startTime` | Date | 充电开始时间 | ISODate("2024-01-16T09:10:00Z") |
| `endTime` | Date | 充电结束时间（进行中记录此项为null） | ISODate("2024-01-15T10:15:00Z") |
| `chargedEnergy` | Double | 充电电量（度） | 25.5 |
| `averageCurrent` | Double | 平均充电电流（A） | 16.8 |
| `cost` | Double | 充电费用（元） | 38.25 |
| `duration` | Integer | 充电时长（分钟，进行中记录此项为null） | 105 |
| `status` | Integer | 充电状态（0=进行中，1=已完成） | 1 |
| `createdAt` | Date | 创建时间 | ISODate("2024-01-15T08:30:00Z") |
| `updatedAt` | Date | 更新时间 | ISODate("2024-01-15T10:15:00Z") |

## 初始化测试数据

初始化脚本会自动插入以下测试数据：

### 充电桩数据

- 4个测试充电桩：
  - CP001：A区1号充电桩，7kW，空闲状态
  - CP002：A区2号充电桩，7kW，充电中状态
  - CP003：B区1号充电桩，22kW，空闲状态
  - CP004：B区2号充电桩，22kW，故障状态

### 充电记录数据

- 总计34条充电记录，分布情况：
  - CP001：20条记录（19条已完成，1条进行中）
  - CP002：5条记录（全部已完成）
  - CP003：6条记录（全部已完成）
  - CP004：3条记录（全部已完成）
- 记录时间范围：2024年1月6日至2024年1月16日
- 充电费用按照1.5元/度计算
- 每个记录都有唯一的opId（格式：OP+年月日时分秒+4位随机数）

## 操作编号（opId）生成规则

脚本中使用自定义函数生成opId，具体规则：
1. 前缀固定为"OP"
2. 中间部分为14位时间戳（年月日时分秒）
3. 后缀为4位随机数
4. 总长度不超过20位

## 查询示例

初始化脚本中包含以下查询示例：

```javascript
// 所有充电桩列表
print("\n所有充电粧列表：");
db.charging_piles.find().forEach(printjson);

// 查询空闲的充电桩
print("\n空闲的充电粧：");
db.charging_piles.find({ "status": 0 }).forEach(printjson);

// 查询特定充电桩的充电记录
print("\nCP001充电桩的充电记录：");
db.charging_records.find({ "chargerId": "CP001" }).sort({ "startTime": -1 }).forEach(printjson);

// 根据操作编号查询充电记录
print("\n根据操作编号查询充电记录示例：");
db.charging_records.find({ "opId": "OP2024011609100001" }).forEach(printjson);
```

## 故障排除

如果初始化过程中遇到问题，请检查：

1. MongoDB服务是否正在运行
2. 连接字符串是否正确（localhost:27017）
3. 是否有足够的权限创建数据库和集合
4. MongoDB客户端工具是否正确安装

如需进一步帮助，请查看MongoDB官方文档或联系系统管理员。