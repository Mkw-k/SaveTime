package com.save.savetime.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CommonAuthFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        Integer errorCode = 1;

        if (exception instanceof AuthenticationServiceException) {
            //"존재하지 않는 사용자입니다."
            errorCode = 2;
        } else if(exception instanceof BadCredentialsException) {
            //"아이디 또는 비밀번호가 틀립니다."
            errorCode = 3;
        } else if(exception instanceof LockedException) {
            //"잠긴 계정입니다."
            errorCode = 4;
        } else if(exception instanceof DisabledException) {
            //"비활성화된 계정입니다."
            errorCode = 5;
        } else if(exception instanceof AccountExpiredException) {
            //"만료된 계정입니다."
            errorCode = 6;
        } else if(exception instanceof CredentialsExpiredException) {
            //"비밀번호가 만료되었습니다."
            errorCode = 7;
        } else if (exception instanceof InternalAuthenticationServiceException) {
            //"시스템 문제로 인증처리에 실패했습니다"
            errorCode = 8;
        } else if (exception instanceof UsernameNotFoundException) {
            //"이름없음"
            errorCode = 9;
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            //인증 요청이 거부되었습니다.
            errorCode = 10;
        }
        else if (exception instanceof BadCredentialsException) {
            errorCode = 11;
        } else if (exception.getCause() instanceof DisabledException) {
            errorCode = 12;
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorCode = 13;
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorCode = 14;
        }

        log.info("로그인에러="+exception.toString());

        // 리턴 url -> 전부다 이 URL 사용
        setDefaultFailureUrl("/login?error=true&code=" + errorCode);
        super.onAuthenticationFailure(request, response, exception);
    }


}