package com.mottc.coze.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.mottc.coze.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_toolbar)
    Toolbar mChatToolbar;
    @BindView(R.id.image)
    ImageButton mImage;
    @BindView(R.id.voice)
    ImageButton mVoice;
    @BindView(R.id.camera)
    ImageButton mCamera;
    @BindView(R.id.phone)
    ImageButton mPhone;
    @BindView(R.id.video)
    ImageButton mVideo;
    @BindView(R.id.add_choose)
    LinearLayout mAddChoose;
    @BindView(R.id.send)
    ImageButton mSend;
    @BindView(R.id.add)
    ImageButton mAdd;
    @BindView(R.id.text_content)
    EditText mTextContent;
    @BindView(R.id.input_layout)
    RelativeLayout mInputLayout;
    @BindView(R.id.chat_recyclerView)
    RecyclerView mChatRecyclerView;
    @BindView(R.id.chat_content)
    RelativeLayout mChatContent;
    @BindView(R.id.remove)
    ImageButton mRemove;
    @BindView(R.id.talkTo)
    TextView mTalkTo;

    private String toChatUsername;
    private int chat_type;
    private List<EMMessage> messages;
    private ChatAdapter mChatAdapter;
    private EMConversation conversation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        toChatUsername = this.getIntent().getStringExtra("toUsername");
        chat_type = this.getIntent().getIntExtra("chat_type", Constant.USER);
        messages = new ArrayList<>();
        initView();
        getMsg();
        mChatAdapter = new ChatAdapter(messages, chat_type, this);
        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.scrollToPosition(messages.size() - 1);
        EMClient.getInstance().chatManager().addMessageListener(msgListener);
    }


    private void initView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    if (messages.size() > 0) {
                        mChatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                    }
                }

            }
        });
        mChatToolbar.setTitle("");
        if (chat_type == Constant.GROUP) {
            String groupName = EMClient.getInstance().groupManager().getGroup(toChatUsername).getGroupName();
            mTalkTo.setText(groupName);
        } else {
//            String nickname = CommonUtils.getNickName(toChatUsername);
            mTalkTo.setText(toChatUsername);
        }
        setSupportActionBar(mChatToolbar);
        mChatToolbar.setNavigationIcon(R.drawable.back);
        mChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.super.onBackPressed();
            }
        });

    }

    private void getMsg() {
        messages.clear();
        conversation = EMClient.getInstance().chatManager()
                .getConversation(toChatUsername, CommonUtils.getConversationType(chat_type), true);
//      获取此会话的所有消息
        int msgCount = messages != null ? messages.size() : 0;
        if (msgCount < conversation.getAllMsgCount() && msgCount < 20) {
            String msgId = null;
            if (messages != null && messages.size() > 0) {
                msgId = messages.get(0).getMsgId();
            }
            conversation.loadMoreMsgFromDB(msgId, 20 - msgCount);
        }
        messages.addAll(conversation.getAllMessages());
        conversation.markAllMessagesAsRead();
    }

    EMMessageListener msgListener = new EMMessageListener() {

        @Override
        public void onMessageReceived(final List<EMMessage> comeMessages) {

            for (final EMMessage message : comeMessages) {
                String username = null;
                // 群组消息
                if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                    username = message.getTo();

                } else {
                    username = message.getFrom();
                }

                final String finalUsername = username;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 如果是当前会话的消息，刷新聊天页面
                        if (finalUsername.equals(toChatUsername)) {
                            messages.add(message);
                            mChatAdapter.notifyDataSetChanged();
                            if (messages.size() > 0) {
                                mChatRecyclerView.smoothScrollToPosition(messages.size() - 1);
                            }
                        } else {
                            mChatToolbar.setNavigationIcon(R.drawable.mail);
                        }
                    }
                });

            }

        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };

    private void sendMessage(String content) {
        // 创建一条文本消息，content为消息文字内容，toChatUsername为对方用户或者群聊的id，后文皆是如此
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        // 如果是群聊，设置chat_type，默认是单聊
        if (chat_type == Constant.GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
        }

        EMClient.getInstance().chatManager().sendMessage(message);
        messages.add(message);
        mChatAdapter.notifyDataSetChanged();
        if (messages.size() > 0) {
            mChatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        }
        mTextContent.setText("");
        mTextContent.clearFocus();
    }


    @OnClick({R.id.image, R.id.voice, R.id.camera, R.id.phone, R.id.video, R.id.send, R.id.add, R.id.chat_content, R.id.remove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                break;
            case R.id.voice:
                break;
            case R.id.camera:
                break;
            case R.id.phone:
                break;
            case R.id.video:
                break;
            case R.id.send:
                String content = mTextContent.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    return;
                }
                sendMessage(content);
                break;
            case R.id.add:
                mAddChoose.setVisibility(View.VISIBLE);
                break;
            case R.id.chat_content:
                break;
            case R.id.remove:
                mAddChoose.setVisibility(View.GONE);
                break;

        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        conversation.markAllMessagesAsRead();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().chatManager().removeMessageListener(msgListener);
    }
}
