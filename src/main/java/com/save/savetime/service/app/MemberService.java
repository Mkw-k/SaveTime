package com.save.savetime.service.app;

import com.save.savetime.model.dto.UserDto;
import com.save.savetime.model.entity.Member;
import com.save.savetime.model.entity.Role;
import com.save.savetime.repository.LoginRepository;
import com.save.savetime.repository.RoleRepository;
import com.save.savetime.service.RoleService;
import com.save.savetime.util.ContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final LoginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;

    public boolean afterCertifyAction(HttpServletRequest request) {
        log.debug("진입성공!!");

        HttpSession session = request.getSession();
        session.getAttribute("provider");

        //회원가입처리 - 현재는 회원가입밖에 없고, 분류도 카카오 밖에 없음
        boolean loginSuccessBool = createMember();
        return loginSuccessBool;
    }

    @Transactional
    public boolean createMember() {
        Member member = Member.builder()
                .email((String) ContextUtil.getAttrFromSession("id"))
                .name((String) ContextUtil.getAttrFromSession("name"))
                .remoteAddr((String) ContextUtil.getAttrFromSession("remoteAddr"))
                .password(passwordEncoder.encode("helperSns#passworddasdsadmksadnks"))
                .build();

        //최초 Role은 User로 설정
        Role role = roleRepository.findByRoleName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        member.setRole(roles);

        Member savedMember = loginRepository.save(member);

        if(savedMember != null){
            log.debug("저장성공");
            return true;
        }else {
            log.debug("저장실패");
            return false;
        }
    }

    @Transactional
    public boolean createMember(Member member) {
        //최초 Role은 User로 설정
        /*Role role = roleRepository.findByRoleName("ROLE_USER");
        Set<Role> roles = new HashSet<>();
        roles.add(role);*/

        /*Optional<Member> byEmail = loginRepository.findByEmail(member.getEmail());
        if(byEmail.isPresent()){
            member.setId(byEmail.get().getId());
        }*/

        //member.setRole(roles);
        Member savedMember = loginRepository.save(member);

        if(savedMember != null){
            log.debug("저장성공");
            return true;
        }else {
            log.debug("저장실패");
            return false;
        }
    }

    @Transactional
    public List<Member> getMembers() {
        List<Member> allMembers = loginRepository.findAll();
        return allMembers;
    }

    @Transactional
    public UserDto getMember(Long id) {
        ModelMapper modelMapper = new ModelMapper();
        //Member member = loginRepository.findById(String.valueOf(id)).orElse(new Member());
        Member member = loginRepository.findById(id).orElse(new Member());

        UserDto userDto = modelMapper.map(member, UserDto.class);

        List<String> roles = member.getRole()
                .stream()
                .map(role -> role.getRoleName())
                .collect(Collectors.toList());

        userDto.setRoles(roles);
        return userDto;
    }

    @Transactional
    public void deleteMember(Long id) {
        loginRepository.deleteById(id);
    }

    public Optional<Member> getMemberByEmail(String Email){
        return loginRepository.findByEmail(Email);
    }
}
