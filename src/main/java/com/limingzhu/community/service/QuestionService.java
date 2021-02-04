package com.limingzhu.community.service;

import com.limingzhu.community.dto.PaginationDTO;
import com.limingzhu.community.dto.QuestionDTO;
import com.limingzhu.community.dto.QuestionQueryDTO;
import com.limingzhu.community.enums.SortEnum;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.exception.CustomizeException;
import com.limingzhu.community.mapper.QuestionExtMapper;
import com.limingzhu.community.mapper.QuestionMapper;
import com.limingzhu.community.mapper.UserMapper;
import com.limingzhu.community.model.Question;
import com.limingzhu.community.model.QuestionExample;
import com.limingzhu.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    /**
     * 欢迎页面的分页操作
     *
     *
     * @param
     * @param tag
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(String search, String tag,String sort,Integer page, Integer size) {

        //搜索栏不同关键字用空格分割，并将空格替换成|交于数据库查询
        if (StringUtils.isNotBlank(search)){
            String[] tags = StringUtils.split(search," ");
            //过滤tags中的特殊字符 , ::的意思是将tags的每个元素都作为StringUtils的isNotBlank的方法的参数
            search = Arrays
                    .stream(tags)
                    .filter(StringUtils::isNotBlank)
                    .map(t -> t.replace("+","").replace("*","").replace("?",""))
                    .filter(StringUtils::isNotBlank)
                    .collect(Collectors.joining("|"));
        }

        PaginationDTO paginationDTO = new PaginationDTO();

        //查询totalCount信息后设置PaginationDTO基本信息
        QuestionQueryDTO questionQueryDTO = new QuestionQueryDTO();
        questionQueryDTO.setSearch(search);
        questionQueryDTO.setTag(tag);

        for (SortEnum sortEnum : SortEnum.values()){
            if (sortEnum.name().toLowerCase().equals(sort)){
                questionQueryDTO.setSort(sort);

                if (sortEnum == SortEnum.HOT7){
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 7);
                }

                if (sortEnum == SortEnum.HOT30){
                    questionQueryDTO.setTime(System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 30);
                }
                break;
            }
        }


        Integer totalCount = questionExtMapper.countBySearch(questionQueryDTO);
        paginationDTO.setPagination(totalCount, page, size);

        //查询页面所需要的Question信息
        page = paginationDTO.getPage();
        Integer offset = size * (page - 1);
        questionQueryDTO.setPage(offset);
        questionQueryDTO.setSize(size);
        List<Question> questions = questionExtMapper.selectBySearch(questionQueryDTO);

        if(questions!=null&&questions.size()!=0) {
           List<QuestionDTO> questionDTOSList = CopyProperties(questions);
           paginationDTO.setData(questionDTOSList);
        }
        return paginationDTO;
    }

    /**
     * 当用户查询自己的问题时页面的分页操作
     * @param userId
     * @param page
     * @param size
     * @return
     */
    public PaginationDTO list(Integer userId, Integer page, Integer size) {

        PaginationDTO paginationDTO = new PaginationDTO();

        //查询totalCount信息并配置paginationDTO基本信息
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        int totalCount = questionMapper.countByExample(questionExample);
        paginationDTO.setPagination(totalCount, page, size);

        //查询页面所需要的Question信息
        page = paginationDTO.getPage();
        Integer offset = size * (page - 1);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(questionExample, new RowBounds(offset, size));

        if(questions != null && questions.size() != 0) {
            List<QuestionDTO> questionDTOSList = CopyProperties(questions);
            paginationDTO.setData(questionDTOSList);
        }
        return paginationDTO;
    }

    /**
     * 辅助方法，用于将每条Question重新封装成QuestionDTO（加入每条question对应的creator信息）
     * @param questions
     * @return
     */
    public List<QuestionDTO> CopyProperties(List<Question> questions){
        List<QuestionDTO> questionDTOSList = new ArrayList<>();

        for (Question question : questions) {
            //查询每条方法对应的user信息
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOSList.add(questionDTO);
        }
        return questionDTOSList;
    }

    /**
     * 通过question的id属性获取QusestionDTO对象返回Controller层
     * @param id    question的id属性
     * @return
     */
    public QuestionDTO getById(Integer id) {
        Question question = questionMapper.selectByPrimaryKey(id);
        if (question == null){
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        QuestionDTO questionDTO = new QuestionDTO();
        BeanUtils.copyProperties(question,questionDTO);
        User user = userMapper.selectByPrimaryKey(question.getCreator());
        questionDTO.setUser(user);
        return questionDTO;
    }

    /**
     * 实现对数据库中的question更新或创建操作
     * 如果初次创建发布question，question中是没有id属性的
     * 如果是用户更新自己的question，通过getById获取的question对象中是拥有id属性值的
     * @param question  更新或者要删除的question对象
     */
    public void createOrUpdate(Question question) {
        if(question.getId() == null){
            //如果question对象中没有id属性，说明是增加question
            question.setViewCount(0);
            question.setLikeCount(0);
            question.setCommentCount(0);
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else{
            //如果question对象中有id属性，说明是修改question(question的创建时间无需更新)
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTitle(question.getTitle());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if(updated != 1){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    /**
     * 累加question的阅读数
     * @param id
     */
    public void incView(Integer id) {
        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incViewCount(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())){
            return new ArrayList<>();
        }
        String tags = queryDTO.getTag();
        String reptags = StringUtils.replace(tags, ",", "|");
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(reptags);

        List<Question> questions = questionExtMapper.selectRelated(question);
        //接收一个函数作为参数，该函数会被应用到每个元素上，并将其映射成一个新的元素
        List<QuestionDTO> questionDTOS = questions.stream().map(q->{
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q,questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;
    }
}
