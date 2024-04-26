package com.save.savetime.common;

import com.save.savetime.model.entity.Member;
import com.save.savetime.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserAuditorAware implements AuditorAware<Member> {
    private final MemberService memberService;
    @Override
    public Optional<Member> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Optional<Member> memberByEmail
                = memberService.getMemberByEmail(((UserDetails) authentication.getPrincipal()).getUsername());
        return memberByEmail;
    }
}