package com.asura.ops.sync.server.controller.web;

import com.asura.common.response.ResponseBase;
import com.asura.ops.sync.server.model.entity.CfgMqEntity;
import com.asura.ops.sync.server.model.entity.req.RepushReq;
import com.asura.ops.sync.server.service.ManageService;
import com.asura.ops.sync.server.sync.DebeziumTools;
import com.asura.ops.sync.server.sync.model.OffsetInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @author: zouyang
 * @date: 2022/8/2
 * @description: 测试controller
 */
@RestController
@RequestMapping("/manage")
@Slf4j
@Api(value = "ManageController", tags = "管理端接口")
public class ManageController {

    @Autowired
    private ManageService manageService;

    @GetMapping("/start")
    public ResponseBase start(@RequestParam("serverId") String serverId) {
        return DebeziumTools.start(serverId, false);
    }

    @GetMapping("/stop")
    public ResponseBase stop(@RequestParam("serverId") String serverId) throws IOException {
        return DebeziumTools.stop(serverId);
    }

    @GetMapping("/restart")
    public ResponseBase restart(@RequestParam("serverId") String serverId) throws IOException {
        DebeziumTools.refresh(serverId);
        return ResponseBase.success();
    }

    @PostMapping("/changeOffset")
    @ApiOperation(value = "修改offset")
    public ResponseBase changeOffset(@RequestParam("serverId") String serverId, @RequestBody OffsetInfo offsetInfo) throws IOException {
        DebeziumTools.changeOffset(serverId, offsetInfo);
        return ResponseBase.success();
    }


    @PostMapping("/repush")
    @ApiOperation(value = "补偿推送")
    public ResponseBase repush(@RequestBody RepushReq repushReq) throws IOException {
        if (ObjectUtils.isEmpty(repushReq.getStartTime()) || ObjectUtils.isEmpty(repushReq.getEndTime())) {
            return ResponseBase.fail("参数非法 startTime | endTime 是空");
        }

        if (ObjectUtils.isEmpty(repushReq.getClients())) {
            return ResponseBase.fail("参数非法 clients 是空");
        }

        for (CfgMqEntity mqEntity : repushReq.getClients()) {
            if (ObjectUtils.isEmpty(mqEntity.getExchangeName())
                    || ObjectUtils.isEmpty(mqEntity.getSyncTableName())
                    || ObjectUtils.isEmpty(mqEntity.getSyncDbName())
                    || ObjectUtils.isEmpty(mqEntity.getRouteKey())
                    || ObjectUtils.isEmpty(mqEntity.getSyncClientCode())) {
                return ResponseBase.fail("参数非法 exchangeName | syncTableName | syncDbName | routeKey | syncClientCode 是空");
            }
        }

        manageService.repush(repushReq);

        return ResponseBase.success();
    }
}
