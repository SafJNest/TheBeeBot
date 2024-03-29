package com.safjnest.Utilities.Controller.Interface;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenValidationInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token == null || token.equals("")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not found.");
        }
        if (!isValidToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unvalid token.");
        }
        return true;
    }

    private boolean isValidToken(String token) {
        if (token.equals("Bearer 123")) {
            return true;
        }
        return false;
    }
}