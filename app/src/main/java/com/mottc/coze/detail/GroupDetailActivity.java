package com.mottc.coze.detail;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    @BindView(R.id.group_desc)
    TextView mGroupDesc;
    @BindView(R.id.more)
    ImageButton mMore;


    private String group_id;
    Boolean isOwner = false;
    EMGroup group;


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

        mGroupMembers.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        mGroupMembers.setNestedScrollingEnabled(false);
    }


    @OnClick({R.id.btn_change_group_avatar, R.id.btn_change_group_name, R.id.quit_group, R.id.more})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_change_group_avatar:
                startActivity(new Intent(this, UploadAvatarActivity.class).putExtra("username", group_id)
                        .putExtra("loginPassword", "群组").putExtra("isUserRegister", false).putExtra("isGroupCreate", false));

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

            case R.id.more:

                final String items[] = {"解散群组", "邀请成员", "更改群简介"};
                new AlertDialog.Builder(GroupDetailActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                switch (which) {
                                    case 0:
                                        EMClient.getInstance().groupManager().asyncDestroyGroup(group_id, new EMCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        ChatActivity.sChatActivity.finish();
                                                        Toast.makeText(GroupDetailActivity.this, "群组已解散", Toast.LENGTH_SHORT).show();
                                                        finish();
                                                    }
                                                });

                                            }

                                            @Override
                                            public void onError(int code, String error) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(GroupDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                });


                                            }

                                            @Override
                                            public void onProgress(int progress, String status) {


                                            }
                                        });

                                        break;
                                    case 1:

                                        final EditText editText = new EditText(GroupDetailActivity.this);
                                        new AlertDialog.Builder(GroupDetailActivity.this)
                                                .setTitle("请输入要邀请的成员用户名：")
                                                .setView(editText)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String username = editText.getText().toString().trim();
                                                        if (TextUtils.isEmpty(username)) {
                                                            Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
                                                        }

                                                        if (group != null) {
                                                            List<String> members = group.getMembers();

                                                            if (members.contains(username)) {
                                                                Toast.makeText(GroupDetailActivity.this, "该用户已是群组成员", Toast.LENGTH_SHORT).show();
                                                            } else {
                                                                String[] usernames = new String[]{username};

                                                                EMClient.getInstance().groupManager().asyncAddUsersToGroup(group_id, usernames, new EMCallBack() {
                                                                    @Override
                                                                    public void onSuccess() {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                Toast.makeText(GroupDetailActivity.this, "邀请已发出，等待对方同意", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        });

                                                                    }

                                                                    @Override
                                                                    public void onError(int code, String error) {
                                                                        runOnUiThread(new Runnable() {
                                                                            @Override
                                                                            public void run() {

                                                                                Toast.makeText(GroupDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        });

                                                                    }

                                                                    @Override
                                                                    public void onProgress(int progress, String status) {

                                                                    }
                                                                });
                                                            }
                                                        } else {
                                                            Toast.makeText(GroupDetailActivity.this, "发生错误：请重启软件！", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                })
                                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).show();
                                        break;
                                    case 2:

                                        final EditText descEditText = new EditText(GroupDetailActivity.this);
                                        new AlertDialog.Builder(GroupDetailActivity.this)
                                                .setTitle("请输入新的群简介：")
                                                .setView(descEditText)
                                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        String desc = descEditText.getText().toString().trim();

                                                        EMClient.getInstance().groupManager().asyncChangeGroupDescription(group_id, desc, new EMCallBack() {
                                                            @Override
                                                            public void onSuccess() {
                                                                new GetGroupMembers().execute();
                                                            }

                                                            @Override
                                                            public void onError(int code, String error) {
                                                                runOnUiThread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        Toast.makeText(GroupDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();

                                                                    }
                                                                });

                                                            }

                                                            @Override
                                                            public void onProgress(int progress, String status) {

                                                            }
                                                        });//需异步处理
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
                        })
                        .create().show();


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
            group = EMClient.getInstance().groupManager().getGroup(group_id);
            mGroupMembers.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            group_members.clear();
            group_members.addAll(group.getMembers());
            if (Objects.equals(group.getOwner(), EMClient.getInstance().getCurrentUser())) {
                mOwnerLayout.setVisibility(View.VISIBLE);
                isOwner = true;
            } else {
                mQuitGroup.setVisibility(View.VISIBLE);
                mMore.setVisibility(View.GONE);
            }
            mTvGroupMembers.setText(String.valueOf(group.getMemberCount()));
            mCollapsingToolbar.setTitle(group.getGroupName());
            mTvGroupName.setText(group.getGroupName());
            if (group.getDescription().isEmpty()) {
                mGroupDesc.setVisibility(View.GONE);
            } else {
                mGroupDesc.setText(group.getDescription());
            }
            GroupMembersAdapter groupMembersAdapter = new GroupMembersAdapter(group_members, group.getOwner(), GroupDetailActivity.this);
            mGroupMembers.setAdapter(groupMembersAdapter);
            groupMembersAdapter.setOnGroupMembersListClickListener(new GroupMembersAdapter.OnGroupMembersListClickListener() {
                @Override
                public void OnGroupMembersListClick(String item) {
                    startActivity(new Intent(GroupDetailActivity.this, UserDetailActivity.class).putExtra("username", item));
                    finish();
                }

                @Override
                public void OnGroupMembersListLongClick(final String item) {

                    if (isOwner) {
                        String items[] = {"踢出群组"};
                        new AlertDialog.Builder(GroupDetailActivity.this)
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        EMClient.getInstance().groupManager().asyncRemoveUserFromGroup(group_id, item, new EMCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new GetGroupMembers().execute();
                                                        if (item.equals(group.getOwner())) {
                                                            Toast.makeText(GroupDetailActivity.this, "不能踢出群主", Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(GroupDetailActivity.this, "已踢出", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onError(int code, String error) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(GroupDetailActivity.this, "请重试", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onProgress(int progress, String status) {

                                            }
                                        });
                                    }
                                })
                                .create().show();
                    }
                }

            });

        }
    }
}
