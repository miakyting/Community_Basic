package com.limingzhu.community.controller;

import com.limingzhu.community.cache.HotTagCache;
import com.limingzhu.community.dto.HotTagDTO;
import com.limingzhu.community.dto.PaginationDTO;
import com.limingzhu.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private HotTagCache hotTagCache;
    /**
     * 登录首页
     * @param model
     * @param page  当前页
     * @param size  每页显示的问题条数，默认显示10页
     * @param search 搜索栏内容，为了在下次翻页时还可以获取搜索内容再次传入前端
     * @return
     */
    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "page", defaultValue = "1") Integer page,
                        @RequestParam(name = "size", defaultValue = "10") Integer size,
                        @RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "tag", required = false) String tag,
                        @RequestParam(name = "sort",required = false) String sort) {

        PaginationDTO paginationDTO = questionService.list(search, tag, sort, page, size);
        List<HotTagDTO> tags = hotTagCache.getHots();
        model.addAttribute("pagination", paginationDTO);
        model.addAttribute("search",search);
        model.addAttribute("tags",tags);
        //如果前端是通过热门标签进入了该controller，那么每次在分页查询时都需要用到该tag，所以需要在收到
        //前端传来的tag时再返回前端
        model.addAttribute("tag",tag);
        model.addAttribute("sort",sort);
        return "index";
    }
}
