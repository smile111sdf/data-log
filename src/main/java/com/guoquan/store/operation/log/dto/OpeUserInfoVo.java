package com.guoquan.store.operation.log.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * @author ranmenglong
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OpeUserInfoVo {
    private List<Long> roles;
    private Long userId;
    private String username;
}
