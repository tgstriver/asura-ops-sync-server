package com.asura.ops.sync.server.mq;

import com.asura.ops.sync.server.AsuraOpsSyncServerApp;
import com.asura.ops.sync.server.service.CfgMqService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Objects;

/**
 * @author: xieyue(paul)
 * @date: 2022/8/5 0005
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AsuraOpsSyncServerApp.class)
public class mqTester {
    @Autowired
    DynamicMQ mq;


    @Autowired
    CfgMqService cfgMqService;

    /**
     * 从数据读取队列信息 并往每个队列发送信息
     */
    @Test
    public void sendHello() {
        var listMqEntity = cfgMqService.list();
        if (Objects.isNull(listMqEntity) || listMqEntity.isEmpty())
            return;

        listMqEntity.forEach(a -> {
            mq.sendMsg(a.getExchangeName(), a.getRouteKey(), System.currentTimeMillis() + "hello mq!");
        });

    }
}
