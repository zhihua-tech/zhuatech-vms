/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms.common;

import java.time.Instant;

public record ApiResponse<T>(boolean success, T data, String message, Instant timestamp) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, data, "ok", Instant.now()); }
    public static ApiResponse<Void> error(String message) { return new ApiResponse<>(false, null, message, Instant.now()); }
}
