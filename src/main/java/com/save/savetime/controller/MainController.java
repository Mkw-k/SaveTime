package com.save.savetime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MainController {

    @GetMapping("/")
    public String getMain(){
        return "login";
    }

    @GetMapping("/callback")
    public String handleCallback(HttpServletRequest request) {
        return "login";
    }

    @GetMapping("/youtube")
    public String getYoutube(HttpServletRequest request) {
        return "youtube";
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

    @GetMapping("/listing-details")
    public String getListingDetails(HttpServletRequest request) {
        return "listing_details";
    }

    @GetMapping("/login")
    public String getLogin(HttpServletRequest request) {
        return "login";
    }
}
