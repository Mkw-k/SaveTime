package com.save.savetime.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.save.savetime.common.Functions;
import com.save.savetime.model.dto.TokenDTO;
import com.save.savetime.model.entity.RefreshToken;
import com.save.savetime.util.ContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.security.auth.Subject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Instant;
import java.util.Date;

/**
 * https://velog.io/@jkijki12/Jwt-Refresh-Token-%EC%A0%81%EC%9A%A9%EA%B8%B0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    @Autowired
    ObjectMapper objectMapper;


    public Subject getSubject(String atk) throws JsonProcessingException {
        DecodedJWT decodedJwt = JWT.decode(atk);
        String subjectStr = decodedJwt.getSubject();

        return objectMapper.readValue(subjectStr, Subject.class);
    }

    public String reCreateAccessTokenAfterValidateRefreshToken(RefreshToken refreshTokenObj) throws Exception {
        log.info("▶▶▶ Access 토큰 재발급");
        // refresh 객체에서 refreshToken 추출
        String refreshToken = refreshTokenObj.getRefreshToken();

        try {
            // 검증 : HMAC512 써야함 HMAC256 도 있음 주의!!
            Algorithm algorithm = Algorithm.HMAC512(JwtProperties.REFRESH_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decodedJWT = verifier.verify(refreshToken);

            //refresh 토큰의 만료시간이 지나지 않았을 경우, 새로운 access 토큰을 생성합니다.
            if (!decodedJWT.getExpiresAt().before(new Date())) {
                Claim userEmailClaim = decodedJWT.getClaim("username");
                String userEmail = userEmailClaim.asString();
                Claim userIdClaim = decodedJWT.getClaim("id");
                String userId = userEmailClaim.asString();
//                Claim rolesClaim = decodedJWT.getClaim("roles");
//                List<String> roles = rolesClaim.asList(String.class);
                return recreationAccessToken(userEmail, userId);
            }
        } catch (JWTVerificationException exception){
            //refresh 토큰이 만료되었을 경우, 로그인이 필요합니다.
            throw new Exception("토큰 정보가 올바르지 않습니다");
            //TODO 로그인페이지로 이동
        }

        return null;
    }

    public String recreationAccessToken(String userEmail, String id){

        String accessToken = JWT.create()
                .withSubject(userEmail)
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("id", id)
                .withClaim("username", userEmail)
                .sign(Algorithm.HMAC512(JwtProperties.ACCESS_SECRET));

        return accessToken;
    }

    public static TokenDTO createTokenInResp(String id, String userType, HttpServletResponse response) throws UnsupportedEncodingException {

        TokenDTO token = createToken(id, userType);

        // 쿠키 생성
        Cookie accessTokenCookie =
            Functions.createCookieForJWT(JwtProperties.ACCESS_TOKEN_STRING,  URLEncoder.encode(JwtProperties.TOKEN_PREFIX + token.getAccessToken(), "UTF-8"), true);

//                = new Cookie("Authorization", URLEncoder.encode(JwtProperties.TOKEN_PREFIX + token.getAccessToken(), "UTF-8"));
//		accessTokenCookie.setMaxAge(60 * 60); // 쿠키 만료 시간 (초)
//        accessTokenCookie.setHttpOnly(true); // HttpOnly 쿠키 설정

        Cookie RefreshTokenCookie =
                Functions.createCookieForJWT(JwtProperties.REFRESH_TOKEN_STRING,  URLEncoder.encode(JwtProperties.TOKEN_PREFIX + token.getRefreshToken(), "UTF-8"), true);

//        Cookie RefreshTokenCookie
//                = new Cookie("ReAuthorization", URLEncoder.encode(JwtProperties.TOKEN_PREFIX + token.getRefreshToken(), "UTF-8"));
//		RefreshTokenCookie.setMaxAge(60 * 60); // 쿠키 만료 시간 (초)
//        RefreshTokenCookie.setHttpOnly(true); // HttpOnly 쿠키 설정

        // 응답 헤더에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(RefreshTokenCookie);

//        response.addHeader(JwtProperties.ACCESS_TOKEN_STRING, JwtProperties.TOKEN_PREFIX+accessToken);
//        response.addHeader(JwtProperties.REFRESH_TOKEN_STRING, JwtProperties.TOKEN_PREFIX+refreshToken);

        return token;
    }

    public static TokenDTO createTokenInRespForOauth2(String id, String userType, HttpServletResponse response) throws UnsupportedEncodingException {

        TokenDTO token = createToken(id, userType);

        // 쿠키 생성
        Cookie accessTokenCookie =
                Functions.createCookieForJWT(JwtProperties.ACCESS_TOKEN_STRING,  URLEncoder.encode(JwtProperties.TOKEN_PREFIX + token.getAccessToken(), "UTF-8"), true);

        Cookie RefreshTokenCookie =
                Functions.createCookieForJWT(JwtProperties.REFRESH_TOKEN_STRING,  URLEncoder.encode(JwtProperties.TOKEN_PREFIX + token.getRefreshToken(), "UTF-8"), true);

        // 응답 헤더에 쿠키 추가
        response.addCookie(accessTokenCookie);
        response.addCookie(RefreshTokenCookie);

        ContextUtil.setAttrToSession("userType", "member");

        // 세션에도 추가
//        ContextUtil.setAttrToSession(JwtProperties.ACCESS_TOKEN_STRING, URLEncoder.encode(JwtProperties.TOKEN_PREFIX+token.getAccessToken(), "UTF-8"));
//        ContextUtil.setAttrToRequest(JwtProperties.REFRESH_TOKEN_STRING, URLEncoder.encode(JwtProperties.TOKEN_PREFIX+token.getRefreshToken(), "UTF-8"));

//        response.addHeader(JwtProperties.ACCESS_TOKEN_STRING, JwtProperties.TOKEN_PREFIX+accessToken);
//        response.addHeader(JwtProperties.REFRESH_TOKEN_STRING, JwtProperties.TOKEN_PREFIX+refreshToken);

        return token;
    }

    private static TokenDTO createToken(String id, String userType) throws UnsupportedEncodingException {
        String accessToken = JWT.create()
                .withSubject(id)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.ACCESS_EXPIRATION_TIME))
                .withClaim("id", id)
//				.withClaim("username", userCommonDTO.getMbName())
                .withClaim("usertype", userType)
                .sign(Algorithm.HMAC512(JwtProperties.ACCESS_SECRET));

        String refreshToken = JWT.create()
                .withSubject(id)
                .withIssuedAt(Date.from(Instant.now()))
                .withExpiresAt(new Date(System.currentTimeMillis()+JwtProperties.REFRESH_EXPIRATION_TIME))
                .withClaim("id", id)
//				.withClaim("username", userCommonDTO.getMbName())
                .withClaim("usertype", userType)
                .sign(Algorithm.HMAC512(JwtProperties.REFRESH_SECRET));

        return TokenDTO.builder()
                .refreshToken(refreshToken)
                .accessToken(accessToken)
                .key(id)
                .build();
    }

}