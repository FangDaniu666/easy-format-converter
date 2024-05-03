package com.daniu.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IOException.class, ExecutionException.class, InterruptedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleIOException() {
        return "转换失败," + "操作中发生异常";
    }

}

