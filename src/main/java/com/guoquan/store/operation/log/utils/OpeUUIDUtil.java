package com.guoquan.store.operation.log.utils;

import java.util.UUID;

/**
 * @author LZH on 2020/9/17 16:31
 */
public class OpeUUIDUtil {

    public static String getUUID() {
       return UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }
}
