/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
package cn.zhuatech.vms.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException exception) {
        var fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst();
        var message = fieldError.map(error -> error.getField() + ": " + error.getDefaultMessage()).orElse("请求参数不合法");
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }

    /**
     * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。
     */
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiResponse<Void>> business(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode())
            .body(ApiResponse.error(exception.getReason() == null ? "业务操作失败" : exception.getReason()));
    }
}
