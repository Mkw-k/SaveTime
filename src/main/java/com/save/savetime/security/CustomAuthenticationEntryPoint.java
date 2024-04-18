package com.save.savetime.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 인증되지 않은 사용자 처리
 */
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private String role;

    public CustomAuthenticationEntryPoint(String role) {
        this.role = role;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.info("가입되지 않은 사용자 접근 / 세션해제" + role);
        String responseUrl = "/";
        switch (role) {
            case "DOCTOR" :     // 의사
                responseUrl = "/hospital/login";
                break;
            case "PHARMACIST" : // 약사
                responseUrl = "/pharmacy/login";
                break;
            case "ADMIN" :      // 관리자
                responseUrl = "/admin/login";
                break;
        }
        response.sendRedirect(responseUrl);
    }
}
