package com.limingzhu.community.schedule;

import com.limingzhu.community.cache.HotTagCache;
import com.limingzhu.community.dto.HotTagNum_QueNum_ComNum;
import com.limingzhu.community.mapper.QuestionExtMapper;
import com.limingzhu.community.mapper.QuestionMapper;
import com.limingzhu.community.model.Question;
import com.limingzhu.community.model.QuestionExample;
import com.sun.javafx.collections.MappingChange;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 定时器
 */
@Component
public class HotTagTasks {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private HotTagCache hotTagCache;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Scheduled(fixedRate = 1000 * 60 * 60 * 3)
    public void reportCurrentTime() {
        int offset = 0;
        int limit = 20;
        logger.info("the time is now {}", simpleDateFormat.format(new Date()));
        List<Question> list = new ArrayList<>();

        Map<String, Integer> priorities = new HashMap<>();
        Map<String, HotTagNum_QueNum_ComNum> prioritys_tags_ques_comms = new HashMap<>();

        while (offset == 0 || list.size() == limit) {
            list = questionMapper.selectByExampleWithRowbounds(new QuestionExample(), new RowBounds(offset, limit));
            for (Question question : list) {
                String[] split = StringUtils.split(question.getTag(),",");
                for (String tag : split) {
                    Integer priority = priorities.get(tag);
                    if (priority != null) {
                        //简化的计算热度的算法，用tag出现的问题次数 * 5 + 每个问题的评论数作为热度值，通过比较热度值比较
                        priorities.put(tag, priority + 5 + question.getCommentCount());
                    } else {
                        priorities.put(tag, 5 + question.getCommentCount());
                    }
                }
            }
            offset += limit;
        }
        //查找每个tag对应的问题数和评论数
        priorities.forEach((name,priority) ->{
            //通过tagname在数据库中查找question
            List<Question> questions = questionExtMapper.selectByTag(name);
            int que_Num = questions.size();
            int comm_num = 0;
            for (Question question : questions){
                comm_num = comm_num + question.getCommentCount();
            }
            HotTagNum_QueNum_ComNum hotTagNum_queNum_comNum = new HotTagNum_QueNum_ComNum();
            hotTagNum_queNum_comNum.setQuestionNum(que_Num);
            hotTagNum_queNum_comNum.setHotTagNum(priority);
            hotTagNum_queNum_comNum.setCommentNum(comm_num);
            prioritys_tags_ques_comms.put(name,hotTagNum_queNum_comNum);
        });
        hotTagCache.updateTags(prioritys_tags_ques_comms);
        logger.info("hostTagSchedule stop {}", new Date());
    }
}
