package com.satyam.securecapita.user.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping(value = "/error", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Map<String, Object> errorAttributes = new LinkedHashMap<>();

        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Throwable error = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String exceptionClassName =error.getCause().getClass().getName();
        String exceptionMessage =error.getCause().getMessage();
        errorAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:SS")));
        errorAttributes.put("status", status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR.value());
//        errorAttributes.put("exception", exceptionClassName != null ? exceptionClassName : "Unknown exception");
        errorAttributes.put("exceptionMessage", exceptionMessage != null ? exceptionClassName+": "+exceptionMessage : "Unknown exception");
        errorAttributes.put("message", errorMessage != null ? errorMessage : "An unexpected error occurred.");
        errorAttributes.put("exception", error != null ? error.toString() : null);

        return ResponseEntity.status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorAttributes);
    }

//    @Override
//    public String getErrorPath() {
//        return "/error";
//    }
}
