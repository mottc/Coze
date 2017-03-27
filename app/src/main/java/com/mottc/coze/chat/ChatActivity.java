package com.mottc.coze.chat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
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
import com.mottc.coze.utils.PermissionsUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.mottc.coze.utils.PermissionsUtils.REQUEST_CODE_ASK_RECORD_AUDIO;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.chat_toolbar)
    Toolbar mChatToolbar;
    @BindView(R.id.image)
    ImageButton mImage;
    @BindView(R.id.voice)
    ImageButton mVoice;
    @BindView(R.id.camera)
    ImageButton mCamera;
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
    @BindView(R.id.voice_image)
    ImageView mVoiceImage;
    @BindView(R.id.voice_text)
    TextView mVoiceText;
    @BindView(R.id.voice_recorder)
    LinearLayout mVoiceRecorder;

    private String toChatUsername;
    private int chat_type;
    private List<EMMessage> messages;
    private ChatAdapter mChatAdapter;
    private EMConversation conversation;
    private List<View> hideInputExcludeViews;
    private String fileName;
    private VoiceRecorder voiceRecorder;
    protected Drawable[] micImages;
    protected PowerManager.WakeLock wakeLock;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    public static final int REQUEST_CODE_ASK_CAMERA = 123;
    public static ChatActivity sChatActivity;


    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // change image
            mVoiceImage.setImageDrawable(micImages[msg.what]);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        sChatActivity = this;
        toChatUsername = this.getIntent().getStringExtra("toUsername");
        chat_type = this.getIntent().getIntExtra("chat_type", Constant.USER);
        messages = new ArrayList<>();
        voiceRecorder = new VoiceRecorder(micImageHandler);
        hideInputExcludeViews = new ArrayList<>();
        hideInputExcludeViews.add(mSend);
        micImages = new Drawable[]{
                getResources().getDrawable(R.drawable.voice1),
                getResources().getDrawable(R.drawable.voice2),
                getResources().getDrawable(R.drawable.voice3),
        };
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        initView();
        getMsg();
        mChatAdapter = new ChatAdapter(mChatRecyclerView,messages, chat_type, this);
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

        mVoice.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                onPressToSpeakBtnTouch(v, event, new EaseVoiceRecorderCallback() {
                    @Override
                    public void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength) {
                        sendVoice(voiceFilePath, voiceTimeLength);
                    }
                });

                return false;
            }
        });

    }


    public boolean onPressToSpeakBtnTouch(View v, MotionEvent event, EaseVoiceRecorderCallback recorderCallback) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                try {
//                    TODO
                    if (VoicePlayClickListener.isPlaying)
                        VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                    v.setPressed(true);
                    startRecording();
                } catch (Exception e) {
                    v.setPressed(false);
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
                    showReleaseToCancelHint();
                } else {
                    showMoveUpToCancelHint();
                }
                return true;
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                if (event.getY() < 0) {
                    // discard the recorded audio.
                    discardRecording();
                } else {
                    // stop recording and send voice file
                    try {
                        int length = stopRecoding();
                        if (length > 0) {
                            if (recorderCallback != null) {
                                recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                            }
                        } else if (length == EMError.FILE_INVALID) {
                            Toast.makeText(this, R.string.Recording_without_permission, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, R.string.The_recording_time_is_too_short, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            default:
                discardRecording();
                return false;
        }
    }

    public interface EaseVoiceRecorderCallback {
        /**
         * on voice record complete
         *
         * @param voiceFilePath   录音完毕后的文件路径
         * @param voiceTimeLength 录音时长
         */
        void onVoiceRecordComplete(String voiceFilePath, int voiceTimeLength);
    }

    public void startRecording() {


        if (!CommonUtils.isSdcardExist()) {
            Toast.makeText(this, R.string.Send_voice_need_sdcard_support, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            PermissionsUtils.getRecordPermissions(this);

            wakeLock.acquire();
            mVoiceRecorder.setVisibility(View.VISIBLE);
            mVoiceText.setText(R.string.up_to_cancel);
            voiceRecorder.startRecording(this);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            mVoiceRecorder.setVisibility(View.INVISIBLE);
            Toast.makeText(this, R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public void showReleaseToCancelHint() {
        mVoiceText.setText(R.string.release_to_cancel);
        mVoiceText.setTextColor(getResources().getColor(android.R.color.holo_red_light));
    }

    public void showMoveUpToCancelHint() {
        mVoiceText.setText(R.string.up_to_cancel);
        mVoiceText.setTextColor(getResources().getColor(android.R.color.black));
    }

    public void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // stop recording
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
                mVoiceRecorder.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {
        }
    }

    public int stopRecoding() {
        mVoiceRecorder.setVisibility(View.INVISIBLE);
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }

    public String getVoiceFilePath() {
        return voiceRecorder.getVoiceFilePath();
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
                            mChatAdapter.notifyItemInserted(messages.size() - 1);
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
        mChatAdapter.notifyItemInserted(messages.size() - 1);
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
        mChatAdapter.notifyItemInserted(messages.size() - 1);
        if (messages.size() > 0) {
            mChatRecyclerView.smoothScrollToPosition(messages.size() - 1);
        }
    }

    private void sendVoice(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        //如果是群聊，设置chattype，默认是单聊
        if (chat_type == Constant.GROUP)
            message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
        messages.add(message);
        mChatAdapter.notifyItemInserted(messages.size() - 1);
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
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
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
                    File picture = new File(
                            Environment.getExternalStorageDirectory().getPath() + "/cozePic/" + fileName);

                    sendImage(picture.getAbsolutePath());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_CAMERA:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    startCamera();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "未授权使用相机", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CODE_ASK_RECORD_AUDIO:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(this, "长按录音图标开始录音", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission Denied
                    Toast.makeText(this, "未授权录音", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @OnClick({R.id.image, R.id.camera, R.id.send, R.id.add, R.id.remove})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image:
                PermissionsUtils.verifyStoragePermissions(this);
                Intent imageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(imageIntent, IMAGE_REQUEST_CODE);
                break;
            case R.id.camera:
                PermissionsUtils.verifyStoragePermissions(this);
                if (Build.VERSION.SDK_INT >= 23) {
                    int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
                    if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_ASK_CAMERA);
                        return;
                    } else {
                        startCamera();
                    }
                } else {
                    startCamera();
                }

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
                mInputLayout.setVisibility(View.GONE);
                break;
            case R.id.remove:
                mInputLayout.setVisibility(View.VISIBLE);
                mAddChoose.setVisibility(View.GONE);
                break;
        }
    }

    private void startCamera() {
        fileName = String.valueOf(System.currentTimeMillis()) + ".jpg";
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/cozePic/");
        if (!folder.exists()) {
            folder.mkdirs();//创建文件夹
        }
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                CommonUtils.getUriForFile(this, new File(folder.getAbsolutePath(), fileName)));
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
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
