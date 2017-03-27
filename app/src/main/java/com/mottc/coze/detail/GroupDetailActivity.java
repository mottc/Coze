package com.mottc.coze.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.R;
import com.mottc.coze.avatar.UploadAvatarActivity;
import com.mottc.coze.chat.ChatActivity;
import com.mottc.coze.utils.AvatarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GroupDetailActivity extends AppCompatActivity {

    @BindView(R.id.image)
    ImageView mImage;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.tv_group_num)
    TextView mTvGroupNum;
    @BindView(R.id.tv_group_name)
    TextView mTvGroupName;
    @BindView(R.id.group_members)
    RecyclerView mGroupMembers;
    @BindView(R.id.btn_change_group_name)
    Button mBtnChangeGroupName;
    @BindView(R.id.tv_group_members)
    TextView mTvGroupMembers;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;
    @BindView(R.id.btn_change_group_avatar)
    Button mBtnChangeGroupAvatar;
    @BindView(R.id.ownerLayout)
    LinearLayout mOwnerLayout;
    @BindView(R.id.quit_group)
    Button mQuitGroup;


    private String group_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        ButterKnife.bind(this);
        group_id = this.getIntent().getStringExtra("group_id");
        initView();

        new GetGroupMembers().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        AvatarUtils.setAvatarWithoutCache(this, group_id, mImage);
    }

    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupDetailActivity.super.onBackPressed();
            }
        });

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbar.setExpandedTitleColor(Color.WHITE);
        mTvGroupNum.setText(group_id);
        mGroupMembers.setLayoutManager(new LinearLayoutManager(this));
        mGroupMembers.setNestedScrollingEnabled(false);
        mGroupMembers.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    }

    @OnClick({R.id.btn_change_group_avatar, R.id.btn_change_group_name, R.id.quit_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_group_avatar:
                startActivity(new Intent(this, UploadAvatarActivity.class).putExtra("username", group_id)
                        .putExtra("loginPassword","群组").putExtra("isUserRegister", false).putExtra("isGroupCreate", false));

                break;
            case R.id.quit_group:

                new AlertDialog
                        .Builder(this)
                        .setTitle("提醒")
                        .setMessage("确定要退出该群组")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EMClient.getInstance().groupManager().asyncLeaveGroup(group_id, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GroupDetailActivity.this, "已退出群组", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        ChatActivity.sChatActivity.finish();
                                        finish();
                                    }

                                    @Override
                                    public void onError(int code, String error) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GroupDetailActivity.this, "操作失败，请重试！", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                })
                        .create()
                        .show(); // 创建对话框
                break;
            case R.id.btn_change_group_name:

                final EditText editText = new EditText(this);
                new AlertDialog.Builder(this)
                        .setTitle("请输入新的群组名称：")
                        .setView(editText)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newGroupName = editText.getText().toString().trim();
                                EMClient.getInstance().groupManager().asyncChangeGroupName(group_id, newGroupName, new EMCallBack() {
                                    @Override
                                    public void onSuccess() {

                                        startActivity(new Intent(GroupDetailActivity.this, GroupDetailActivity.class)
                                                .putExtra("group_id", group_id));
                                        finish();
                                    }

                                    @Override
                                    public void onError(int code, String error) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(GroupDetailActivity.this, "修改名称失败，请重试", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();


                break;
        }
    }


    private class GetGroupMembers extends AsyncTask<Void, Void, String[]> {
        @Override
        protected String[] doInBackground(Void... params) {

            try {
                EMClient.getInstance().groupManager().getGroupFromServer(group_id);
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.

            super.onPostExecute(result);
            List<String> group_members = new ArrayList<>();
            EMGroup group = EMClient.getInstance().groupManager().getGroup(group_id);
            mGroupMembers.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            group_members.clear();
            group_members.addAll(group.getMembers());
            if (Objects.equals(group.getOwner(), EMClient.getInstance().getCurrentUser())) {
                mOwnerLayout.setVisibility(View.VISIBLE);
            } else {
                mQuitGroup.setVisibility(View.VISIBLE);
            }
            mTvGroupMembers.append(String.valueOf(group.getMemberCount()));
            mCollapsingToolbar.setTitle(group.getGroupName());
            mTvGroupName.setText(group.getGroupName());
            GroupMembersAdapter groupMembersAdapter = new GroupMembersAdapter(group_members, group.getOwner());
            mGroupMembers.setAdapter(groupMembersAdapter);
            groupMembersAdapter.setOnGroupMembersListClickListener(new GroupMembersAdapter.OnGroupMembersListClickListener() {
                @Override
                public void OnGroupMembersListClick(String item) {
                    startActivity(new Intent(GroupDetailActivity.this, UserDetailActivity.class).putExtra("username", item));
                    finish();
                }
            });

        }
    }
}
