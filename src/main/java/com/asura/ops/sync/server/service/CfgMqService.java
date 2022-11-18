package com.asura.ops.sync.server.service;

import com.asura.ops.sync.server.model.entity.CfgDbEntity;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Collection;
import java.util.List;

/**
* @author zouyang
* @description 针对表【cfg_mq(同步队列定义配置)】的数据库操作Service
* @createDate 2022-08-02 17:12:00
*/
public interface CfgMqService extends IService<CfgMqEntity> {

    /**
     * 根据MQ队列信息 找到 监听的DB连接配置
     * @param mqEntity
     * @return
     */
    CfgDbEntity queryDBForMq(CfgMqEntity mqEntity);

    /**
     * 获取客户端MQ配置集合
     *
     * @return
     */
    List<CfgMqEntity> getCfgMqList(Collection<Long> clientIds,Collection<Long> tableIds);

    /**
     * 获取所有队列信息
     * @return
     */
    List<CfgMqEntity> getAllCfgMqList();

}
