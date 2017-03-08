package com.mottc.coze.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hyphenate.chat.EMConversation;
import com.mottc.coze.Constant;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 15:48
 */
public class CommonUtils {
    // 检测网络是否可用
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable() && mNetworkInfo.isConnected();
            }
        }
        return false;
    }


    /**
     * 将应用的会话类型转化为SDK的会话类型
     */
    public static EMConversation.EMConversationType getConversationType(int chatType) {
        if (chatType == Constant.USER) {
            return EMConversation.EMConversationType.Chat;
        } else if (chatType == Constant.GROUP) {
            return EMConversation.EMConversationType.GroupChat;
        } else {
            return EMConversation.EMConversationType.ChatRoom;
        }
    }

}
