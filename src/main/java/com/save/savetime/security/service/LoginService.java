package com.save.savetime.security.service;

import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.model.entity.Member;
import com.save.savetime.repository.LoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

        Member member = loginRepository.findByEmail(email).orElse(null);
        ObjectNullChk(member);
        log.info("loadUserByUsername={}, mbId={}, mbPassword={}", email, member.getEmail(), member.getPassword());
        return new MemberAccount(member);
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
