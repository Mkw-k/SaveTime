package com.save.savetime.security.service;

/**
 */

import com.save.savetime.model.dto.TokenDTO;
import com.save.savetime.model.entity.RefreshToken;
import com.save.savetime.repository.RefreshTokenRepository;
import com.save.savetime.security.jwt.JwtProperties;
import com.save.savetime.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void login(TokenDTO tokenDto){

        RefreshToken refreshToken = RefreshToken.builder().keyEmail(tokenDto.getKey()).refreshToken(tokenDto.getRefreshToken()).build();
        String loginUserEmail = refreshToken.getKeyEmail();
        if(refreshTokenRepository.findByKeyEmail(loginUserEmail).isPresent()){
            log.info("기존의 존재하는 refresh 토큰 삭제");
            refreshTokenRepository.deleteByKeyEmail(loginUserEmail);
        }
        refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> getRefreshToken(String refreshToken){

        return refreshTokenRepository.findByRefreshToken(refreshToken);
    }

    public Map<String, String> validateRefreshTokenInDbAndReCreationAccessToken(String refreshToken, HttpServletResponse response) throws Exception {
        //Bareer 제거
//        refreshToken = refreshToken.replace("Bearer ", "");

        //refresh DB 에 refreshToken 존재여부 확인
        RefreshToken refreshToken1 = getRefreshToken(refreshToken).orElseThrow(() -> new Exception("로그인 정보가 만료되었습니다 다시 로그인해주세요"));

        //refreshToken 실제 검증 - 검증 성공시 AccessToken 발급
        String createdAccessToken = jwtTokenProvider.reCreateAccessTokenAfterValidateRefreshToken(refreshToken1);

        return createRefreshJson(createdAccessToken, response);
    }

    public Map<String, String> createRefreshJson(String createdAccessToken, HttpServletResponse response){

        Map<String, String> map = new HashMap<>();
        if(createdAccessToken == null){

            map.put("errortype", "Forbidden");
            map.put("status", "402");
            map.put("message", "Refresh 토큰이 만료되었습니다. 로그인이 필요합니다.");
            return map;
        }

        map.put("status", "200");
        map.put("message", "Refresh 토큰을 통한 Access Token 생성이 완료되었습니다.");
        map.put("accessToken", createdAccessToken);

        //refreshToken을 통한 AccessToken 재발급후 저장
        response.addHeader(JwtProperties.ACCESS_TOKEN_STRING, JwtProperties.TOKEN_PREFIX+createdAccessToken);

        return map;
    }
}