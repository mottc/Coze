package com.mottc.coze.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.add.AddNewFriendActivity;
import com.mottc.coze.avatar.UploadAvatarActivity;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.chat.ChatActivity;
import com.mottc.coze.db.CozeUserDao;
import com.mottc.coze.utils.AvatarUtils;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mottc.coze.R.id.toolbar;

public class UserDetailActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.tv_username)
    TextView mTvUsername;
    @BindView(R.id.btn_delete_friend)
    Button mBtnDeleteFriend;
    @BindView(R.id.btn_send_msg)
    Button mBtnSendMsg;
    @BindView(R.id.friend_layout)
    LinearLayout mFriendLayout;
    @BindView(R.id.btn_change_avatar)
    Button mBtnChangeAvatar;
    @BindView(R.id.btn_add_friend)
    Button mBtnAddFriend;

    private String username;
    private CozeUserDao mCozeUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        username = this.getIntent().getStringExtra("username");
        mCozeUserDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getCozeUserDao();
        initView();

    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserDetailActivity.super.onBackPressed();
            }
        });

        if (Objects.equals(username, EMClient.getInstance().getCurrentUser())) {
            mBtnChangeAvatar.setVisibility(View.VISIBLE);
        } else {
            boolean isStranger = true;
            for (CozeUser user : mCozeUserDao.loadAll()) {
                if (user.getUserName().equals(username)) {
                    isStranger = false;
                    mFriendLayout.setVisibility(View.VISIBLE);
                    break;
                }
            }
            if (isStranger) {
                mBtnAddFriend.setVisibility(View.VISIBLE);
            }

        }


        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbar.setExpandedTitleColor(Color.WHITE);
        mCollapsingToolbar.setTitle(username);
        mTvUsername.setText(username);

//        AvatarUtils.setAvatar(this, username, mImage);
        AvatarUtils.setAvatarWithoutCache(this, username, mImage);
    }


    @OnClick({R.id.btn_delete_friend, R.id.btn_send_msg, R.id.btn_change_avatar, R.id.btn_add_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_delete_friend:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("确认删除好友？");
                builder.setTitle("提示");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                            EMClient.getInstance().contactManager().aysncDeleteContact(username, new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    dialog.dismiss();
                                    startActivity(new Intent(UserDetailActivity.this, UserDetailActivity.class)
                                            .putExtra("username",username));
                                    finish();
                                }
                                @Override
                                public void onError(int code, String error) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(UserDetailActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                @Override
                                public void onProgress(int progress, String status) {

                                }
                            });
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.btn_send_msg:
                startActivity(new Intent(this, ChatActivity.class).putExtra("toUsername", username));
                finish();
                break;
            case R.id.btn_change_avatar:
                startActivity(new Intent(this, UploadAvatarActivity.class).putExtra("username",username).putExtra("isRegister", false));
                break;
            case R.id.btn_add_friend:
                startActivity(new Intent(this, AddNewFriendActivity.class).putExtra("new_name", username));
                finish();
                break;
        }
    }
}
