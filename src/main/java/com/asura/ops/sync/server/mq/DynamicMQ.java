package com.asura.ops.sync.server.mq;

import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.service.CfgMqService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;

/**
 * @author: xieyue(paul)
 * @date: 2022/8/5 0005
 * @description:消息队列动态管理器
 */
@Component
@Slf4j
public class DynamicMQ {
    @Autowired
    @Qualifier("syncRabbitAdmin")
    private AmqpAdmin rabbitAdmin;

    @Autowired
    @Qualifier("syncRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @Autowired
    CfgMqService cfgMqService;


    @Autowired
    @Qualifier("syncConnectionFactory")
    private ConnectionFactory connectionFactory;


    /**
     * 构造时 自动根据数据库配置 创建消息队列
     */
    @PostConstruct
    public void initQueueByDB() {
        var listMqEntity = cfgMqService.list();
        if (Objects.isNull(listMqEntity) || listMqEntity.isEmpty()) {
            return;
        }

        listMqEntity.forEach(a -> {
            prepareCreateQueue(a);
        });

    }

    public void recreateQueue(List<CfgMqEntity> listMqEntity) {
        listMqEntity.forEach(a -> {
            prepareCreateQueue(a);
        });
    }


    /**
     * 根据数据库实体创建 交换机、消息队列、监听器 创建队列准备工作
     *
     * @param mqEntity
     */
    public void prepareCreateQueue(CfgMqEntity mqEntity) {
        String exchangeName = mqEntity.getExchangeName();
        String queueName = mqEntity.getQueueName();
        String routingKey = mqEntity.getRouteKey();

        //增量：实例化一个topic交换机
        Exchange exchange = initExchange(exchangeName, true);
        Queue queue = initQueue(queueName, true);
        bindExchangeAndQueue(exchange, queue, routingKey);

        //补偿队列相关
        String suff = "_all";
        Exchange exchangeAll = initExchange(exchangeName + suff, true);
        Queue queueAll = initQueue(queueName + suff, true);
        bindExchangeAndQueue(exchangeAll, queueAll, routingKey + "_" + mqEntity.getSyncClientCode());
    }

    /**
     * 实例化一个Exchage
     *
     * @param exchangeName
     * @param isDurable
     * @return
     */
    public Exchange initExchange(String exchangeName, Boolean isDurable) {
        return initExchange(exchangeName, isDurable, ExchangeTypes.TOPIC);
    }

    public Exchange initExchange(String exchangeName, Boolean isDurable, String exchangeTypes) {

        Exchange exchange = null;
        switch (exchangeTypes) {
            case ExchangeTypes.TOPIC:
                exchange = ExchangeBuilder.topicExchange(exchangeName).durable(isDurable).build();
                break;
            case ExchangeTypes.DIRECT:
                exchange = ExchangeBuilder.directExchange(exchangeName).durable(isDurable).build();
                break;
            case ExchangeTypes.FANOUT:
                exchange = ExchangeBuilder.fanoutExchange(exchangeName).durable(isDurable).build();
                break;
            case ExchangeTypes.HEADERS:
                exchange = ExchangeBuilder.headersExchange(exchangeName).durable(isDurable).build();
                break;
            default:
                break;
        }
        return exchange;
    }

    /**
     * 实例化一个队列
     *
     * @param queueName
     * @param isDurable
     * @return
     */
    public Queue initQueue(String queueName, Boolean isDurable) {
        if (isDurable) {
            return QueueBuilder.durable(queueName).build();
        } else {
            return QueueBuilder.nonDurable(queueName).build();
        }
    }

    /**
     * 通过routekey绑定exchange和queue
     *
     * @param exchange
     * @param queue
     * @param routingKey
     */
    public void bindExchangeAndQueue(Exchange exchange, Queue queue, String routingKey) {
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs());
    }

    /**
     * 创建队列 并将队列 通过routekey和交换器绑定
     *
     * @param queueName
     * @param routingKey
     * @param exchange
     * @return
     */
    public Queue createQueueAndBindExchange(String queueName, String routingKey, Exchange exchange) {
        //创建交换机
        rabbitAdmin.declareExchange(exchange);
        Queue queue = new Queue(queueName, true, false, false);
        // Queue queue = initQueue(queueName, true);
        //创建队列
        rabbitAdmin.declareQueue(queue);
        //队列绑定路由key
        rabbitAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs());
        return queue;

    }

    private RabbitListenerContainerFactory getRabbitListenerContainerFactory(ConnectionFactory connectionFactory, Integer concurrentConsumers) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        if (concurrentConsumers > 1) {
            factory.setPrefetchCount(5);
            factory.setConcurrentConsumers(concurrentConsumers);
            factory.setMaxConcurrentConsumers(concurrentConsumers * 2);
        } else {
            factory.setPrefetchCount(1);
            factory.setConcurrentConsumers(1);
            factory.setMaxConcurrentConsumers(1);
        }
        return factory;
    }

    /**
     * 删除指定名称的队列
     *
     * @param queueName
     */
    public void delQueue(String queueName) {
        rabbitAdmin.deleteQueue(queueName, true, true);
    }

    /**
     * 删除交换机
     *
     * @param exchangeName
     */
    public void delExchange(String exchangeName) {
        rabbitAdmin.deleteExchange(exchangeName);
    }

    /**
     * 项指定队列发送文本内容
     *
     * @param exchageName
     * @param routeKey
     * @param msg
     */
    public void sendMsg(String exchageName, String routeKey, String msg) {
        rabbitTemplate.convertAndSend(exchageName, routeKey, msg);
//        Channel channel = rabbitTemplate.getConnectionFactory().createConnection().createChannel(false);
//        try {
//            channel.basicPublish(exchageName, routeKey, null, msg.getBytes());
//            channel.close();
//        } catch (Exception exception) {
//            log.error("生产消息channel异常:" + exception.getMessage());
//        }
    }
}
