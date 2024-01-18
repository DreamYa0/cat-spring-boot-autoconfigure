package com.g7.framework.cat.context;

/**
 * @author dreamyao
 * @title
 * @date 2020-03-23 10:25
 * @since 1.0.0
 */
public class DubheContextFactory {

    public static final String CONTEXT_JSON_TYPE = "json";

    public static DubheContext getDubheContext(String type) {
        if (CONTEXT_JSON_TYPE.equals(type)) {
            return new DubheJsonContext();
        } else {
            //默认返回 json类型
            return new DubheJsonContext();
        }
    }
}
