package com.asura.ops.sync.server.service.impl;

import com.asura.ops.sync.server.model.entity.CfgClientEntity;
import com.asura.ops.sync.server.model.entity.CfgDbEntity;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.service.CfgMqService;
import com.asura.ops.sync.server.mapper.CfgMqMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * @author zouyang
 * @description 针对表【cfg_mq(同步队列定义配置)】的数据库操作Service实现
 * @createDate 2022-08-02 17:12:00
 */
@Service
public class CfgMqServiceImpl extends ServiceImpl<CfgMqMapper, CfgMqEntity> implements CfgMqService {

    @Autowired
    private CfgMqMapper mqMapper;

    @Override
    public CfgDbEntity queryDBForMq(CfgMqEntity mqEntity) {
        return mqMapper.queryDBForMq(mqEntity);
    }

    @Override
    public List<CfgMqEntity> getCfgMqList(Collection<Long> clientIds, Collection<Long> tableIds) {
        LambdaQueryWrapper<CfgMqEntity> queryWrapper = new LambdaQueryWrapper<>();
        return null;
    }

    /**
     * 获取所有队列信息
     *
     * @return
     */
    @Override
    public List<CfgMqEntity> getAllCfgMqList(){
       return this.list();
    }
}




