// MongoDB 建库建表脚本
// 适用于充电粧系统的数据初始化

// 连接到MongoDB服务器并明确创建charging_station数据库
db = db.getSiblingDB('charging_station');
print('已创建/切换到charging_station数据库');

// 删除已存在的集合（如果需要重新初始化）
db.charging_piles.drop();
db.charging_records.drop();

// 创建充电粧集合
// MongoDB中如果集合不存在，插入数据时会自动创建

// 创建索引以提高查询性能
db.charging_piles.createIndex({ "id": 1 }, { unique: true }); // 为充电桩编号创建唯一索引
db.charging_piles.createIndex({ "location": 1 }); // 为位置创建索引
db.charging_piles.createIndex({ "status": 1 }); // 为状态创建索引

// 为充电记录集合创建索引
db.charging_records.createIndex({ "chargerId": 1 }); // 为充电桩ID创建索引，加速按充电桩查询
db.charging_records.createIndex({ "userId": 1 }); // 为用户ID创建索引，加速按用户查询
db.charging_records.createIndex({ "startTime": -1 }); // 为开始时间创建降序索引，加速按时间排序
db.charging_records.createIndex({ "status": 1 }); // 为状态创建索引，加速按状态过滤
db.charging_records.createIndex({ "chargerId": 1, "status": 1 }); // 复合索引，加速按充电桩和状态查询
db.charging_records.createIndex({ "opId": 1 }, { unique: true }); // 为操作编号创建唯一索引，确保操作编号的唯一性
print('已为charging_records集合创建索引');

// 插入初始充电粧数据
db.charging_piles.insertMany([
  {
    "_id": "CP001", // MongoDB默认的_id字段，这里与我们的业务id保持一致
    "id": "CP001",
    "location": "A区1号充电桩",
    "power": 7,
    "environmentImage": "https://example.com/images/pile1.jpg",
    "status": 0,
    "statusDescription": "空闲",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": "CP002",
    "id": "CP002",
    "location": "A区2号充电桩",
    "power": 7,
    "environmentImage": "https://example.com/images/pile2.jpg",
    "status": 1,
    "statusDescription": "充电中",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": "CP003",
    "id": "CP003",
    "location": "B区1号充电桩",
    "power": 22,
    "environmentImage": "https://example.com/images/pile3.jpg",
    "status": 0,
    "statusDescription": "空闲",
    "createdAt": new Date(),
    "updatedAt": new Date()
  },
  {
    "_id": "CP004",
    "id": "CP004",
    "location": "B区2号充电桩",
    "power": 22,
    "environmentImage": "https://example.com/images/pile4.jpg",
    "status": 2,
    "statusDescription": "故障",
    "createdAt": new Date(),
    "updatedAt": new Date()
  }
]);

// 验证数据插入是否成功
print("充电粧数据初始化完成，共插入" + db.charging_piles.countDocuments() + "条记录");

// 插入充电历史记录模拟数据
print("\n开始插入充电历史记录数据...");

// 生成唯一操作编号的函数 - 不超过20位，通过自定义函数方式生成
function generateOpId() {
    // 生成当前时间戳：年月日时分秒（14位）
    var now = new Date();
    var year = now.getFullYear();
    var month = String(now.getMonth() + 1).padStart(2, '0');
    var day = String(now.getDate()).padStart(2, '0');
    var hours = String(now.getHours()).padStart(2, '0');
    var minutes = String(now.getMinutes()).padStart(2, '0');
    var seconds = String(now.getSeconds()).padStart(2, '0');
    
    var timestamp = year + month + day + hours + minutes + seconds;
    
    // 生成4位随机数
    var randomNum = Math.floor(1000 + Math.random() * 9000);
    
    // 格式：OP + 年月日时分秒 + 4位随机数（总长度不超过20位）
    return "OP" + timestamp + randomNum;
}

// 为每个充电桩生成充电历史记录
var chargingRecords = [
  // CP001 充电桩的充电记录（20条）
  {
    "chargerId": "CP001",
    "opId": "OP2024011609100001",
    "userId": "USER003",
    "startTime": new Date(2024, 0, 16, 9, 10, 0), // 2024-01-16 09:10:00
    "status": 0, // 进行中
    "chargedEnergy": 0,
    "averageCurrent": 0,
    "cost": 0,
    "duration": null,
    "createdAt": new Date(2024, 0, 16, 9, 10, 0),
    "updatedAt": new Date(2024, 0, 16, 9, 10, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011508300002",
    "userId": "USER001",
    "startTime": new Date(2024, 0, 15, 8, 30, 0), // 2024-01-15 08:30:00
    "endTime": new Date(2024, 0, 15, 10, 15, 0),  // 2024-01-15 10:15:00
    "chargedEnergy": 25.5,
    "averageCurrent": 16.8,
    "cost": 38.25,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 15, 8, 30, 0),
    "updatedAt": new Date(2024, 0, 15, 10, 15, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011416450003",
    "userId": "USER002",
    "startTime": new Date(2024, 0, 14, 16, 45, 0), // 2024-01-14 16:45:00
    "endTime": new Date(2024, 0, 14, 18, 20, 0),  // 2024-01-14 18:20:00
    "chargedEnergy": 18.2,
    "averageCurrent": 14.3,
    "cost": 27.30,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 14, 16, 45, 0),
    "updatedAt": new Date(2024, 0, 14, 18, 20, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011407150004",
    "userId": "USER004",
    "startTime": new Date(2024, 0, 14, 7, 15, 0), // 2024-01-14 07:15:00
    "endTime": new Date(2024, 0, 14, 8, 50, 0),  // 2024-01-14 08:50:00
    "chargedEnergy": 17.3,
    "averageCurrent": 13.9,
    "cost": 25.95,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 14, 7, 15, 0),
    "updatedAt": new Date(2024, 0, 14, 8, 50, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011320300005",
    "userId": "USER005",
    "startTime": new Date(2024, 0, 13, 20, 30, 0), // 2024-01-13 20:30:00
    "endTime": new Date(2024, 0, 13, 22, 15, 0),  // 2024-01-13 22:15:00
    "chargedEnergy": 24.1,
    "averageCurrent": 16.1,
    "cost": 36.15,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 13, 20, 30, 0),
    "updatedAt": new Date(2024, 0, 13, 22, 15, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011311450006",
    "userId": "USER006",
    "startTime": new Date(2024, 0, 13, 11, 45, 0), // 2024-01-13 11:45:00
    "endTime": new Date(2024, 0, 13, 13, 20, 0),  // 2024-01-13 13:20:00
    "chargedEnergy": 19.8,
    "averageCurrent": 14.5,
    "cost": 29.70,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 13, 11, 45, 0),
    "updatedAt": new Date(2024, 0, 13, 13, 20, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011215200007",
    "userId": "USER007",
    "startTime": new Date(2024, 0, 12, 15, 20, 0), // 2024-01-12 15:20:00
    "endTime": new Date(2024, 0, 12, 17, 0o5, 0),  // 2024-01-12 17:05:00
    "chargedEnergy": 26.3,
    "averageCurrent": 17.2,
    "cost": 39.45,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 12, 15, 20, 0),
    "updatedAt": new Date(2024, 0, 12, 17, 0o5, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011208100008",
    "userId": "USER008",
    "startTime": new Date(2024, 0, 12, 8, 10, 0), // 2024-01-12 08:10:00
    "endTime": new Date(2024, 0, 12, 9, 45, 0),  // 2024-01-12 09:45:00
    "chargedEnergy": 18.7,
    "averageCurrent": 14.6,
    "cost": 28.05,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 12, 8, 10, 0),
    "updatedAt": new Date(2024, 0, 12, 9, 45, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011119300009",
    "userId": "USER009",
    "startTime": new Date(2024, 0, 11, 19, 30, 0), // 2024-01-11 19:30:00
    "endTime": new Date(2024, 0, 11, 21, 15, 0),  // 2024-01-11 21:15:00
    "chargedEnergy": 23.5,
    "averageCurrent": 15.8,
    "cost": 35.25,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 11, 19, 30, 0),
    "updatedAt": new Date(2024, 0, 11, 21, 15, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011110450010",
    "userId": "USER010",
    "startTime": new Date(2024, 0, 11, 10, 45, 0), // 2024-01-11 10:45:00
    "endTime": new Date(2024, 0, 11, 12, 20, 0),  // 2024-01-11 12:20:00
    "chargedEnergy": 21.2,
    "averageCurrent": 15.3,
    "cost": 31.80,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 11, 10, 45, 0),
    "updatedAt": new Date(2024, 0, 11, 12, 20, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011016200011",
    "userId": "USER011",
    "startTime": new Date(2024, 0, 10, 16, 20, 0), // 2024-01-10 16:20:00
    "endTime": new Date(2024, 0, 10, 17, 55, 0),  // 2024-01-10 17:55:00
    "chargedEnergy": 19.3,
    "averageCurrent": 14.8,
    "cost": 28.95,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 10, 16, 20, 0),
    "updatedAt": new Date(2024, 0, 10, 17, 55, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024011009300012",
    "userId": "USER012",
    "startTime": new Date(2024, 0, 10, 9, 30, 0), // 2024-01-10 09:30:00
    "endTime": new Date(2024, 0, 10, 11, 0o5, 0),  // 2024-01-10 11:05:00
    "chargedEnergy": 22.5,
    "averageCurrent": 15.6,
    "cost": 33.75,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 10, 9, 30, 0),
    "updatedAt": new Date(2024, 0, 10, 11, 0o5, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010918450013",
    "userId": "USER013",
    "startTime": new Date(2024, 0, 9, 18, 45, 0), // 2024-01-09 18:45:00
    "endTime": new Date(2024, 0, 9, 20, 20, 0),  // 2024-01-09 20:20:00
    "chargedEnergy": 20.7,
    "averageCurrent": 15.1,
    "cost": 31.05,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 9, 18, 45, 0),
    "updatedAt": new Date(2024, 0, 9, 20, 20, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010910150014",
    "userId": "USER014",
    "startTime": new Date(2024, 0, 9, 10, 15, 0), // 2024-01-09 10:15:00
    "endTime": new Date(2024, 0, 9, 11, 40, 0),  // 2024-01-09 11:40:00
    "chargedEnergy": 16.3,
    "averageCurrent": 13.8,
    "cost": 24.45,
    "duration": 85,
    "status": 1,
    "createdAt": new Date(2024, 0, 9, 10, 15, 0),
    "updatedAt": new Date(2024, 0, 9, 11, 40, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010815200015",
    "userId": "USER015",
    "startTime": new Date(2024, 0, 8, 15, 20, 0), // 2024-01-08 15:20:00
    "endTime": new Date(2024, 0, 8, 16, 55, 0),  // 2024-01-08 16:55:00
    "chargedEnergy": 17.9,
    "averageCurrent": 14.2,
    "cost": 26.85,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 8, 15, 20, 0),
    "updatedAt": new Date(2024, 0, 8, 16, 55, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010808450016",
    "userId": "USER016",
    "startTime": new Date(2024, 0, 8, 8, 45, 0), // 2024-01-08 08:45:00
    "endTime": new Date(2024, 0, 8, 10, 20, 0),  // 2024-01-08 10:20:00
    "chargedEnergy": 19.5,
    "averageCurrent": 14.7,
    "cost": 29.25,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 8, 8, 45, 0),
    "updatedAt": new Date(2024, 0, 8, 10, 20, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010719300017",
    "userId": "USER017",
    "startTime": new Date(2024, 0, 7, 19, 30, 0), // 2024-01-07 19:30:00
    "endTime": new Date(2024, 0, 7, 21, 0o5, 0),  // 2024-01-07 21:05:00
    "chargedEnergy": 21.8,
    "averageCurrent": 15.4,
    "cost": 32.70,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 7, 19, 30, 0),
    "updatedAt": new Date(2024, 0, 7, 21, 0o5, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010711200018",
    "userId": "USER018",
    "startTime": new Date(2024, 0, 7, 11, 20, 0), // 2024-01-07 11:20:00
    "endTime": new Date(2024, 0, 7, 12, 55, 0),  // 2024-01-07 12:55:00
    "chargedEnergy": 18.4,
    "averageCurrent": 14.4,
    "cost": 27.60,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 7, 11, 20, 0),
    "updatedAt": new Date(2024, 0, 7, 12, 55, 0)
  },
  {
    "chargerId": "CP001",
    "opId": "OP2024010616100019",
    "userId": "USER019",
    "startTime": new Date(2024, 0, 6, 16, 10, 0), // 2024-01-06 16:10:00
    "endTime": new Date(2024, 0, 6, 17, 45, 0),  // 2024-01-06 17:45:00
    "chargedEnergy": 16.9,
    "averageCurrent": 13.9,
    "cost": 25.35,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 6, 16, 10, 0),
    "updatedAt": new Date(2024, 0, 6, 17, 45, 0)
  },
  
  // CP002 充电桩的充电记录（5条）
  {
    "chargerId": "CP002",
    "opId": "OP2024011514200020",
    "userId": "USER004",
    "startTime": new Date(2024, 0, 15, 14, 20, 0), // 2024-01-15 14:20:00
    "endTime": new Date(2024, 0, 15, 16, 5, 0),  // 2024-01-15 16:05:00
    "chargedEnergy": 32.8,
    "averageCurrent": 18.5,
    "cost": 49.20,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 15, 14, 20, 0),
    "updatedAt": new Date(2024, 0, 15, 16, 5, 0)
  },
  {
    "chargerId": "CP002",
    "opId": "OP2024011311300021",
    "userId": "USER001",
    "startTime": new Date(2024, 0, 13, 11, 30, 0), // 2024-01-13 11:30:00
    "endTime": new Date(2024, 0, 13, 13, 15, 0),  // 2024-01-13 13:15:00
    "chargedEnergy": 22.1,
    "averageCurrent": 15.7,
    "cost": 33.15,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 13, 11, 30, 0),
    "updatedAt": new Date(2024, 0, 13, 13, 15, 0)
  },
  {
    "chargerId": "CP002",
    "opId": "OP2024011409450022",
    "userId": "USER009",
    "startTime": new Date(2024, 0, 14, 9, 45, 0),  // 2024-01-14 09:45:00
    "endTime": new Date(2024, 0, 14, 11, 30, 0),  // 2024-01-14 11:30:00
    "chargedEnergy": 20.5,
    "averageCurrent": 14.9,
    "cost": 30.75,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 14, 9, 45, 0),
    "updatedAt": new Date(2024, 0, 14, 11, 30, 0)
  },
  {
    "chargerId": "CP002",
    "opId": "OP2024011218500023",
    "userId": "USER010",
    "startTime": new Date(2024, 0, 12, 18, 50, 0), // 2024-01-12 18:50:00
    "endTime": new Date(2024, 0, 12, 20, 35, 0),  // 2024-01-12 20:35:00
    "chargedEnergy": 28.3,
    "averageCurrent": 17.6,
    "cost": 42.45,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 12, 18, 50, 0),
    "updatedAt": new Date(2024, 0, 12, 20, 35, 0)
  },
  {
    "chargerId": "CP002",
    "opId": "OP2024011008150024",
    "userId": "USER005",
    "startTime": new Date(2024, 0, 10, 8, 15, 0),  // 2024-01-10 08:15:00
    "endTime": new Date(2024, 0, 10, 9, 50, 0),   // 2024-01-10 09:50:00
    "chargedEnergy": 17.9,
    "averageCurrent": 14.2,
    "cost": 26.85,
    "duration": 95,
    "status": 1,
    "createdAt": new Date(2024, 0, 10, 8, 15, 0),
    "updatedAt": new Date(2024, 0, 10, 9, 50, 0)
  },
  
  // CP003 充电桩的充电记录（6条）
  {
    "chargerId": "CP003",
    "opId": "OP2024011520400025",
    "userId": "USER005",
    "startTime": new Date(2024, 0, 15, 20, 40, 0), // 2024-01-15 20:40:00
    "endTime": new Date(2024, 0, 15, 22, 25, 0),  // 2024-01-15 22:25:00
    "chargedEnergy": 45.3,
    "averageCurrent": 28.7,
    "cost": 67.95,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 15, 20, 40, 0),
    "updatedAt": new Date(2024, 0, 15, 22, 25, 0)
  },
  {
    "chargerId": "CP003",
    "opId": "OP2024011407250026",
    "userId": "USER002",
    "startTime": new Date(2024, 0, 14, 7, 25, 0), // 2024-01-14 07:25:00
    "endTime": new Date(2024, 0, 14, 9, 10, 0),  // 2024-01-14 09:10:00
    "chargedEnergy": 38.7,
    "averageCurrent": 26.2,
    "cost": 58.05,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 14, 7, 25, 0),
    "updatedAt": new Date(2024, 0, 14, 9, 10, 0)
  },
  {
    "chargerId": "CP003",
    "opId": "OP2024011316300027",
    "userId": "USER011",
    "startTime": new Date(2024, 0, 13, 16, 30, 0), // 2024-01-13 16:30:00
    "endTime": new Date(2024, 0, 13, 18, 15, 0),  // 2024-01-13 18:15:00
    "chargedEnergy": 42.8,
    "averageCurrent": 27.5,
    "cost": 64.20,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 13, 16, 30, 0),
    "updatedAt": new Date(2024, 0, 13, 18, 15, 0)
  },
  {
    "chargerId": "CP003",
    "opId": "OP2024011211150028",
    "userId": "USER012",
    "startTime": new Date(2024, 0, 12, 11, 15, 0), // 2024-01-12 11:15:00
    "endTime": new Date(2024, 0, 12, 13, 0, 0),  // 2024-01-12 13:00:00
    "chargedEnergy": 36.5,
    "averageCurrent": 25.8,
    "cost": 54.75,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 12, 11, 15, 0),
    "updatedAt": new Date(2024, 0, 12, 13, 0, 0)
  },
  {
    "chargerId": "CP003",
    "opId": "OP2024011115450029",
    "userId": "USER013",
    "startTime": new Date(2024, 0, 11, 15, 45, 0), // 2024-01-11 15:45:00
    "endTime": new Date(2024, 0, 11, 17, 30, 0),  // 2024-01-11 17:30:00
    "chargedEnergy": 40.2,
    "averageCurrent": 26.9,
    "cost": 60.30,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 11, 15, 45, 0),
    "updatedAt": new Date(2024, 0, 11, 17, 30, 0)
  },
  {
    "chargerId": "CP003",
    "opId": "OP2024010919200030",
    "userId": "USER014",
    "startTime": new Date(2024, 0, 9, 19, 20, 0),  // 2024-01-09 19:20:00
    "endTime": new Date(2024, 0, 9, 21, 5, 0),   // 2024-01-09 21:05:00
    "chargedEnergy": 44.1,
    "averageCurrent": 28.2,
    "cost": 66.15,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 9, 19, 20, 0),
    "updatedAt": new Date(2024, 0, 9, 21, 5, 0)
  },
  
  // CP004 充电桩的充电记录（3条，由于状态为故障，记录较少）
  {
    "chargerId": "CP004",
    "opId": "OP2024011013500031",
    "userId": "USER003",
    "startTime": new Date(2024, 0, 10, 13, 50, 0), // 2024-01-10 13:50:00
    "endTime": new Date(2024, 0, 10, 15, 35, 0),  // 2024-01-10 15:35:00
    "chargedEnergy": 31.2,
    "averageCurrent": 25.8,
    "cost": 46.80,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 10, 13, 50, 0),
    "updatedAt": new Date(2024, 0, 10, 15, 35, 0)
  },
  {
    "chargerId": "CP004",
    "opId": "OP2024010810300032",
    "userId": "USER015",
    "startTime": new Date(2024, 0, 8, 10, 30, 0),  // 2024-01-08 10:30:00
    "endTime": new Date(2024, 0, 8, 12, 15, 0),   // 2024-01-08 12:15:00
    "chargedEnergy": 37.5,
    "averageCurrent": 26.5,
    "cost": 56.25,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 8, 10, 30, 0),
    "updatedAt": new Date(2024, 0, 8, 12, 15, 0)
  },
  {
    "chargerId": "CP004",
    "opId": "OP2024010717450033",
    "userId": "USER016",
    "startTime": new Date(2024, 0, 7, 17, 45, 0),  // 2024-01-07 17:45:00
    "endTime": new Date(2024, 0, 7, 19, 30, 0),   // 2024-01-07 19:30:00
    "chargedEnergy": 33.9,
    "averageCurrent": 26.1,
    "cost": 50.85,
    "duration": 105,
    "status": 1,
    "createdAt": new Date(2024, 0, 7, 17, 45, 0),
    "updatedAt": new Date(2024, 0, 7, 19, 30, 0)
  }
];

// 批量插入充电记录
db.charging_records.insertMany(chargingRecords);
print("充电历史记录数据初始化完成，共插入" + chargingRecords.length + "条记录");

// 查询示例
print("\n所有充电粧列表：");
db.charging_piles.find().forEach(printjson);

// 查询空闲的充电粧
print("\n空闲的充电粧：");
db.charging_piles.find({ "status": 0 }).forEach(printjson);

// 查询充电记录示例
print("\nCP001充电桩的充电记录：");
db.charging_records.find({ "chargerId": "CP001" }).sort({ "startTime": -1 }).forEach(printjson);

// 根据操作编号查询示例
print("\n根据操作编号查询充电记录示例：");
db.charging_records.find({ "opId": "OP2024011609100001" }).forEach(printjson);