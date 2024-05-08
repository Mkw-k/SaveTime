package com.save.savetime.controller.login;

import com.save.savetime.common.AuthMember;
import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.model.dto.UserDto;
import com.save.savetime.model.dto.YoutubeListDTO;
import com.save.savetime.model.entity.Member;
import com.save.savetime.repository.RoleRepository;
import com.save.savetime.service.MemberService;
import com.save.savetime.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final YoutubeService youtubeService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @GetMapping(value = {"/certify/{mode}", "/certify/{mode}/{id}"})
    public String afterCertifyAction(HttpServletRequest request, Model model) throws Exception {
        boolean loginSuccessBool = memberService.afterCertifyAction(request);

        if(loginSuccessBool){
            return "index";
        }else{
            return "main";
        }
    }

    @GetMapping({"/login-success"})
    public String loginSuccess(Model model, @AuthMember Member member) throws Exception {
        //하디마 유튜브 페이지로 이동
        List<YoutubeListDTO> dbYoutubeLists = youtubeService.getMyYouTubeListByMemberIdxAtDB(member);
        model.addAttribute("dbYoutubeLists", dbYoutubeLists);

        return "index";
    }

    @GetMapping(value="/denied")
    public String accessDenied(@RequestParam(value = "exception", required = false) String exception, Principal principal, Model model) throws Exception {

        MemberAccount account = null;

        if (principal instanceof UsernamePasswordAuthenticationToken) {
            account = (MemberAccount) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        }
		/*else if(principal instanceof AjaxAuthenticationToken){
			account = (Member) ((AjaxAuthenticationToken) principal).getPrincipal();
		}*/

        model.addAttribute("username", account.getUsername());
        model.addAttribute("exception", exception);

        return "user/login/denied";
    }

    @RequestMapping(value="/login", method = RequestMethod.GET)
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception, Model model){
        model.addAttribute("error",error);
        model.addAttribute("exception",exception);
        return "about_login";
    }

    @GetMapping(value="/users")
    public String createUser() throws Exception {

        return "about_register";
    }

    /**
     * 회원가입
     * @param accountDto
     * @return
     * @throws Exception
     */
    @PostMapping(value="/users")
    public String createUser(@Valid UserDto accountDto, Errors errors, HttpServletRequest request) throws Exception {
        if(errors.hasErrors()){
            return "redirect:/500";
        }
        ModelMapper modelMapper = new ModelMapper();
        Member account = modelMapper.map(accountDto, Member.class);
        account.setPassword(passwordEncoder.encode(accountDto.getPassword()));

        boolean registerSuccessBool = memberService.createMember(account);

        //실패시
        if(!registerSuccessBool){
            return "redirect:/500";
        }

        return "redirect:/login";
    }

    ////////////////////////////////////하단은 테스트 url///////////////////////////////////////////////////////
    @GetMapping(value="/mypage")
    public String myPage(@AuthenticationPrincipal Member account, Authentication authentication, Principal principal) throws Exception {

        return "user/mypage";
    }

    @GetMapping(value="/messages")
    public String messages() throws Exception {

        return "user/messages";
    }


}
