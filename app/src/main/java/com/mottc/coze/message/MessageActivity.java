package com.mottc.coze.message;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.bean.InviteMessage;
import com.mottc.coze.db.InviteMessageDao;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessageActivity extends AppCompatActivity {

    @BindView(R.id.msg_toolbar)
    Toolbar mMsgToolbar;
    @BindView(R.id.msg_recyclerView)
    RecyclerView mMsgRecyclerView;

    private List<InviteMessage> mInviteMessageList;
    private InviteMessageDao mInviteMessageDao;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        ButterKnife.bind(this);
        mInviteMessageList = new ArrayList<>();
        mInviteMessageDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getInviteMessageDao();
        getAllMessage();
        mMsgToolbar.setTitle(R.string.message);
        setSupportActionBar(mMsgToolbar);
        mMsgToolbar.setNavigationIcon(R.drawable.back);
        mMsgToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMsgRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mMsgRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        mMsgRecyclerView.setAdapter(new MessageAdapter(mInviteMessageList));
    }

    private void getAllMessage(){
        mInviteMessageList.clear();
        mInviteMessageList.addAll(mInviteMessageDao.loadAll());
    }

}
