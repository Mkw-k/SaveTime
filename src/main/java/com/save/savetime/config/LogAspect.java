package com.save.savetime.config;

import com.save.savetime.common.Functions;
import com.save.savetime.util.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@Slf4j
public class LogAspect {

    @Autowired
    Environment env;

    // helper 패키지 이하의 모든 메서드
    @Around("execution(* com.save.savetime.*.*.*.*(..))")
    public Object logging(ProceedingJoinPoint pjp) throws Throwable {
        log.info("start - " + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());
        Object result = pjp.proceed();
//        log.info("finished - " + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());
        return result;
    }

    @Before("execution(* com.save.savetime.controller..*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        String controllerName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String requestUrl = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
        String remoteAddr = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr();
        String clientIP = Functions.getClientIP(ContextUtil.getRequest());
//        log.info("[{}] {} - {}", controllerName, methodName, requestUrl);
        log.info("requestURL ▶▶▶ {} // {} // {}", env.getProperty("baseurl") + requestUrl, remoteAddr, clientIP);
//        log.info("requestURL ▶▶▶ {}", env.getProperty("baseurl") + requestUrl);
    }

    @Around("@annotation(TimeLog)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long end = System.currentTimeMillis();
        log.info(joinPoint.getSignature() + " 실행 시간: " + (end - start) + "ms");
        return proceed;
    }
}
