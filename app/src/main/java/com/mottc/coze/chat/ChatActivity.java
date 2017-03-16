package com.mottc.coze.chat;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.mottc.coze.detail.GroupDetailActivity;
import com.mottc.coze.detail.UserDetailActivity;
import com.mottc.coze.utils.CommonUtils;
import com.mottc.coze.utils.DisplayUtils;

import java.io.File;
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
    @BindView(R.id.btn_detail)
    ImageButton mBtnDetail;

    private String toChatUsername;
    private int chat_type;
    private List<EMMessage> messages;
    private ChatAdapter mChatAdapter;
    private EMConversation conversation;
    private List<View> hideInputExcludeViews;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        toChatUsername = this.getIntent().getStringExtra("toUsername");
        chat_type = this.getIntent().getIntExtra("chat_type", Constant.USER);
        messages = new ArrayList<>();
        hideInputExcludeViews = new ArrayList<>();
        hideInputExcludeViews.add(mSend);
        initView();
        getMsg();
        mChatAdapter = new ChatAdapter(messages, chat_type, this);
        mChatRecyclerView.setAdapter(mChatAdapter);
        mChatRecyclerView.scrollToPosition(messages.size() - 1);
//        mChatRecyclerView.smoothScrollToPosition(messages.size() - 1);
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
        setSupportActionBar(mChatToolbar);
        mChatToolbar.setNavigationIcon(R.drawable.back);
        mChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatActivity.super.onBackPressed();
            }
        });

        if (chat_type == Constant.GROUP) {
            String groupName = EMClient.getInstance().groupManager().getGroup(toChatUsername).getGroupName();
            mTalkTo.setText(groupName);
            mBtnDetail.setImageResource(R.drawable.group);
            mBtnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, GroupDetailActivity.class).putExtra("group_id", toChatUsername));
                }
            });
        } else {
//            String nickname = CommonUtils.getNickName(toChatUsername);
            mTalkTo.setText(toChatUsername);
            mBtnDetail.setImageResource(R.drawable.person);
            mBtnDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(ChatActivity.this, UserDetailActivity.class).putExtra("username", toChatUsername));
                }
            });
        }


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
    private void sendImage(String path) {
        EMMessage message = EMMessage.createImageSendMessage(path, true, toChatUsername);

        if (chat_type == Constant.GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
        messages.add(message);
        mChatAdapter.notifyDataSetChanged();
        if (messages.size() > 0) {
            mChatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = this.getContentResolver().query(data.getData(), filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        if (picturePath == null || picturePath.equals("null")) {
                            Toast toast = Toast.makeText(this, "can not find", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        sendImage(picturePath);
                    } else {
                        File file = new File(data.getData().getPath());
                        if (!file.exists()) {
                            Toast toast = Toast.makeText(this, "can not find", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                            return;
                        }
                        sendImage(file.getAbsolutePath());
                    }
                    break;
                case CAMERA_REQUEST_CODE:

                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }



    @OnClick({R.id.image,R.id.voice, R.id.camera, R.id.phone, R.id.video, R.id.send, R.id.add, R.id.remove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
//                Intent intentFromGallery = new Intent();
//                intentFromGallery.setType("image/*"); // 设置文件类型
//                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);



                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /*点击非键盘区，键盘落下*/
        DisplayUtils.hideInputWhenTouchOtherView(this, event, hideInputExcludeViews);
        return super.dispatchTouchEvent(event);
    }

}
