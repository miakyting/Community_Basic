package com.limingzhu.community.dto;

public class FileDTO {
    private Integer success;
    private String message;
    private String url;

    public int getSuccess() {
        return success;
    }

    public void setSuccess(Integer success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static FileDTO okOf(String url){
        FileDTO fileDTO = new FileDTO();
        fileDTO.success = 1;
        fileDTO.url = url;
        fileDTO.message = "图片上传成功";
        return fileDTO;
    }
}
