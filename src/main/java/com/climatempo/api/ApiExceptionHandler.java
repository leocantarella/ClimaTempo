package com.climatempo.api;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
        return body(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }


    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String,Object>> upstream(FeignException ex, HttpServletRequest req) {
        HttpStatus st = HttpStatus.resolve(ex.status());
        return body(st != null ? st : HttpStatus.BAD_GATEWAY, "Falha ao chamar serviço externo.", req);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> generic(Exception ex, HttpServletRequest req) {
        return body(HttpStatus.INTERNAL_SERVER_ERROR, "Erro inesperado.", req);
    }

    private ResponseEntity<Map<String,Object>> body(HttpStatus status, String msg, HttpServletRequest req) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", OffsetDateTime.now().toString(),
                "status", status.value(),
                "message", msg,
                "path", req.getRequestURI()
        ));
    }
}
