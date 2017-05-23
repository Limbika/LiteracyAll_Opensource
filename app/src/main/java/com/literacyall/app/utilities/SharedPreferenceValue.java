package com.literacyall.app.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;

/**
 * Created by RAFI on 11/15/2016.
 */

public class SharedPreferenceValue {
    private static String defaultPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String getFilePath(Context context) {
        SharedPreferences settings = context.getSharedPreferences("TAG_FILE_PATH_PREF", 0);
        String mFlag = settings.getString("TAG_FILE_PATH", null);
        return mFlag;
    }

    public static void setFilePath(Context context, String filePath) {
        SharedPreferences settings = context.getSharedPreferences("TAG_FILE_PATH_PREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("TAG_FILE_PATH", filePath);
        editor.commit();
    }

    public static void setFileSoundPath(Context context, String filePath) {
        SharedPreferences settings = context.getSharedPreferences("TAG_FILE_SOUND_PATH_PREF", 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("TAG_FILE_SOUND_PATH", filePath);
        editor.commit();
    }

    public static String getFileSoundPath(Context context) {
        SharedPreferences settings = context.getSharedPreferences("TAG_FILE_SOUND_PATH_PREF", 0);
        String mFlag = settings.getString("TAG_FILE_SOUND_PATH", null);

        return mFlag == null ? defaultPath : mFlag;
    }
}
