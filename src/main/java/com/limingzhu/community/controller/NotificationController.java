package com.limingzhu.community.controller;

import com.limingzhu.community.dto.NotificationDTO;
import com.limingzhu.community.enums.NotificationTypeEnum;
import com.limingzhu.community.model.Notification;
import com.limingzhu.community.model.User;
import com.limingzhu.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpServletRequest;

@Controller
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request, @PathVariable(name = "id") Integer id){
        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            return "redirect:/";
        }

        NotificationDTO notificationDTO = notificationService.read(id,user);
        if(NotificationTypeEnum.REPLAY_COMMENT.getType() == notificationDTO.getType()||NotificationTypeEnum.REPLAY_QUESTION.getType() == notificationDTO.getType()){
            return "redirect:/question/" + notificationDTO.getOuterid();
        }else{
            return "/";
        }
    }
}
