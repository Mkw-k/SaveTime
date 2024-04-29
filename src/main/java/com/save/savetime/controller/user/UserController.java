package com.save.savetime.controller.user;


import com.save.savetime.model.dto.UserDto;
import com.save.savetime.model.entity.Member;
import com.save.savetime.repository.RoleRepository;
import com.save.savetime.service.MemberService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class UserController {
	
	@Autowired
	private MemberService userService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private RoleRepository roleRepository;

	@GetMapping(value="/users")
	public String createUser() throws Exception {

		return "user/login/register";
	}

	@PostMapping(value="/users")
	public String createUser(UserDto accountDto) throws Exception {

		ModelMapper modelMapper = new ModelMapper();
		Member account = modelMapper.map(accountDto, Member.class);
		account.setPassword(passwordEncoder.encode(accountDto.getPassword()));

		userService.createMember(account);

		return "redirect:/";
	}

	@GetMapping(value="/mypage")
	public String myPage(@AuthenticationPrincipal Member account, Authentication authentication, Principal principal) throws Exception {

		return "user/mypage";
	}
}
