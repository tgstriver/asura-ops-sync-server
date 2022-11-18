package com.asura.ops.sync.server.sync.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: huyuntao(Mars)
 * @date: 2022/8/8
 * @description: 类的描述
 */
@Getter
@AllArgsConstructor
public enum ChangeTypeEnum {

    INSERT(1, "插入"),
    UPDATE(2, "更新"),
    DELETE(3, "删除"),

    REPUSH(4, "补偿"),

    ;
    private Integer value;

    private String desc;
}
