package com.limingzhu.community.provider;

import com.alibaba.fastjson.JSON;
import com.limingzhu.community.dto.AccessTokenDTO;
import com.limingzhu.community.dto.GithubUser;
import okhttp3.*;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class GithubProvider {
    //okhttp发送post请求到url，同时请求头信息包含AccessTokenDTO的json信息，并返回accessToken信息
    public String getAccessToken(AccessTokenDTO accessTokenDTO){
        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        //post请求实体，为json类型
        RequestBody body = RequestBody.create(mediaType, JSON.toJSONString(accessTokenDTO));
        Request request = new Request.Builder()
                .url("https://github.com/login/oauth/access_token")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String string = response.body().string();
            String token = string.split("&")[0].split("=")[1];
            return token;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //okhttp发送get请求，返回user信息
    public GithubUser getUser(String accessToken){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().
                url("https://api.github.com/user?access_token").
                header("Authorization","token "+accessToken).
                build();
        try {
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            //利用fastjson自动将json对象转化为githubuser对象
            GithubUser githubUser = JSON.parseObject(string, GithubUser.class);
            return githubUser;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
