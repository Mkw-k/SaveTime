package com.save.savetime.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 공통함수 */
@Slf4j
public class Functions {
    // 쿠키 가져오기
    public static String getCookieValue(HttpServletRequest request, String cookieName) throws UnsupportedEncodingException {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return URLDecoder.decode(cookie.getValue(), "UTF-8");
                }
            }
        }
        return null;
    }

    // 쿠키 생성
    public static Cookie createCookieForJWT(String cookieName, String value, boolean bHttpOnly) throws UnsupportedEncodingException {

        Cookie cookie = new Cookie(cookieName, URLEncoder.encode(value, "UTF-8"));
        cookie.setMaxAge(60*60*24*10); // 쿠키 만료 시간 (초)
        cookie.setHttpOnly(bHttpOnly); // HttpOnly 쿠키 설정
        cookie.setPath("/");
//        cookie.setSecure(true);

        return cookie;
    }

    //기본 로그아웃 리다이렉트 URL로 설정 - response
    public static void setLogoutRedirectUrl(String requestURI, HttpServletResponse response) throws IOException {
        log.info("▶▶▶ 로그아웃 처리");
        if(requestURI.startsWith("/hospital")){
            response.sendRedirect("/hospital/logout");
        }else if(requestURI.startsWith("/pharmacy")){
            response.sendRedirect("/pharmacy/logout");
        }else if(requestURI.startsWith("/admin")){
            response.sendRedirect("/admin/logout");
        }else{
            response.sendRedirect("/logout");
        }
    }

    // 기본 로그인 리다이렉트 URL로 설정 - response
    public static void setLoginRedirectUrl(String requestURI, HttpServletResponse response) throws IOException {
        if(requestURI.startsWith("/hospital")){
            response.sendRedirect("/hospital/login");
        }else if(requestURI.startsWith("/pharmacy")){
            response.sendRedirect("/pharmacy/login");
        }else if(requestURI.startsWith("/admin")){
            response.sendRedirect("/admin/login");
        }else{
            response.sendRedirect("/");
        }
    }

    public static String extractMonths(String input) {
        String month = "";
        String regex = "\\d{1,2}월"; // 1자 또는 2자 숫자 + "월" 패턴을 정규 표현식으로 정의

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        while (matcher.find()) {
            month = matcher.group();
        }

        return month;
    }

    public static String extractMatchingName(String transactionRecord, List<String> subcriberNameAllList) {
        for (String name : subcriberNameAllList) {
            if (transactionRecord.contains(name)) {
                return name;
            }
        }
        // 일치하는 이름이 없을 경우 빈 문자열("")을 반환
        return "";
    }

    public static String extractMatchingCategory(String transactionRecord, List<String> subcriberNameAllList) {
        for (String name : subcriberNameAllList) {
            if (transactionRecord.contains(name)) {
                return name;
            }
        }
        // 일치하는 이름이 없을 경우 빈 문자열("")을 반환
        return "";
    }

    /**
     * @methodName    : getClientIP
     * @description   : 클라이언트의 IP주소 추출
     * <pre>
     *  X-FORWARDED-FOR 값이 없을 경우에는 remoteAddr을 추출해낸다
     * </pre>
     * @Param request
     * @Return string
     * */
    public static String getClientIP(HttpServletRequest request) {
        String remoteAddr = "";
        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }
        return remoteAddr;
    }



}
