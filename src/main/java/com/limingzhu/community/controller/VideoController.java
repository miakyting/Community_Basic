package com.limingzhu.community.controller;

import com.limingzhu.community.dto.VideoDTO;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.exception.CustomizeException;
import com.limingzhu.community.model.User;
import com.limingzhu.community.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class VideoController {

    @Autowired
    private VideoService videoService;

    @GetMapping("/videos")
    public String index() {
        return "videos";
    }

    @GetMapping("/video")
    public String vodeo(@RequestParam(name = "type", required = false) Integer type,
                        @RequestParam(name = "id", required = false) Integer id,
                        HttpServletRequest request,
                        Model model) {

        User user = (User) request.getSession().getAttribute("user");
        //未登录
        if (user == null) {
            return "redirect:/";
        }

        if (type != null && id == null) {
            //说明是在videos页面进入该controller的，查询所有该type的视频
            List<VideoDTO> VideoList = videoService.list(type);
            model.addAttribute("videos", VideoList);
            model.addAttribute("defaultAid", VideoList.get(0).getAid());
            model.addAttribute("defaultPage", VideoList.get(0).getPage());
            return "video";
        } else if (id != null && type != null) {
            //根据id来查询数据库中唯一指向的视频
            VideoDTO videoDTO = videoService.selectById(id);
            List<VideoDTO> VideoList = videoService.list(type);
            model.addAttribute("video", videoDTO);
            model.addAttribute("videos", VideoList);
            return "video";
        } else {
            throw new CustomizeException(CustomizeErrorCode.INPUT_NOT_AVALIABLE);
        }
    }
}
