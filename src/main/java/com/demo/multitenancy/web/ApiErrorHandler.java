package com.demo.multitenancy.web;

import com.demo.multitenancy.tenant.UnknownTenantException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(UnknownTenantException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> unknownTenant(UnknownTenantException ex) {
        return Map.of("error", "unknown tenant", "host", ex.getHost());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(NoSuchElementException ex) {
        return Map.of("error", "not found", "detail", String.valueOf(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(IllegalArgumentException ex) {
        return Map.of("error", "bad request", "detail", String.valueOf(ex.getMessage()));
    }
}
