package com.mottc.coze.detail;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.add.AddNewFriendActivity;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.chat.ChatActivity;
import com.mottc.coze.utils.AvatarUtils;

import java.util.List;
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
    @BindView(R.id.tv_nickname)
    TextView mTvNickname;
    @BindView(R.id.btn_change_nickname)
    Button mBtnChangeNickname;
    @BindView(R.id.btn_send_msg)
    Button mBtnSendMsg;
    @BindView(R.id.friend_layout)
    LinearLayout mFriendLayout;
    @BindView(R.id.btn_change_avatar)
    Button mBtnChangeAvatar;
    @BindView(R.id.btn_add_friend)
    Button mBtnAddFriend;

    private String username;
    private String nickname;
    private CozeUserDao mCozeUserDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        ButterKnife.bind(this);

        username = this.getIntent().getStringExtra("username");
        nickname = username;
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
                    getNickName();
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
        mTvNickname.setText(nickname);

        AvatarUtils.setAvatar(this,username,mImage);
    }

    private void getNickName() {
        List<CozeUser> cozeUserList = mCozeUserDao.queryBuilder()
                .where(CozeUserDao.Properties.UserName.eq(username))
                .list();
        nickname = cozeUserList.get(0).getNickName();
        if (nickname == null) {
            nickname = username;
        }
    }


    @OnClick({R.id.btn_change_nickname, R.id.btn_send_msg, R.id.btn_change_avatar, R.id.btn_add_friend})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_nickname:
//                TODO
//                Long id = CommonUtils.getCozeUserFromDB(username).getId();
//                CozeUser cozeUser = new CozeUser(id, username, "zhangsan", null);
//                CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser())
//                        .getCozeUserDao().update(cozeUser);
                break;
            case R.id.btn_send_msg:
                startActivity(new Intent(this, ChatActivity.class).putExtra("toUsername", username));
                finish();
                break;
            case R.id.btn_change_avatar:
                break;
            case R.id.btn_add_friend:
                startActivity(new Intent(this, AddNewFriendActivity.class).putExtra("new_name", username));
                finish();
                break;
        }
    }
}
