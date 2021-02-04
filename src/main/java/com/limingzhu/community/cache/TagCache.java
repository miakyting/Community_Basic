package com.limingzhu.community.cache;

import com.limingzhu.community.dto.TagDTO;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TagCache {
    public static List<TagDTO> get(){
        ArrayList<TagDTO> tagDTO = new ArrayList<>();
        TagDTO program = new TagDTO();
        program.setCategroyName("开发语言");
        program.setTags(Arrays.asList("js","php","css","html","java","node.js","python","c++","c#","asp","shell","swift","typescript"));
        tagDTO.add(program);

        TagDTO framework = new TagDTO();
        framework.setCategroyName("平台框架");
        framework.setTags(Arrays.asList("laravel","spring","express","django","flask","yii","tornado","koa","struts"));
        tagDTO.add(framework);

        TagDTO server = new TagDTO();
        server.setCategroyName("服务器");
        server.setTags(Arrays.asList("linux","nginx","docker","apache","ubuntu","centos","缓存 tomact","负载均衡","unix","hadoop","windows-server"));
        tagDTO.add(server);

        TagDTO db = new TagDTO();
        db.setCategroyName("数据库");
        db.setTags(Arrays.asList("mysql","redis","mongodb","oracle","nosql memcached","sqlserver","postqresql","sqlite"));
        tagDTO.add(db);

        TagDTO tool = new TagDTO();
        tool.setCategroyName("开发工具");
        tool.setTags(Arrays.asList("git","github","visual-studio-code","vim","sublime-text","xcode intellij-idea","eclipse","maven","ide","svn","visual-studio","atom emacs","textmate","hg"));
        tagDTO.add(tool);
        return tagDTO;
    }

    public static String filterInvalid(String tags){
        String[] split = StringUtils.split(tags, ",");
        List<TagDTO> tagDTOS = get();

        List<String> tagList = tagDTOS.stream().flatMap(tag -> tag.getTags().stream()).collect(Collectors.toList());
        //如果输入的tag是空格或制表符，或者不包含在Tagcache中，则将其返回
        String invalid = Arrays.stream(split).filter(t -> StringUtils.isBlank(t)||!tagList.contains(t)).collect(Collectors.joining(","));
        return invalid;
    }
}
