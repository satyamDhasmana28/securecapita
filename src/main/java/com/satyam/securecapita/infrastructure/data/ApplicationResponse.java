package com.satyam.securecapita.infrastructure.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/*
*  This class signifies the
* */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApplicationResponse<T> {
    private T result;
    private String statusCode;
}
