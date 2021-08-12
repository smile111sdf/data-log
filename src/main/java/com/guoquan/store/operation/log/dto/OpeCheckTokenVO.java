package com.guoquan.store.operation.log.dto;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * @author ranmenglong
 */
@Data
public class OpeCheckTokenVO {

    private String success;
    private Integer code;
    private String msg;
    private OpeUserInfoVo data;
}
