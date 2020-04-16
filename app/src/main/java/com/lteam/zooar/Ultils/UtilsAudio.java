package com.lteam.zooar.Ultils;

import android.content.Context;
import android.content.SharedPreferences;

import com.lteam.zooar.Constans;

public class UtilsAudio {
    public static boolean GetStatusAudio(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constans.Login.KEY_SAVE_AUDIO_STATUS,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Constans.Login.KEY_AUDIO,true);
    }
    public static boolean SaveStatusAudio(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences(Constans.Login.KEY_SAVE_AUDIO_STATUS,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        if(GetStatusAudio(context)) editor.putBoolean(Constans.Login.KEY_AUDIO,false);
        else editor.putBoolean(Constans.Login.KEY_AUDIO,true);
        editor.apply();
        return GetStatusAudio(context);
    }
}
