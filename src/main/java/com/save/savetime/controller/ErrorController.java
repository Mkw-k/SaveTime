package com.save.savetime.controller;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @RequestMapping(value = "/error")
    public ModelAndView handleNoHandlerFoundException(HttpServletResponse response, HttpServletRequest request, Model model) {
        Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        // int status = response.getStatus();

        ModelAndView modelAndView = new ModelAndView();

        if (statusCode != null) {
            Integer status = Integer.valueOf(statusCode.toString());
            if (status == HttpStatus.UNAUTHORIZED.value()) {
                modelAndView.addObject("errorCode", status);
                //modelAndView.setViewName("/error/401");
                modelAndView.setViewName("login");
            } else if (status == HttpStatus.NOT_FOUND.value()) {
                modelAndView.addObject("errorCode", status);
                modelAndView.setViewName("/error/404");
            } else if (status == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                modelAndView.addObject("errorCode", status);
                modelAndView.setViewName("/error/500");
            } else if (status == HttpStatus.FORBIDDEN.value()) {
                modelAndView.setViewName("/error/403");
            } else {
                modelAndView.setViewName("/error/error");
            }
        }

        return modelAndView;
    }

}
