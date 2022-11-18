package com.asura.ops.sync.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.asura.ops.sync.server.model.entity.ChangeInfoEntity;
import com.asura.ops.sync.server.service.ChangeInfoService;
import com.asura.ops.sync.server.mapper.ChangeInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author zouyang
* @description 针对表【change_info(变更信息)】的数据库操作Service实现
* @createDate 2022-08-02 17:12:06
*/
@Service
public class ChangeInfoServiceImpl extends ServiceImpl<ChangeInfoMapper, ChangeInfoEntity>
    implements ChangeInfoService{

}




