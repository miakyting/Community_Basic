package com.limingzhu.community.mapper;

import com.limingzhu.community.dto.QuestionQueryDTO;
import com.limingzhu.community.model.Question;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface QuestionExtMapper {
    int incViewCount(Question question);

    int incCommentCount(Question question);

    List<Question> selectRelated(Question question);

    Integer countBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectBySearch(QuestionQueryDTO questionQueryDTO);

    List<Question> selectByTag(String name);
}