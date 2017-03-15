package com.mottc.coze.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMClient;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.mottc.coze.login.LoginActivity;
import com.mottc.coze.main.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.splash_image)
    ImageView mSplashImage;
    @BindView(R.id.coze_logo)
    ImageView mCozeLogo;

    private static final int sleepTime = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
//      使图片延伸至状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        Glide
                .with(this)
                .load(Constant.SPLASH_URL)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mSplashImage);

//      LOGO位移动画
        TranslateAnimation translateAnimation = new TranslateAnimation(0, -250, 0, 800);
        translateAnimation.setFillAfter(true);
        translateAnimation.setDuration(1500);
        translateAnimation.setStartOffset(1000);
        mCozeLogo.startAnimation(translateAnimation);
    }


    @Override
    protected void onStart() {

        super.onStart();

        new Thread(new Runnable() {
            public void run() {

                if (EMClient.getInstance().isLoggedInBefore()) {
                    //免登陆情况 加载所有本地群和会话
                    //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
                    //加上的话保证进了主页面会话和群组都已经load完毕
                    long start = System.currentTimeMillis();
                    EMClient.getInstance().groupManager().loadAllGroups();
                    EMClient.getInstance().chatManager().loadAllConversations();
                    long costTime = System.currentTimeMillis() - start;
                    //等待sleepTime时长
                    if (sleepTime - costTime > 0) {
                        try {
                            Thread.sleep(sleepTime - costTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    //进入主页面
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                    }
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.getWindow().setBackgroundDrawable(null);
    }
}
