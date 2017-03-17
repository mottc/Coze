package com.mottc.coze.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.qiniu.util.Auth;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/9
 * Time: 19:45
 */
public class AvatarUtils {

    public static void setAvatar(Context context, String username, ImageView imageView) {

        String Url = Constant.BASIC_URL + username + ".png";
        Glide
                .with(context)
                .load(Url)
                .error(R.drawable.default_avatar)
                .into(imageView);
    }


    public static String createImageToken(String userName) {
        String bucketName = "jungle:" + userName + ".png";
        Auth auth = Auth.create("thx5mKjSsksUU1I24M8XTt5q0DSjgs9tXpMB54gr", "Xw2OGDoefwxGEAuJP_SWHnvm32PssnJgTJRGeHTB");
        return auth.uploadToken(bucketName);
    }

}
