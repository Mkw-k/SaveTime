package com.save.savetime.util;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ContextUtil {
    /**
     * 빈을 직접 얻습니다.
     *
     * @param beanName
     * @return
     */
    public static Object getBean(String beanName) {
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        return context.getBean(beanName);
    }

    /**
     * HttpServletReqeust 객체를 직접 얻습니다.
     * @return
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes attr =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        return attr.getRequest();
    }

    /**
     * HttpServletResponse 객체를 직접 얻습니다.
     * @return
     */
    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attr =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        return attr.getResponse();
    }

    /**
     * HttpSession 객체를 직접 얻습니다.
     *
     * @param gen 새 세션 생성 여부
     * @return
     */
    public static HttpSession getSession(boolean gen) {
        return ContextUtil.getRequest().getSession(gen);
    }

    /**
     * REQUEST 영역에서 가져오기
     *
     * @param key
     * @return
     */
    public static Object getAttrFromRequest(String key) {
        ServletRequestAttributes attr =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        return attr.getAttribute(key, ServletRequestAttributes.SCOPE_REQUEST);
    }

    /**
     * REQUEST 영역에 객체 저장
     *
     * @param key
     * @param obj
     */
    public static void setAttrToRequest(String key, Object obj) {
        ServletRequestAttributes attr =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        attr.setAttribute(key, obj, ServletRequestAttributes.SCOPE_REQUEST);
    }

    /**
     * SESSION 영역에서 가져오기
     *
     * @param key
     * @param isEnc 저장했을때 암호화여부
     * @return
     */
    public static Object getAttrFromSession(String key, boolean isEnc) {
        ServletRequestAttributes attr =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();

        if(isEnc == true){
            try {
                AES256Util aes256Util = new AES256Util();
                return aes256Util.decrypt(attr.getAttribute(key, ServletRequestAttributes.SCOPE_SESSION) + "");
            } catch (Exception e){

            }

        }
        return attr.getAttribute(key, ServletRequestAttributes.SCOPE_SESSION);
    }

    public static Object getAttrFromSession(String key) {
        return getAttrFromSession(key, true);
    }

    /**
     * Session 영역에 객체 저장
     *
     * @param key
     * @param obj
     * @param isEnc 암호화여부
     */
    public static void setAttrToSession(String key, Object obj, boolean isEnc) {
        ServletRequestAttributes attr =
                (ServletRequestAttributes)RequestContextHolder.currentRequestAttributes();
        if(isEnc == true){
            try {
                AES256Util aes256Util = new AES256Util();
                attr.setAttribute(key, aes256Util.encrypt(obj+""), ServletRequestAttributes.SCOPE_SESSION);
            } catch (Exception e){

            }
        } else {
            attr.setAttribute(key, obj, ServletRequestAttributes.SCOPE_SESSION);
        }
    }

    public static void setAttrToSession(String key, Object obj){
        setAttrToSession(key, obj, true);
    }

    public static void removeSessionAttribute(String key) {
        // 현재 요청의 HttpSession 객체를 가져옴
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // 세션에서 지정된 키값을 가진 속성을 삭제
        session.removeAttribute(key);
    }

    public static boolean hasSessionAttribute(String key) {
        // 현재 요청의 HttpSession 객체를 가져옴
        HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getSession();

        // 세션에서 지정된 키값을 가진 속성이 존재하는지 여부를 반환
        return session.getAttribute(key) != null;
    }

    public static void setSessionAttribute(HttpServletRequest request, String attributeName, Object attributeValue, int maxInactiveIntervalInSeconds) {
        HttpSession session = request.getSession();
        session.setAttribute(attributeName, attributeValue);
        session.setMaxInactiveInterval(maxInactiveIntervalInSeconds);
    }
}
