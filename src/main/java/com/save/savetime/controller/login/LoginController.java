package com.save.savetime.controller.login;

import com.save.savetime.model.dto.MemberAccount;
import com.save.savetime.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;


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
    public String loginSuccess(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        //하디마 유튜브 페이지로 이동
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
