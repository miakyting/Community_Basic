package com.limingzhu.community.service;

import com.limingzhu.community.dto.CommentDTO_Que;
import com.limingzhu.community.enums.CommentTypeEnum;
import com.limingzhu.community.enums.NotificationTypeEnum;
import com.limingzhu.community.enums.NotificationStatusEnum;
import com.limingzhu.community.exception.CustomizeErrorCode;
import com.limingzhu.community.exception.CustomizeException;
import com.limingzhu.community.mapper.*;
import com.limingzhu.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private NotificationMapper notificationMapper;

    /**
     * 创建评论
     * @param comment
     * @param commentator
     */
    @Transactional
    public void insert(Comment comment,User commentator) {
        //如果没有选择某个问题或评论进行评论抛出异常
        if (comment.getParentId() == null || comment.getParentId() == 0){
            throw new CustomizeException(CustomizeErrorCode.TAGET_PARAM_NOT_FOUND);
        }
        //如果不知道当前评论的类型（回复的是问题还是评论）抛出异常
        if(comment.getType() == null && !CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }

        //回复评论
        if(comment.getType() == CommentTypeEnum.COMMENT.getType()){
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment == null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            //查询问题,用于通知问题发起人
            Question question = questionMapper.selectByPrimaryKey(dbComment.getParentId());
            if (question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);

            //增加父类评论的被评论数
            Comment parentComment = new Comment();
            parentComment.setId(comment.getParentId());
            parentComment.setCommentCount(1);
            commentExtMapper.incCommentCount(parentComment);

            //新建通知
            createNotify(comment,dbComment.getCommentator(),commentator.getName(),question.getTitle(),NotificationTypeEnum.REPLAY_COMMENT,question.getId());
        }else{
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            Question updateQuestion = new Question();
            updateQuestion.setId(question.getId());
            updateQuestion.setCommentCount(1);
            if(question == null){
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加问题的被评论数
            questionExtMapper.incCommentCount(updateQuestion);

            //新建通知
            createNotify(comment,question.getCreator(), commentator.getName(), question.getTitle(),NotificationTypeEnum.REPLAY_QUESTION,question.getId());
        }
    }

    /**
     * 创建通知的方法
     * @param comment
     * @param receiver
     * @param notifierName
     * @param notificationTypeEnum
     */
    private void createNotify(Comment comment, Integer receiver, String notifierName, String outerTitle, NotificationTypeEnum notificationTypeEnum,Integer outerId){
        if (receiver == comment.getCommentator()){
            return;
        }
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setType(notificationTypeEnum.getType());
        notification.setOuterid(outerId);
        notification.setNotifier(comment.getCommentator());
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());
        notification.setReceiver(receiver); //需要通知的人
        notification.setNotifierName(notifierName);
        notification.setOuterTitle(outerTitle);
        notificationMapper.insert(notification);
    }


    /**
     * 通过Id查找相关评论
     * @param id 查找评论的父类ID为id的评论
     * @param type 评论的类型
     * @return
     */
    public List<CommentDTO_Que> listByTargetId(Integer id,CommentTypeEnum type) {
        //查找评论的父类ID为id的评论或问题
        CommentExample example = new CommentExample();
        example.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        example.setOrderByClause("gmt_modified DESC");
        List<Comment> comments = commentMapper.selectByExample(example);

        if (comments.size() == 0){
            return new ArrayList<>();
        }
        //获取去重的评论人ID
        Set<Integer> commentators = comments.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Integer> userIds = new ArrayList<>();
        userIds.addAll(commentators);

        //查询所有评论人的个人信息，并封装为map对象<id,user>
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Integer, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment为commentDTO_Que
        List<CommentDTO_Que> commentDTOS = comments.stream().map(comment -> {
            CommentDTO_Que commentDTO_que = new CommentDTO_Que();
            BeanUtils.copyProperties(comment, commentDTO_que);
            commentDTO_que.setUser(userMap.get(comment.getCommentator()));
            return commentDTO_que;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
