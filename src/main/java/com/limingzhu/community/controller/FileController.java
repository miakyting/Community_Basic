package com.limingzhu.community.controller;

import com.limingzhu.community.dto.FileDTO;
import com.limingzhu.community.provider.AliyunProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 文件上传Controller
 */
@Controller
public class FileController {

    @Autowired
    private AliyunProvider aliyunProvider;

    @ResponseBody
    @PostMapping("/fileUpload")
    public FileDTO upload(HttpServletRequest request, Model model){
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("editormd-image-file");
        try {
            String url = aliyunProvider.upload(file.getInputStream(), file.getOriginalFilename());
            return FileDTO.okOf(url);
        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error","图片上传失败,请重试");
        }
        return null;
    }
}
