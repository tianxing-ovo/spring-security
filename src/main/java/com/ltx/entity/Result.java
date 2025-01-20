package com.ltx.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.ltx.enums.ErrorCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用响应对象
 *
 * @author tianxing
 */
@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class Result {

    // 状态码
    private Integer code;
    // 消息
    private String message;
    // 数据
    private Map<String, Object> data;

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 成功
     *
     * @param message 消息
     * @return 通用响应对象
     */
    public static Result success(String message) {
        return new Result(200, message);
    }

    /**
     * 失败
     *
     * @param errorCodeEnum 错误状态码枚举
     * @return 通用响应对象
     */
    public static Result fail(ErrorCodeEnum errorCodeEnum) {
        return new Result(errorCodeEnum.getCode(), errorCodeEnum.getMessage());
    }

    /**
     * 存放数据
     *
     * @param key   键
     * @param value 值
     * @return 通用响应对象
     */
    public Result put(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
        return this;
    }
}
