package com.save.savetime.security.jwt;

import com.save.savetime.common.Functions;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 */
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // JWT 토큰 삭제 로직 추가
        Cookie accessTokenCookie = new Cookie(JwtProperties.ACCESS_TOKEN_STRING, null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
//        accessTokenCookie.setSecure(true); // HTTPS 사용시 true

        Cookie refreshTokenCookie = new Cookie(JwtProperties.REFRESH_TOKEN_STRING, null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
//        refreshTokenCookie.setSecure(true); // HTTPS 사용시 true

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);

        Functions.setLoginRedirectUrl(request.getRequestURI(), response);

    }
}