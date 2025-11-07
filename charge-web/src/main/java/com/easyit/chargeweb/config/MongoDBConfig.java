package com.easyit.chargeweb.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * MongoDB配置类
 * 启用Spring Data MongoDB的审计功能，支持@CreatedDate和@LastModifiedDate自动填充
 */
@Configuration
@EnableMongoAuditing
public class MongoDBConfig {
    // 该配置类启用MongoDB的审计功能
    // 使得@CreatedDate和@LastModifiedDate注解能够自动设置日期
}