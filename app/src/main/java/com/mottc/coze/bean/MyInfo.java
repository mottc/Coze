package com.mottc.coze.bean;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/17
 * Time: 16:22
 */
public class MyInfo {
    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "local_userInfo";
    private static SharedPreferences mSharedPreferences;
    private static MyInfo mPreferenceUtils;
    private static SharedPreferences.Editor editor;

    private MyInfo(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 单例模式，获取instance实例
     *
     * @param cxt
     * @return
     */
    public static MyInfo getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new MyInfo(cxt);
        }
        editor = mSharedPreferences.edit();
        return mPreferenceUtils;
    }

    //
    public void setUserInfo(String str_name, String str_value) {

        editor.putString(str_name, str_value);
        editor.commit();
    }

    public String getUserInfo(String str_name) {

        return mSharedPreferences.getString(str_name, "");

    }
}
