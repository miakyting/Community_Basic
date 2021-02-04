package com.limingzhu.community.service;

import com.limingzhu.community.dto.VideoDTO;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.exception.CustomizeException;
import com.limingzhu.community.mapper.VideoMapper;
import com.limingzhu.community.model.Video;
import com.limingzhu.community.model.VideoExample;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VideoService {

    @Autowired
    private VideoMapper videoMapper;

    /**
     * 通过type查询所有属于该type的video信息
     * @param type
     * @return
     */
    public List<VideoDTO> list(Integer type) {

        List<VideoDTO> list = new ArrayList<>();
        VideoExample example = new VideoExample();
        example.createCriteria().andTypeEqualTo(type);

        List<Video> videos = videoMapper.selectByExample(example);
        //若查询不到内容，说明该视频的内容已经置空了
        if (videos == null || videos.size() == 0){
            throw new CustomizeException(CustomizeErrorCode.VIDEO_NOT_FOUND);
        }
        list = videos.stream().map(q ->{
            VideoDTO videoDTO = new VideoDTO();
            BeanUtils.copyProperties(q,videoDTO);
            return videoDTO;
        }).collect(Collectors.toList());
        return list;
    }

    public VideoDTO selectById(Integer id) {

        VideoDTO videoDTO = new VideoDTO();
        Video video = videoMapper.selectByPrimaryKey(id);
        BeanUtils.copyProperties(video,videoDTO);
        return videoDTO;
    }
}
