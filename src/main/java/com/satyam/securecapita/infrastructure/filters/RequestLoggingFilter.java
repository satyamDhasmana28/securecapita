package com.satyam.securecapita.infrastructure.filters;

import com.satyam.securecapita.infrastructure.constants.ApplicationConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

//@WebFilter("/*")
public class RequestLoggingFilter implements Filter{
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String dateTime = LocalDateTime.now().format(dateTimeFormatter);
        String method = httpRequest.getMethod();
        String url = httpRequest.getRequestURL().toString();

        // Log the request details
        logger.info(dateTime + " " + method + " " + url);

        // Pass the request down the chain
        chain.doFilter(request, response);
    }

//    private void logRequestDetails(ContentCachingRequestWrapper request){
//        if(request.getRequestURI().startsWith(ApplicationConstants.BASE_PATH)){
//            String dateTime = LocalDateTime.now().format(dateTimeFormatter);
//            String methodType = request.getMethod();
//            String url = request.getRequestURL().toString();
//
//            logger.info("{} {} {}", dateTime, methodType, url);
//        }

        // Log request body (if any)
//        byte[] requestBody = request.getContentAsByteArray();
//        if (requestBody.length > 0) {
//            logger.info("Request Body: {}", new String(requestBody, request.getCharacterEncoding()));
//        }
//    }

//    private void logResponseDetails(ContentCachingResponseWrapper response) throws UnsupportedEncodingException {
//        logger.info("Response Status: {}", response.getStatus());
//
//        // Log response body (if any)
//        byte[] responseBody = response.getContentAsByteArray();
//        if (responseBody.length > 0) {
//            logger.info("Response Body: {}", new String(responseBody, response.getCharacterEncoding()));
//        }
//    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic
    }

    @Override
    public void destroy() {
        // Cleanup logic
    }
}
