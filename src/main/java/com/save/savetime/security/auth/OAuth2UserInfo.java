package com.save.savetime.security.auth;

import java.util.Map;

/**
 * 간편로그인 별 사용자정보 받는 인터페이스
 */

public interface OAuth2UserInfo {
    Map<String, Object> getAttributes();
    String getProviderId();
}
