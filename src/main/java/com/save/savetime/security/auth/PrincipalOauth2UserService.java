package com.save.savetime.security.auth;

import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.model.dto.TokenDTO;
import com.save.savetime.model.entity.Member;
import com.save.savetime.model.entity.Role;
import com.save.savetime.repository.LoginRepository;
import com.save.savetime.security.jwt.JwtProvider;
import com.save.savetime.service.common.JwtService;
import com.save.savetime.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.Set;

/**
 * loadUser
 */
@Service
@Transactional
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private Logger logger = LoggerFactory.getLogger("PrincipalOauth2UserService");
    @Autowired
    private LoginRepository loginRepository;
    @Autowired
    JwtService jwtService;

    @Autowired
    Environment env;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        String provider = userRequest.getClientRegistration().getRegistrationId();
        provider = provider.toLowerCase();

        String loginType = "";

        // if(provider.equals("google")){
        //     oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        // }
        if (provider.equals("kakao")) {
            logger.info("oAuth2UserInfo     : " + oAuth2UserInfo);
            logger.info("spring.config.activate.on-profile     : " + System.getProperty("spring.config.activate.on-profile"));
            logger.info("getDefaultProfiles     : "+ Arrays.toString(env.getDefaultProfiles()));
            logger.info("env.getActiveProfiles()     : " + Arrays.toString(env.getActiveProfiles()));
            logger.info("environment.acceptsProfiles      : " + env.acceptsProfiles(Profiles.of("prod", "dev")));
            logger.info("environment.getProperty     : " + env.getProperty("baseurl"));
            oAuth2UserInfo = new KaKaoUserInfo(oAuth2User.getAttributes());
            loginType = "K";
        } else if (provider.equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo(oAuth2User.getAttributes());
            loginType = "N";
        }

        // oAuth2UserInfo 리턴값
        // 네이버 { "resultCode":~, "message":~, "response": { "id":~, "email":~, "name":~, ... } }
        // 카카오 { id=아이디값, connected_at=2022-02-22T15:50:21Z, properties={nickname=이름},
        // kakao_account={
        //     profile_nickname_needs_agreement=false,
        //     profile={nickname=이름},
        //     has_email=true,
        //     email_needs_agreement=false,
        //     is_email_valid=true,
        //     is_email_verified=true,
        //     email=이메일}
        // }
        String providerId = oAuth2UserInfo.getProviderId();
        String userId = provider+"_"+providerId; // kakao_***, naver_***
        String userName = "";

        //TODO 널처리
        if(oAuth2UserInfo != null){
            LinkedHashMap properties = (LinkedHashMap) oAuth2UserInfo.getAttributes().get("properties");
            userName = (String) properties.get("nickname");
        }
            Optional<Member> optionalMember = loginRepository.findByEmail(userId);
            HttpServletResponse response = ContextUtil.getResponse();
            Member member = optionalMember.orElse(new Member());

        // DB에 없는 사용자면 회원가입 처리
        if (!optionalMember.isPresent()) {
            System.out.println("회원가입 처리!");

            String msg = ",provider=" + provider;
            msg += ",providerId=" + userId;
            msg += ",name=" + userName;
            throw new UsernameNotFoundException(msg);

            /*
            member = new Member();
            member.setLoginType(loginType);
            member.setMbNo(Functions.createMemberNo());
            member.setMbId(userId);
            member.setMbPassword("");
            member.setMbCertify("");
            member.setMbName(userName);
            member.setMbSex("남");
            member.setMbBirth("");
            member.setMbHp("");
            member.setLoginIp("");

            // DB등록
            int insertChk = memberMapper.signUpMember(member);
            if(insertChk < 1) {
                throw new AuthenticationCredentialsNotFoundException("");
            }
            */
        } else{

            // JWT 토큰 생성
            try {
                //Oauth2일경우에는 request에 쿠키 만들어서 넣어주기
                TokenDTO token = JwtProvider.createTokenInRespForOauth2(member.getEmail(), "member", response);
                //DB에 Refresh 토큰 저장
                jwtService.login(token);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        Set<Role> roleSet = member.getRole();

        return new MemberAccount(member, oAuth2User.getAttributes());
    }
}
