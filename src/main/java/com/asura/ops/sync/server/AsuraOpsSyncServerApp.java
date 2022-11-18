package com.asura.ops.sync.server;

import com.asura.database.annotation.EnableAsuraDatabase;
import com.asura.web.annotation.EnableAsuraWeb;
import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author: zouyang
 * @date: 2022/8/1
 * @description: 启动类
 */
@SpringBootApplication(exclude = {RabbitAutoConfiguration.class})
@EnableAsuraWeb
@EnableAsync
@EnableDiscoveryClient
@ComponentScan(basePackages = {"com.asura.ops.sync", "com.asura.database"})
@MapperScan(basePackages = {"com.asura.ops.sync.server.mapper", "com.asura.database"})
@EnableAsuraDatabase
@EnableApolloConfig
@EnableFeignClients(basePackages = {"com.asura.sequence"})
public class AsuraOpsSyncServerApp {
    public static void main(String[] args) {
        SpringApplication.run(AsuraOpsSyncServerApp.class, args);
    }

}
