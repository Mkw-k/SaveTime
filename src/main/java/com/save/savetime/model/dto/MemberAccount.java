package com.save.savetime.model.dto;

import com.save.savetime.model.entity.Member;
import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

/**
 * 앱 회원정보 어댑터 클래스
 * https://sol-devlog.tistory.com/3
 */
@Getter
public class MemberAccount extends User implements OAuth2User {
    private Member member;
    private Map<String, Object> attributes; // SNS 로그인

    // 이메일로 로그인
    public MemberAccount(Member member) {
        super(member.getEmail(), member.getPassword(), member.getAuthorities());
        this.member = member;
    }

    // 간편로그인
    public MemberAccount(Member member, Map<String, Object> attributes) {
        super(member.getEmail(), member.getPassword(), member.getAuthorities());
        this.member = member;
        this.attributes = attributes;
    }

    /**
     * OAuth2User 구현
     * @return
     */
    @Override
    public String getName() {
        String sub = attributes.get("sub").toString();
        return sub;
    }
}
