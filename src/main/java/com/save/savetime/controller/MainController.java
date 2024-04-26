package com.save.savetime.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;


@Controller
public class MainController {

    @GetMapping("/")
    public String getMain(){
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

    @GetMapping("/login")
    public String getLogin(HttpServletRequest request) {
        return "login";
    }
}
