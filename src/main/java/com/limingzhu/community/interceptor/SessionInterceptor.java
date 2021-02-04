package com.limingzhu.community.interceptor;

import com.limingzhu.community.mapper.NotificationMapper;
import com.limingzhu.community.mapper.UserMapper;
import com.limingzhu.community.model.User;
import com.limingzhu.community.model.UserExample;
import com.limingzhu.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private NotificationService notificationService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Cookie[] cookies = request.getCookies();
        if (cookies != null&&cookies.length!=0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    UserExample userExample = new UserExample();
                    userExample.createCriteria().andTokenEqualTo(token);
                    List<User> users = userMapper.selectByExample(userExample);
                    if (users.size() != 0) {
                        //在数据库中查询到了对应的user信息，就将user加入到session
                        request.getSession().setAttribute("user", users.get(0));
                        int unreadCount = notificationService.unreadCount(users.get(0).getId());
                        request.getSession().setAttribute("unreadNotification",unreadCount);
                    }
                    break;
                }
            }
        }
        return true;
    }
}
