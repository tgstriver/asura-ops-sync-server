package com.asura.ops.sync.server.model.entity.req;

import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 补偿请求参数定义
 * </p>
 *
 * @author Mars
 * @since 2022-08-05
 */
@Data
public class RepushReq {

    /**
     * 补偿开始时间
     */
    private String startTime;

    /**
     * 补偿结束时间
     */
    private String endTime;

    /**
     * 对那些客户端进行补偿
     */
    private List<CfgMqEntity> clients;
}
