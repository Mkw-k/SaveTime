package com.save.savetime.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 권한 없는 사용자 접근 처리
 */
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private String role;

    public CustomAccessDeniedHandler(String role) {
        this.role = role;
    }


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.info("권한 없는 사용자 접근 처리" + role);
        String responseUrl = "/logout";
        switch (role) {
            case "DOCTOR" :     // 의사
                responseUrl = "/hospital/logout";
                break;
            case "PHARMACIST" : // 약사
                responseUrl = "/pharmacy/logout";
                break;
            case "ADMIN" :      // 관리자
                responseUrl = "/admin/logout";
                break;
        }
        response.sendRedirect(responseUrl);
    }
}
