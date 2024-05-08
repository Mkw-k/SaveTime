package com.save.savetime.controller.admin;


import com.save.savetime.common.AuthMember;
import com.save.savetime.model.dto.UserDto;
import com.save.savetime.model.entity.Member;
import com.save.savetime.model.entity.Role;
import com.save.savetime.repository.RoleRepository;
import com.save.savetime.security.service.RoleService;
import com.save.savetime.service.MemberService;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
public class UserManagerController {
	
	@Autowired
	private MemberService memberService;

	@Autowired
	private RoleService roleService;
	@Autowired
	private RoleRepository roleRepository;

	@GetMapping(value={"/admin/accounts"})
	public String getUsers(Model model) throws Exception {
		List<Member> allMembers = memberService.getMembers();
		model.addAttribute("accounts", allMembers);
		return "admin/user/list";
	}

	@PostMapping(value="/admin/accounts")
	public String createUser(UserDto userDto) throws Exception {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
		Member member = modelMapper.map(userDto, Member.class);

		member.setEmail(userDto.getEmail());

		if(!userDto.getPassword().isEmpty()){
			member.setPassword(userDto.getPassword());
		}

		Role role = roleRepository.findByRoleName(userDto.getRoleName());

		String email = userDto.getEmail();
		Optional<Member> memberByEmail = memberService.getMemberByEmail(email);

		if(memberByEmail.isPresent()){//업데이트일경우
			member.setIdx(memberByEmail.get().getIdx());
		}

		Set<Role> newRole = new HashSet<>();
		newRole.add(role);
		member.setRole(newRole);
		memberService.createMember(member);
		return "redirect:/admin/accounts";
	}

	@GetMapping(value = "/admin/accounts/{email}")
	public String getUser(@PathVariable(value = "email") String email, Model model) {
		String roleName = "";
		UserDto userDto = memberService.getMember(email);
		List<Role> roleList = roleService.getRoles();

		model.addAttribute("act", (email != "")? "modify":"add");
		model.addAttribute("account", userDto);
		model.addAttribute("roleList", roleList);

		return "admin/user/detail";
	}

	@DeleteMapping(value = "/admin/accounts/delete/{id}")
	public String removeUser(@PathVariable(value = "id") Long id, Model model) {
		memberService.deleteMember(id);
		return "redirect:/admin/users";
	}

	@GetMapping(value = "/admin/main")
	public String adminHome(@AuthMember Member member){
		return "admin/index";
	}

	@GetMapping("/admin-home")
	public String getAdminHomePage(){
		return "redirect:/admin/accounts";
	}
}
