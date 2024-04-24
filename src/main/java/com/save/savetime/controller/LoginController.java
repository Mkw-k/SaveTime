package com.save.savetime.controller;

import com.save.savetime.service.app.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class LoginController {
    private final MemberService memberService;


    @GetMapping(value = {"/certify/{mode}", "/certify/{mode}/{id}"})
    public String afterCertifyAction(HttpServletRequest request, Model model) throws Exception {
        boolean loginSuccessBool = memberService.afterCertifyAction(request);

        if(loginSuccessBool){
            return "login_success";
        }else{
            return "main";
        }
    }



    @GetMapping({"/login-success"})
    public String loginSuccess(Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) throws Exception {
        //하디마 유튜브 페이지로 이동
        return "index";
    }

}
