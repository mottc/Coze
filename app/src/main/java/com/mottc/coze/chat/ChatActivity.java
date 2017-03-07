package com.mottc.coze.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mottc.coze.R;

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

    private String toUsername;
    private int chat_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setupChatToolbar();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setupChatToolbar() {

        mChatToolbar.setTitle("");
        mTalkTo.setText("mottc");
        setSupportActionBar(mChatToolbar);
        mChatToolbar.setNavigationIcon(R.drawable.back);
        mChatToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

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
}
