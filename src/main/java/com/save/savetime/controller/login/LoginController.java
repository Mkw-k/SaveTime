package com.save.savetime.controller.login;

import com.save.savetime.common.AuthMember;
import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.model.entity.Member;
import com.save.savetime.model.entity.YoutubeList;
import com.save.savetime.service.MemberService;
import com.save.savetime.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;
    private final YoutubeService youtubeService;


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
        List<YoutubeList> dbYoutubeLists = youtubeService.getMyYouTubeListByMemberIdx(member);
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

    @RequestMapping(value="/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "exception", required = false) String exception, Model model){
        model.addAttribute("error",error);
        model.addAttribute("exception",exception);
        return "login";
    }

}
