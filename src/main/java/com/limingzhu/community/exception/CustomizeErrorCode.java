package com.limingzhu.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    //2xx系统级别的异常
    QUESTION_NOT_FOUND(2001,"你找的问题不在了，要不换一个试试"),
    TAGET_PARAM_NOT_FOUND(2002,"未选择任何问题或评论进行回复"),
    NOT_LOGIN(2003,"当前操作需要登录，请登录后重试"),
    SYS_ERROR(2004,"服务器冒烟了，请稍后重试"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在"),
    CONTENT_IS_EMPTY(2007,"输入的内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"信息读取失败"),
    NOTIFICATION_NOT_FOUND(2009,"消息未查询到"),
    VIDEO_NOT_FOUND(2010,"新查询的视频集已经空了"),
    INPUT_NOT_AVALIABLE(2011,"您的URL我们不认识，请重新输入");

    private Integer code;
    private String message;
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    CustomizeErrorCode(Integer code,String message){
        this.message = message;
        this.code = code;
    }
}
