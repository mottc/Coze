package com.mottc.coze.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.mottc.coze.Constant;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.db.CozeUserDao;
import com.qiniu.util.Auth;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/9
 * Time: 19:45
 */
public class AvatarUtils {

    static CozeUserDao mCozeUserDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getCozeUserDao();


    public static void setAvatar(Context context, String username, ImageView imageView) {

        String avatar = null;
        List<CozeUser> cozeUserList = mCozeUserDao.queryBuilder().where(CozeUserDao.Properties.UserName.eq(username)).list();
        if (cozeUserList.size() != 0) {
            avatar = cozeUserList.get(0).getAvatar();
        }

        String Url = Constant.BASIC_URL + username + ".png?" + avatar;
        Glide
                .with(context)
                .load(Url)
                .error(R.drawable.default_avatar)
                .into(imageView);
    }


    public static void groupSetAvatar(Context context, String username, ImageView imageView) {

        String Url = Constant.BASIC_URL + username + ".png";
        Glide
                .with(context)
                .load(Url)
                .error(R.drawable.default_avatar)
                .into(imageView);
    }

    public static void setAvatarWithoutCache(Context context, String username, ImageView imageView) {

        String Url = Constant.BASIC_URL + username + ".png?" + System.currentTimeMillis();
        Glide
                .with(context)
                .load(Url)
                .error(R.drawable.default_avatar)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);
    }


    public static String createImageToken(String userName) {
        String bucketName = "jungle:" + userName + ".png";
        Auth auth = Auth.create("thx5mKjSsksUU1I24M8XTt5q0DSjgs9tXpMB54gr", "Xw2OGDoefwxGEAuJP_SWHnvm32PssnJgTJRGeHTB");
        return auth.uploadToken(bucketName);
    }

}
