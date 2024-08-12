package com.satyam.securecapita.infrastructure.security;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityMisc {
    public static String getAuthorizeUrlWithoutAuth(){
        List<String> ll = new ArrayList();
        ll.add(ApplicationConstants.BASE_PATH+"/register");
        ll.add(ApplicationConstants.BASE_PATH+"/authenticate");
        return ll.stream().collect(Collectors.joining(","));
    }
}
