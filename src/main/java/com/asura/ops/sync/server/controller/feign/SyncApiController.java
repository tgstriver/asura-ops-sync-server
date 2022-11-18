package com.asura.ops.sync.server.controller.feign;

import com.asura.base.response.FeignResponseBase;
import com.asura.ops.sync.api.SyncApi;
import com.asura.ops.sync.api.model.CfgClientDto;
import com.asura.ops.sync.api.model.ErrorReportDto;
import com.asura.ops.sync.server.service.CfgClientService;
import com.asura.ops.sync.server.service.ChangeInfoMqService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

/**
 * @author: zouyang
 * @date: 2022/8/1
 * @description: 同步配置入口
 */
@RestController
@Api(value = "SyncApiController", tags = "同步配置入口")
public class SyncApiController implements SyncApi {

    @Autowired
    private CfgClientService cfgClientService;

    @Autowired
    private ChangeInfoMqService changeInfoMqService;

    @ApiOperation("根据客户端code获取客户端同步配置")
    @ApiImplicitParam(name = "clientCode", value = "客户端编码", required = true)
    public FeignResponseBase<CfgClientDto> getCfgClient(String clientCode) {
        return FeignResponseBase.success(cfgClientService.getCfgClientDto(Arrays.asList(clientCode)));
    }

    @ApiOperation("客户端消息处理失败上报")
    @Override
    public FeignResponseBase<CfgClientDto> errorReport(ErrorReportDto errorReport) {

        changeInfoMqService.updateConsumerStatus(errorReport);
        return FeignResponseBase.success();
    }

}
