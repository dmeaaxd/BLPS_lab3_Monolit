package org.example.blps_lab3_monolit.jackson.handler;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnrecognizedPropertyException.class)
    public ResponseEntity<?> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Запрос содержит неопознанные параметры");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}

