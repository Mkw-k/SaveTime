package com.save.savetime.service.app;

import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.model.entity.Member;
import com.save.savetime.repository.LoginRepository;
import com.save.savetime.util.ContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {
    @Autowired
    private LoginRepository loginRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        /**
         * 스프링이 로그인 요청을 가로챌때 username, password변수 2개를 가로채는데
         * password 부분 처리는 알아서 처리,
         * username이 DB에 있는지 확인해줘야함
         */

        //유저 타입 확인
        String userType = (String) ContextUtil.getAttrFromSession("userType");

        //TODO Ajax 에러 리턴 처리 필요
        Member member = loginRepository.findByEmail(email).orElse(null);
        ObjectNullChk(member);
        log.info("loadUserByUsername=" + email + "/mbId=" + member.getEmail() + "/mbPassword=" + member.getPassword());
        return new MemberAccount(member);


        //throw new UsernameNotFoundException("회원정보 조회 실패");

        // 세션에 등록되는 Type (UserDetail)
        // UserDetail을 구현하고 있는 User객체 반환; 생성자로 아이디, 비번, Role
        /*return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles("MEMBER")
                .build();*/
        /*Member authMember = Member.builder()
                .mbId(member.getEmail())
                .mbPassword(member.getPassword())
                .role("MEMBER")
                .build();

        return authMember;*/
    }

    private static void chkAuthYn(String authYn) {
        if (!authYn.equals("Y")) {
            // 관리자 인증안됨
            throw new DisabledException("관리자 인증안됨");
        }
    }

    /**
     * 객체 NULL 체크 NULL이면 Throw
     * */
    private static void ObjectNullChk(Object object) {
        if (object == null) {
            log.info("에러!");
            throw new UsernameNotFoundException("회원정보 조회 실패");
        }
    }
}
