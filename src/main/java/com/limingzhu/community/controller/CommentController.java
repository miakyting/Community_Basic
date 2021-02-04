package com.limingzhu.community.controller;

import com.limingzhu.community.dto.CommentDTO;
import com.limingzhu.community.dto.CommentDTO_Que;
import com.limingzhu.community.dto.ResultDTO;
import com.limingzhu.community.enums.CommentTypeEnum;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.model.Comment;
import com.limingzhu.community.model.User;
import com.limingzhu.community.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 通过ajax对Question进行评论
     * @param commentDTO
     * @param request
     * @return
     */
    @ResponseBody
    @PostMapping("/comment")
    public Object post(@RequestBody CommentDTO commentDTO,
                       HttpServletRequest request){ //自动将前端传来的json自动赋值到commentDTO上去

        User user = (User) request.getSession().getAttribute("user");
        if (user == null){
            //如果用户没有登录就评论，直接返回NOT_LOGIN的json错误信息
            return ResultDTO.errorof(CustomizeErrorCode.NOT_LOGIN);
        }

        if(commentDTO == null || commentDTO.getContent() == null || commentDTO.getContent() == ""){
            return ResultDTO.errorof(CustomizeErrorCode.CONTENT_IS_EMPTY);
        }
        Comment comment = new Comment();
        comment.setParentId(commentDTO.getParentId());
        comment.setContent(commentDTO.getContent());
        comment.setType(commentDTO.getType());
        comment.setGmtCreate(System.currentTimeMillis());
        comment.setGmtModified(comment.getGmtCreate());
        comment.setCommentator(user.getId());
        comment.setLikeCount(0L);
        comment.setCommentCount(0);
        commentService.insert(comment,user);

        return ResultDTO.okOf();
    }

    /**
     * 获取对评论的留言信息
     * @param id
     * @return
     */
    @ResponseBody
    @GetMapping("/comment/{id}")
    public ResultDTO<List<CommentDTO>> comments(@PathVariable("id") Integer id){
        List<CommentDTO_Que> commentDTO_ques = commentService.listByTargetId(id, CommentTypeEnum.COMMENT);
        return ResultDTO.okOf(commentDTO_ques);
    }
}
