package com.asura.ops.sync.server.service.impl;

import com.asura.ops.sync.server.mapper.CfgMqMapper;
import com.asura.ops.sync.server.model.entity.CfgDbEntity;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asura.ops.sync.server.model.entity.CfgTableEntity;
import com.asura.ops.sync.server.service.CfgTableService;
import com.asura.ops.sync.server.mapper.CfgTableMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author zouyang
* @description 针对表【cfg_table(同步表信息)】的数据库操作Service实现
* @createDate 2022-08-02 17:12:03
*/
@Service
public class CfgTableServiceImpl extends ServiceImpl<CfgTableMapper, CfgTableEntity>
    implements CfgTableService{

}




