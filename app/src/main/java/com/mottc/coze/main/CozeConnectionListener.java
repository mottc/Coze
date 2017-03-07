package com.mottc.coze.main;

import android.content.Context;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.util.NetUtils;
import com.mottc.coze.R;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/7
 * Time: 9:41
 */
public class CozeConnectionListener implements EMConnectionListener {

    private Context mContext;

    public CozeConnectionListener(Context context) {
        mContext = context;
    }

    @Override
    public void onConnected() {
    }

    @Override
    public void onDisconnected(final int error) {

        if (error == EMError.USER_REMOVED) {
            // 显示帐号已经被移除
            Toast.makeText(mContext, R.string.user_removed, Toast.LENGTH_SHORT).show();
        } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
            // 显示帐号在其他设备登录
            Toast.makeText(mContext, R.string.user_login_another_device, Toast.LENGTH_SHORT).show();
        } else if (NetUtils.hasNetwork(mContext)) {
            //连接不到聊天服务器
            Toast.makeText(mContext, R.string.can_not_connect, Toast.LENGTH_SHORT).show();
        } else {
            //当前网络不可用，请检查网络设置
            Toast.makeText(mContext, R.string.network_anomalies, Toast.LENGTH_SHORT).show();
        }

    }
}

