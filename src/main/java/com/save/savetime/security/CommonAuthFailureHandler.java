package com.save.savetime.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
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

        /*if (exception instanceof AuthenticationServiceException) {
            //"존재하지 않는 사용자입니다."
        } else if(exception instanceof BadCredentialsException) {
            //"아이디 또는 비밀번호가 틀립니다."
        } else if(exception instanceof LockedException) {
            //"잠긴 계정입니다."
        } else if(exception instanceof DisabledException) {
            //"비활성화된 계정입니다."
        } else if(exception instanceof AccountExpiredException) {
            //"만료된 계정입니다."
        } else if(exception instanceof CredentialsExpiredException) {
            //"비밀번호가 만료되었습니다."
        } else if (exception instanceof InternalAuthenticationServiceException) {
            //"시스템 문제로 인증처리에 실패했습니다"
        } else if (exception instanceof UsernameNotFoundException) {
            //"이름없음"
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            //인증 요청이 거부되었습니다.
        }*/
        if (exception instanceof BadCredentialsException) {
            errorCode = 1;
        } else if (exception.getCause() instanceof DisabledException) {
            // HospitalAuthenticationProvider.java에서 에러 발생 - 관리자 인증안된 의사, 약사 로그인시
            errorCode = 4;
        } else if (exception instanceof InternalAuthenticationServiceException) {
            errorCode = 2;
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {
            errorCode = 3;
        }

        log.info("로그인에러="+exception.toString());

        // login form 파라미터
        // Enumeration eParam = request.getParameterNames();
        // while (eParam.hasMoreElements()) {
        //     String pName = (String)eParam.nextElement();
        //     String pValue = request.getParameter(pName);
        //     log.info(pName + "=" + pValue);
        // }

        //errorMessage = URLEncoder.encode(errorMessage, "UTF-8"); // String

        // 리턴 url -> 전부다 이 URL 사용
        setDefaultFailureUrl("/app/ajaxemaillogin?error=true&code=" + errorCode);

//        switch (request.getParameter("target")) {
        /*switch ((String) ContextUtil.getAttrFromSession("userType")) {
            case "hospital" :   // 의사
//                setDefaultFailureUrl("/hospital/login?error=true&code=" + errorCode);
//                setDefaultFailureUrl("/hospital/ajax-login?error=true&code=" + errorCode);
                setDefaultFailureUrl("/hospital/ajaxemaillogin?error=true&code=" + errorCode);
                break;
            case "pharmacy" :   // 약사
                setDefaultFailureUrl("/pharmacy/ajaxemaillogin?error=true&code=" + errorCode);
                break;
            case "admin" :      // 관리자
                setDefaultFailureUrl("/admin/ajaxemaillogin?error=true&code=" + errorCode);
                break;
            case "member" :      // 멤버
                setDefaultFailureUrl("/app/ajaxemaillogin?error=true&code=" + errorCode);
                break;
          *//*  default :      //디폴트 설정 (방어)
                setDefaultFailureUrl("/app/emailLogin?error=true&code=" + errorCode);*//*
        }*/

        super.onAuthenticationFailure(request, response, exception);
    }


}