package com.limingzhu.community.advice;

import com.alibaba.fastjson.JSON;
import com.limingzhu.community.dto.ResultDTO;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.exception.CustomizeException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice
//404错误是不经过Controller的，所以使用@ControllerAdvice或@RestControllerAdvice无法获取到404错误
public class CustomizeExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView ExceptionHandler(Throwable e, Model model, HttpServletRequest request, HttpServletResponse response) {
        String contentType = request.getContentType();
        //如果请求的是json信息
        if ("application/json".equals(contentType)) {
            ResultDTO resultDTO;
            //返回json
            if (e instanceof CustomizeException) {
                //解析错误信息然后映射到resultDTO对象中
                resultDTO = ResultDTO.errorof((CustomizeException) e);
            } else {
                //当出现了CustomizeException无法解析的错误，且controller没有拦截4xx异常，剩下的就是服务器端的系统异常
                resultDTO = ResultDTO.errorof(CustomizeErrorCode.SYS_ERROR);
            }
            try {
                //将错误信息ResultDTO转化为json信息直接write到浏览器端
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException ioe) {

            }
            return null;
        } else {
            //错误页面跳转
            if (e instanceof CustomizeException) {
                model.addAttribute("message", e.getMessage());
            } else {
                model.addAttribute("message", CustomizeErrorCode.SYS_ERROR.getMessage());
            }
            return new ModelAndView("error");
        }
    }
}
