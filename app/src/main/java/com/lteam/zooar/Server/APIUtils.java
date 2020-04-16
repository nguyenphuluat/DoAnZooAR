package com.lteam.zooar.Server;

public class APIUtils {
    public static final String base_url="http://192.168.0.61/ARSceneformServer/";
    public static DataClient getData(){
        return ReClient.getRetrofit(base_url).create(DataClient.class);
    }
}
