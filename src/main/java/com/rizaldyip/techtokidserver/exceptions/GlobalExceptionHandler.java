package com.rizaldyip.techtokidserver.exceptions;

import com.rizaldyip.techtokidserver.responses.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.UnknownHostException;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataNotFoundExceptions.class)
    public final ResponseEntity<Response<String>> handleDataNotFoundException(DataNotFoundExceptions ex) {
        return Response.failed(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Response<String>> handleValidationException(MethodArgumentNotValidException ex) {
        String errMsg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return Response.failed(HttpStatus.BAD_REQUEST.value(), "Unable to process request. Error" + errMsg);
    }

    public final ResponseEntity<Response<String>> handleAllExceptions(Exception ex) {
        log.error(ex.getMessage(), ex.getCause(), ex);

        if (ex.getCause() instanceof UnknownHostException) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), "Unable to process the request. Error: " + ex.getLocalizedMessage());
        }

        return Response.failed(HttpStatus.BAD_REQUEST.value(), "Unable to process the request. Error: " + ex.getMessage());
    }
}
