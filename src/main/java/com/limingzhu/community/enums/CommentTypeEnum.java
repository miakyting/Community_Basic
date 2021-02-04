package com.limingzhu.community.enums;

public enum  CommentTypeEnum {
    QUESTION(1),
    COMMENT(2);

    private Integer type;

    CommentTypeEnum(Integer type){
        this.type = type;
    }

    //判断某个枚举对象是否存在
    public static boolean isExist(Integer type) {
        for (CommentTypeEnum value : CommentTypeEnum.values()) {
            if (value.getType() == type){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public Integer getType(){
        return type;
    }
}
