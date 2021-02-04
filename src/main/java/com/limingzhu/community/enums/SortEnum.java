package com.limingzhu.community.enums;

public enum SortEnum {
    NEW("最新问题"),
    Hot("最热问题"),
    HOT7("7天最热问题"),
    HOT30("30天最热问题"),
    NO("消灭0回复");

    private String message;

    SortEnum(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
