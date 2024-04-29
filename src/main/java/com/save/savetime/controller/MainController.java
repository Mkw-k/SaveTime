package com.save.savetime.controller;

import com.save.savetime.common.AuthMember;
import com.save.savetime.model.entity.Member;
import com.save.savetime.service.YoutubeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final YoutubeService youtubeService;

    //메인페이지 이동시
    @GetMapping("/")
    public String getMain(@AuthMember Member member, Model model){
        log.debug("아이디 >>> {}", member.getEmail());
        //본인의 유튜브 리스트 받아오기
        //List<String> myYouTubeListById = youtubeService.getMyYouTubeListById(member.getEmail());
        //model.addAttribute("youTubeList", myYouTubeListById);

        return "index";
    }

    @GetMapping("/callback")
    public String handleCallback(HttpServletRequest request) {
        return "index";
    }

    @GetMapping("/index")
    public String getIndex(HttpServletRequest request) {
        return "index";
    }

    @GetMapping("/about")
    public String getAbout(HttpServletRequest request) {
        return "about";
    }

    @GetMapping("/catagori")
    public String getCatagori(HttpServletRequest request) {
        return "catagori";
    }

    @GetMapping("/blog")
    public String getBlog(HttpServletRequest request) {
        return "blog";
    }

    @GetMapping("/blog-details")
    public String getBlogDetails(HttpServletRequest request) {
        return "blog_details";
    }

    @GetMapping("/elements")
    public String getElements(HttpServletRequest request) {
        return "elements";
    }

    @GetMapping("/listing")
    public String getListing(HttpServletRequest request, @RequestParam String listId)
    {
        return "listing";
    }

    @GetMapping("/listing-details")
    public String getListingDetails(HttpServletRequest request) {
        return "listing_details";
    }



}
