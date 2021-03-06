package com.limingzhu.community.controller;

import com.limingzhu.community.cache.TagCache;
import com.limingzhu.community.dto.QuestionDTO;
import com.limingzhu.community.model.Question;
import com.limingzhu.community.model.User;
import com.limingzhu.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }

    //发布问题
    @PostMapping("/publish")
    public String doPublish(@RequestParam(value = "title",required = false) String title,
                            @RequestParam(value = "description",required = false) String desc,
                            @RequestParam(value = "tag",required = false) String tag,
                            @RequestParam(value = "id",required = false) Integer id,
                            HttpServletRequest request,
                            Model model) {
        //当用户没有填写完成点击发布，请求后会将用户先前写好的信息保留，然后提示错误信息
        model.addAttribute("title", title);
        model.addAttribute("description", desc);
        model.addAttribute("tag", tag);
        model.addAttribute("tags", TagCache.get());

        if (StringUtils.isEmpty(title)) {
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }

        if (StringUtils.isEmpty(desc)) {
            model.addAttribute("error", "问题补充不能为空");
            return "publish";
        }

        if (StringUtils.isEmpty(title)) {
            model.addAttribute("error", "标签不能为空");
            return "publish";
        }
        String newTag = StringUtils.replace(tag,"，",",");
        String invalid = TagCache.filterInvalid(newTag);
        if (!StringUtils.isEmpty(invalid)){
            model.addAttribute("error","输入非法标签:"+invalid);
            return "publish";
        }

        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            model.addAttribute("error", "用户未登录");
            return "publish";
        }

        //如果用户新发表的question符合规范
        Question question = new Question();
        question.setTitle(title);
        question.setTag(newTag);
        question.setDescription(desc);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
        return "redirect:/";
    }

    //对某个问题进行编辑，修改（仅本人可修改）
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable("id") Integer id,
                       Model model){

        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",question.getId());
        model.addAttribute("tags", TagCache.get());
        return "publish";
    }
}
