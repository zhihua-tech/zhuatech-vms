/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.common;

import java.time.Instant;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
public record ApiResponse<T>(boolean success, T data, String message, Instant timestamp) {

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, "ok", Instant.now());
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    public static ApiResponse<Void> error(String message) {
        return new ApiResponse<>(false, null, message, Instant.now());
    }
}
