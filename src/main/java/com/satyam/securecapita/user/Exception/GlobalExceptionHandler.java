package com.satyam.securecapita.user.Exception;

import com.satyam.securecapita.infrastructure.data.ApplicationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/*
*  Global class to handle generic exception thrown by controller level
* */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApplicationResponse<String>> handleApplicationException(ApplicationException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApplicationResponse.getSuccessResponse(null,HttpStatus.BAD_REQUEST.value(),ex.getMessage()));
    }
}
