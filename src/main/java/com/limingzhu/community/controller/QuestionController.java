package com.limingzhu.community.controller;

import com.limingzhu.community.dto.CommentDTO_Que;
import com.limingzhu.community.dto.QuestionDTO;
import com.limingzhu.community.enums.CommentTypeEnum;
import com.limingzhu.community.service.CommentService;
import com.limingzhu.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model){

        //获取页面评论信息
        List<CommentDTO_Que> commentDTO = commentService.listByTargetId(id, CommentTypeEnum.QUESTION);

        //累加阅读数功能
        questionService.incView(id);

        //获取页面question信息
        QuestionDTO questionDTO = questionService.getById(id);

        //获取推荐question信息
        List<QuestionDTO> questionDTOS = questionService.selectRelated(questionDTO);

        model.addAttribute("question",questionDTO);
        model.addAttribute("comments",commentDTO);
        model.addAttribute("relatedQuestions",questionDTOS);
        return "question";
    }
}
