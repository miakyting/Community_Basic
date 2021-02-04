package com.limingzhu.community.dto;

import java.util.ArrayList;
import java.util.List;

public class PaginationDTO<T> {
    //前端question信息
    private List<T> data;
    //是否显示前一页信息标签
    private boolean showPrevious;
    //是否显示回到第一页信息标签
    private boolean showFirstPage;
    //是否显示下一页信息标签
    private boolean showNext;
    //是否显示回到最后一页信息标签
    private boolean showEndPage;
    //当前页
    private Integer page;
    //下方导航栏显示的数字集合
    private List<Integer> pages = new ArrayList<>();
    //总页数
    private Integer totalPage;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public boolean isShowPrevious() {
        return showPrevious;
    }

    public void setShowPrevious(boolean showPrevious) {
        this.showPrevious = showPrevious;
    }

    public boolean isShowFirstPage() {
        return showFirstPage;
    }

    public void setShowFirstPage(boolean showFirstPage) {
        this.showFirstPage = showFirstPage;
    }

    public boolean isShowNext() {
        return showNext;
    }

    public void setShowNext(boolean showNext) {
        this.showNext = showNext;
    }

    public boolean isShowEndPage() {
        return showEndPage;
    }

    public void setShowEndPage(boolean showEndPage) {
        this.showEndPage = showEndPage;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public void setPages(List<Integer> pages) {
        this.pages = pages;
    }

    public void setPagination(Integer totalCount,Integer page,Integer size){

        //计算总页数
        Integer totalPage=0;
        if(totalCount % size == 0){
            totalPage = totalCount / size;
        }else{
            totalPage = totalCount / size + 1;
        }

        this.totalPage = totalPage;

        //放置page超出范围(注意顺序，要先判断是否大于totalPage)
        if (page > this.totalPage){
            this.page = totalPage;
        }else if (page < 1){
            this.page = 1;
        }else {
            this.page = page;
        }

        //分页栏显示的页码数
        pages.add(this.page);
        for (int i = 1; i <=3 ; i++) {
            if(this.page - i > 0){
                pages.add(0,this.page - i);
            }
            if(this.page + i <= totalPage){
                pages.add(this.page + i);
            }
        }

        // 是否展示上一页
        if(this.page == 1){
            showPrevious = false;
        }else{
            showPrevious = true;
        }

        // 是否展示下一页
        if(this.page == totalPage){
            showNext = false;
        }else{
            showNext = true;
        }

        // 是否展示第一页
        if(pages.contains(1)){
            showFirstPage = false;
        }else{
            showFirstPage = true;
        }

        // 是否展示最后一页
        if(pages.contains(totalPage)){
            showEndPage = false;
        }else{
            showEndPage = true;
        }
    }
}
