package com.limingzhu.community.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
@Controller
public class NotFoundExceptionController implements ErrorController {

    @Override
    public String getErrorPath() {
        return "/error";
    }

    /**
     * ExceptionHandler无法捕捉404错误，手动捕捉，并将错误信息返回
     * @param modelAndView
     * @param request
     * @return
     */
    @RequestMapping("/error")
    public ModelAndView error(ModelAndView modelAndView, HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
        if (httpStatus.is4xxClientError()) {
            modelAndView.addObject("message", "您访问的姿势不对，换个姿势再输入一遍");
        }
        modelAndView.setViewName("error");
        return modelAndView;
    }
}
