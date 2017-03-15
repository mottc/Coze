package com.mottc.coze.detail;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.R;
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
    ListView mGroupMembers;
    @BindView(R.id.btn_change_group_name)
    Button mBtnChangeGroupName;
    @BindView(R.id.tv_group_members)
    TextView mTvGroupMembers;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

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


    private void initView() {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupDetailActivity.super.onBackPressed();
            }
        });

        AvatarUtils.setAvatar(this, group_id, mImage);

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCollapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        mCollapsingToolbar.setExpandedTitleColor(Color.WHITE);
        mTvGroupNum.setText(group_id);

    }

    @OnClick(R.id.btn_change_group_name)
    public void onClick() {
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

            List<String> group_members = new ArrayList<>();
            EMGroup group = EMClient.getInstance().groupManager().getGroup(group_id);
            super.onPostExecute(result);
            mGroupMembers.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
            group_members.clear();
            group_members.addAll(group.getMembers());
            if (Objects.equals(group.getOwner(), EMClient.getInstance().getCurrentUser())) {
                mBtnChangeGroupName.setVisibility(View.VISIBLE);
            }
            mTvGroupMembers.append(String.valueOf(group.getMemberCount()));
            mCollapsingToolbar.setTitle(group.getGroupName());
            mTvGroupName.setText(group.getGroupName());
            GroupMembersAdapter groupMembersAdapter = new GroupMembersAdapter(group_members, getBaseContext(), group.getOwner());
            mGroupMembers.setAdapter(groupMembersAdapter);
            groupMembersAdapter.setOnGroupMembersListClickListener(new GroupMembersAdapter.OnGroupMembersListClickListener() {
                @Override
                public void OnGroupMembersListClick(String item) {
                    startActivity(new Intent(GroupDetailActivity.this, UserDetailActivity.class).putExtra("username", item));
                }
            });

            setListViewHeightBasedOnChildren(mGroupMembers);
        }
    }

//TODO:高度测量
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight +200;
        listView.setLayoutParams(params);
    }
}
