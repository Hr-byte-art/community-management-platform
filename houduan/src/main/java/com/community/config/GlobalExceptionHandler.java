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
        return Result.error(400, "\u8bf7\u6c42\u4f53\u683c\u5f0f\u9519\u8bef\uff0c\u8bf7\u68c0\u67e5\u65e5\u671f\u65f6\u95f4\u5b57\u6bb5\u662f\u5426\u4e3a yyyy-MM-dd HH:mm:ss");
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> handleDuplicateKeyException(DuplicateKeyException ex) {
        logger.error("duplicate key: {}", ex.getMessage());
        return Result.error(400, "\u6570\u636e\u5df2\u5b58\u5728\uff0c\u8bf7\u68c0\u67e5\u540e\u91cd\u8bd5");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Result<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("illegal argument: {}", ex.getMessage());
        return Result.error(400, ex.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<String> handleRuntimeException(RuntimeException ex) {
        logger.error("runtime exception", ex);
        return Result.error(500, "\u670d\u52a1\u5668\u5185\u90e8\u9519\u8bef");
    }

    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception ex) {
        logger.error("system exception", ex);
        return Result.error(500, "\u7cfb\u7edf\u7e41\u5fd9\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5");
    }
}
