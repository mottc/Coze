package com.mottc.coze;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.mottc.coze.bean.MyInfo;
import com.mottc.coze.db.DaoMaster;
import com.mottc.coze.db.DaoSession;

import org.greenrobot.greendao.database.Database;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 15:21
 */
public class CozeApplication extends Application {
    public static Context applicationContext;
    private static CozeApplication instance;
    private String username = "";
    public DaoSession mDaoSession;
//    private Map<String, EaseUser> contactList;
//    private UserDao userDao;

    public static CozeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = this;
        instance = this;
        // 初始化环信sdk
        init(applicationContext);
    }



	/*
     * 第一步：sdk的一些参数配置 EMOptions
	 * 第二步：将配置参数封装类 传入SDK初始化
	 */

    public void init(Context context) {
        // 第一步
        EMOptions options = initChatOptions();
        // 第二步
        boolean success = initSDK(context, options);
        if (success) {
//          TODO:设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
            // 初始化数据库
            initDbDao(context);
        }
    }


    private EMOptions initChatOptions() {

        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(true);
        return options;
    }

    private void initDbDao(Context context) {
//        userDao = new UserDao(context);
    }

    private boolean isSDKInit = false;

    public synchronized boolean initSDK(Context context, EMOptions options) {

        if (isSDKInit) {
            return true;
        }

        int pid = android.os.Process.myPid();
        String processAppName = getAppName(pid);

        // 如果app启用了远程的service，此application:onCreate会被调用2次
        // 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
        // 默认的app会在以包名为默认的process name下运行，如果查到的process name不是app的process
        // name就立即返回
        if (processAppName == null || !processAppName.equalsIgnoreCase(applicationContext.getPackageName())) {
            // 则此application::onCreate 是被service 调用的，直接返回
            return false;
        }

        if (options == null) {
            EMClient.getInstance().init(context, initChatOptions());
        } else {
            EMClient.getInstance().init(context, options);
        }
        isSDKInit = true;
        return true;
    }

    /**
     * check the application process name if process name is not qualified, then
     * we think it is a service process and we will not init SDK
     * @param pID
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) applicationContext.getSystemService(Context.ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        for (Object aL : l) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (aL);
            try {
                if (info.pid == pID) {

                    processName = info.processName;
                    return processName;
                }
            } catch (Exception ignored) {
            }
        }
        return processName;
    }


    public void setCurrentUserName(String username) {
        this.username = username;
        MyInfo.getInstance(instance).setUserInfo(Constant.KEY_USERNAME, username);
    }

    public String getCurrentUserName() {
        if (TextUtils.isEmpty(username)) {
            username = MyInfo.getInstance(instance).getUserInfo(Constant.KEY_USERNAME);
        }
        return username;
    }


    public DaoSession getDaoSession(String loginUserName) {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), loginUserName + ".db");
        Database db = helper.getWritableDb();
        mDaoSession = new DaoMaster(db).newSession();
        return mDaoSession;
    }

    /**
     * 退出登录
     *
     * @param unbindDeviceToken 是否解绑设备token(使用GCM才有)
     * @param callback          callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {

        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {

                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {

                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }
}