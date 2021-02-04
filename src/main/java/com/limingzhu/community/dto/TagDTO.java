package com.limingzhu.community.dto;

import java.util.List;

public class TagDTO {
    private String categroyName;
    private List<String> tags;

    public String getCategroyName() {
        return categroyName;
    }

    public void setCategroyName(String categroyName) {
        this.categroyName = categroyName;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
