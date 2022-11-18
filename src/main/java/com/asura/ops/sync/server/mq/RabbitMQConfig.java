package com.asura.ops.sync.server.mq;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/11
 * @description: 类的描述
 */
@Configuration
public class RabbitMQConfig {

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

//    @Bean(name = "syncRabbitProperties")
//    @Primary
//    public RabbitProperties syncRabbitProperties() {
//        RabbitProperties rabbitProperties = new RabbitProperties();
//        rabbitProperties.setHost(host);
//        rabbitProperties.setPort(port);
//        rabbitProperties.setUsername(username);
//        rabbitProperties.setPassword(password);
//        rabbitProperties.setVirtualHost(virtualHost);
//        return rabbitProperties;
//    }

    @Bean(name = "syncConnectionFactory")
    public CachingConnectionFactory connectionFactory(SyncRabbitProperties syncRabbitProperties) {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(syncRabbitProperties.getHost(), syncRabbitProperties.getPort());
        connectionFactory.setUsername(syncRabbitProperties.getUsername());
        connectionFactory.setPassword(syncRabbitProperties.getPassword());
        connectionFactory.setVirtualHost(syncRabbitProperties.getVirtualHost());
//        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        connectionFactory.getRabbitConnectionFactory().setRequestedChannelMax(1000);
        connectionFactory.getRabbitConnectionFactory().setConnectionTimeout(60);
//        connectionFactory.setPublisherReturns(true);
//        connectionFactory.setPublisherConfirmType(CachingConnectionFactory.ConfirmType.CORRELATED);
//        connectionFactory.setChannelCacheSize(10);
//        connectionFactory.setConnectionCacheSize(10);

        return connectionFactory;
    }

    @Bean(name = "syncRabbitAdmin")
    public RabbitAdmin rabbitAdmin(@Qualifier("syncConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean(name = "syncRabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("syncConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitTemplate syncRabbitTemplate = new RabbitTemplate(connectionFactory);
        syncRabbitTemplate.setMandatory(true);
        syncRabbitTemplate.setConfirmCallback((correlationData, ack, s) -> {
            if (!ack) {
//                    LOGGER.info("{} 发送RabbitMQ消息 ack确认 失败: [{}]", this.name, JSON.toJSONString(object));
            } else {
//                    LOGGER.info("{} 发送RabbitMQ消息 ack确认 成功: [{}]", this.name, JSON.toJSONString(object));
            }
        });
        syncRabbitTemplate.setReturnCallback((message, code, s, exchange, routingKey) -> {
//                LOGGER.error("{} 发送RabbitMQ消息returnedMessage，出现异常，Exchange不存在或发送至Exchange却没有发送到Queue中，message：[{}], code[{}], s[{}], exchange[{}], routingKey[{}]", new Object[]{this.name, JSON.toJSONString(message), JSON.toJSONString(code), JSON.toJSONString(s), JSON.toJSONString(exchange), JSON.toJSONString(routingKey)});
        });
        return syncRabbitTemplate;
    }

}
