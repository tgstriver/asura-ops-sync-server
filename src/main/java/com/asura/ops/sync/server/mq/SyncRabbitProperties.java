package com.asura.ops.sync.server.mq;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/11
 * @description: 类的描述
 */
@Data
@Component
public class SyncRabbitProperties {

    @Value("${sync.rabbitmq.host}")
    private String host;

    @Value("${sync.rabbitmq.port}")
    private Integer port;

    @Value("${sync.rabbitmq.username}")
    private String username;

    @Value("${sync.rabbitmq.password}")
    private String password;

    @Value("${sync.rabbitmq.virtual-host}")
    private String virtualHost;

}
