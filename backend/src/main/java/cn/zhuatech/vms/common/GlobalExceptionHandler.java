/* Copyright 2026 上海如静知华信息科技有限公司 */
package cn.zhuatech.vms.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse<Void>> validation(MethodArgumentNotValidException exception) {
        var fieldError = exception.getBindingResult().getFieldErrors().stream().findFirst();
        var message = fieldError.map(error -> error.getField() + ": " + error.getDefaultMessage()).orElse("请求参数不合法");
        return ResponseEntity.badRequest().body(ApiResponse.error(message));
    }
}
