package com.satyam.securecapita.infrastructure.data;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

/*
 *  This class signifies the
 * */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationResponse<T> {
    private T result;
    private int statusCode;
    private String message;

    //    success response generation methods
    public static ApplicationResponse<String> getSuccessResponse() {
        return new ApplicationResponse<>(null, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(T result) {
        return new ApplicationResponse<>(result, HttpStatus.OK.value(), ApplicationConstants.SUCCESS);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(T result, int statusCode) {
        return new ApplicationResponse<>(result, statusCode, ApplicationConstants.SUCCESS);
    }

    public static <T> ApplicationResponse<T> getSuccessResponse(T result, int statusCode, String message) {
        return new ApplicationResponse<>(result, statusCode, message);
    }

    //    failure response generation methods
    public static ApplicationResponse<String> getFailureResponse() {
        return new ApplicationResponse<>(null, HttpStatus.BAD_REQUEST.value(), ApplicationConstants.FAILURE);
    }

    public static <T> ApplicationResponse<T> getFailureResponse(T result) {
        return new ApplicationResponse<>(result, HttpStatus.BAD_REQUEST.value(), ApplicationConstants.FAILURE);
    }

    public static <T> ApplicationResponse<T> getFailureResponse(T result, int statusCode) {
        return new ApplicationResponse<>(result, statusCode, ApplicationConstants.FAILURE);
    }

    public static <T> ApplicationResponse<T> getFailureResponse(T result, int statusCode, String message) {
        return new ApplicationResponse<>(result, statusCode, message);
    }

}
