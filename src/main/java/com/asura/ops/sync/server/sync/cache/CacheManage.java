package com.asura.ops.sync.server.sync.cache;

import cn.hutool.json.JSONUtil;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.service.CfgMqService;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/8
 * @description: 类的描述
 */
@Component
@Slf4j
public class CacheManage {

    private final static String MQ_CACHE_KEY = "CFG_MQ_CACHE_KEY";

    @Autowired
    private CfgMqService cfgMqService;

    private final LoadingCache<String, List<CfgMqEntity>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(60))
            .refreshAfterWrite(Duration.ofSeconds(60))
            .build(new CacheLoader<String, List<CfgMqEntity>>() {
                @Override
                public List<CfgMqEntity> load(String key) throws Exception {
                    List<CfgMqEntity> mqList = cfgMqService.getAllCfgMqList();
                    log.info("cfgMq缓存更新:{}", JSONUtil.toJsonStr(mqList));
                    return mqList;
                }
            });

    /**
     * 获取本地mq配置信息缓存
     *
     * @return
     */
    public List<CfgMqEntity> getCacheMqList() throws ExecutionException {
        return cache.get(MQ_CACHE_KEY);
    }
}
