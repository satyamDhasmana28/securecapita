package com.satyam.securecapita.user.controller;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Objects;
@RestController
@RequestMapping(ApplicationConstants.BASE_PATH+"/say")
public class Test {
    @PostMapping
    public String set(HttpSession session){
        session.setAttribute("count", Objects.isNull(session.getAttribute("count"))?1:(Integer)session.getAttribute("count")+1);
        return "success";
    }

    @GetMapping
    public String get(HttpSession session){
        return "count is: "+session.getAttribute("count");
    }
}
