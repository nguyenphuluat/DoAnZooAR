package com.lteam.zooar.Ultils;

import com.google.gson.Gson;
import com.lteam.zooar.Model.User;

public class UltilsGson {
    private Gson gson=new Gson();

    public String toJson(Object o){
        return gson.toJson(o);
    }
    public User toOjectUser(String json){
        return gson.fromJson(json,User.class);
    }
}
