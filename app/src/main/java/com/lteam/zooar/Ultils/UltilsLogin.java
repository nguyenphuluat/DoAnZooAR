package com.lteam.zooar.Ultils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lteam.zooar.Constans;
import com.lteam.zooar.Model.User;

public class UltilsLogin {
    public static void saveStatusUserLogin(boolean status, Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constans.Login.KEY_SHAREPREFERENT_ACCOUNT,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(Constans.Login.KEY_LOGIN_STATUS,status);
        editor.apply();
    }
    public static boolean getStattusUserLogin(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constans.Login.KEY_SHAREPREFERENT_ACCOUNT,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constans.Login.KEY_LOGIN_STATUS,false);
    }
    public static void saveUser(Context context, User user){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constans.Login.KEY_SHAREPREFERENT_ACCOUNT,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(Constans.Login.KEY_SAVE_USER,new UltilsGson().toJson(user));
        editor.apply();
    }
    public static User getUser(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constans.Login.KEY_SHAREPREFERENT_ACCOUNT,Context.MODE_PRIVATE);
        return new UltilsGson().toOjectUser(sharedPreferences.getString(Constans.Login.KEY_SAVE_USER,""));
    }
}
