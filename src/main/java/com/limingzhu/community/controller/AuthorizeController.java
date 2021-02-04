package com.limingzhu.community.controller;

import com.limingzhu.community.dto.AccessTokenDTO;
import com.limingzhu.community.dto.GithubUser;
import com.limingzhu.community.mapper.UserMapper;
import com.limingzhu.community.model.User;
import com.limingzhu.community.provider.GithubProvider;
import com.limingzhu.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {

    @Autowired
    private GithubProvider githubProvider;

    @Autowired
    private UserService userService;

    @Value("${github.client.id}")
    private String client_id;

    @Value("${github.client.secret}")
    private String client_secret;

    @Value("${github.client.redirect}")
    private String client_redirect;

    /**
     * 授权登录
     * @param code
     * @param state
     * @param response
     * @return
     */
    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state,
                           HttpServletResponse response){
        //github回调后需要发送post请求到github，使用okhttp实现在controller里发送post请求
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id(client_id);
        accessTokenDTO.setClient_secret(client_secret);
        accessTokenDTO.setState(state);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(client_redirect);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubuser = githubProvider.getUser(accessToken);
        if(githubuser!=null && githubuser.getId()!=null){
            User user = new User();
            String token = UUID.randomUUID().toString();
            user.setAccountId(String.valueOf(githubuser.getId()));
            user.setName(githubuser.getName());
            user.setToken(token);
            user.setAvatarUrl(githubuser.getAvatar_url());
            //将user信息存入数据库
            userService.createOrUpdate(user);
            Cookie cookie = new Cookie("token",token);
            cookie.setMaxAge(3*24*60*60);
            response.addCookie(cookie);
            //登录成功，重定向到登录页面
            return "redirect:/";
        }else{
            //登陆失败，重新登录
            return "redirect:/";
        }
    }

    /**
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request,
                         HttpServletResponse response){
        //清除session和cookie
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
