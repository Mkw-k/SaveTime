package com.save.savetime.security;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 세션 시간관리
 */
@Slf4j
public class SessionListner implements HttpSessionListener {
    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //HttpSessionListener.super.sessionCreated(se);
        log.info("세션생성" + se);
        se.getSession().setMaxInactiveInterval(60*60); // 60분
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("세션해제" + se);
        HttpSessionListener.super.sessionDestroyed(se);
    }
}
