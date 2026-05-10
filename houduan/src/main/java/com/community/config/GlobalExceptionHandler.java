package com.community.config;

import com.community.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<String> handleValidationException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getDefaultMessage()).append(";");
        }

        logger.error("validation failed: {}", errorMsg.toString());
        return Result.error(400, errorMsg.toString());
    }

    @ExceptionHandler(BindException.class)
    public Result<String> handleBindException(BindException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        StringBuilder errorMsg = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMsg.append(fieldError.getDefaultMessage()).append(";");
        }

        logger.error("bind failed: {}", errorMsg.toString());
        return Result.error(400, errorMsg.toString());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        logger.error("request body parse failed: {}", ex.getMessage());
        return Result.error(400, "请求体格式错误，请检查日期时间字段是否为 yyyy-MM-dd HH:mm:ss、yyyy-MM-dd HH:mm 或 ISO-8601 格式");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException ex) {
        logger.error("duplicate key: {}", ex.getMessage());
        return Result.error(400, "数据已存在，请检查后重试");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("illegal argument: {}", ex.getMessage());
        return Result.error(400, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException ex) {
        logger.error("runtime exception", ex);
        return Result.error(500, "服务器内部错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        logger.error("system exception", ex);
        return Result.error(500, "系统繁忙，请稍后重试");
    }
}
