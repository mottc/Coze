package com.mottc.coze.chat;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.hyphenate.util.EMLog;
import com.mottc.coze.R;

import java.io.File;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/23
 * Time: 14:59
 */
public class VoicePlayClickListener implements View.OnClickListener {
    private static final String TAG = "VoicePlayClickListener";
    private EMMessage message;
    private EMVoiceMessageBody voiceBody;
    private SeekBar mSeekBar;

    private MediaPlayer mediaPlayer = null;
    private Activity activity;
    private EMMessage.ChatType chatType;
    private ChatAdapter adapter;
    private Boolean isStop;
    private TextView mTextView;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            mSeekBar.setProgress(msg.what);
            return false;
        }
    });

    public static boolean isPlaying = false;
    public static VoicePlayClickListener currentPlayListener = null;
    public static String playMsgId;

    public VoicePlayClickListener(EMMessage message, SeekBar seekBar,TextView textView, ChatAdapter adapter, Activity context) {
        this.message = message;
        voiceBody = (EMVoiceMessageBody) message.getBody();
        this.adapter = adapter;
        mSeekBar = seekBar;
        mTextView = textView;
        this.activity = context;
        this.chatType = message.getChatType();
    }

    public void stopPlayVoice() {
        // stop play voice
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        isPlaying = false;
        isStop = true;
        playMsgId = null;
        mSeekBar.setProgress(0);
//        adapter.notifyDataSetChanged();
    }

    public void playVoice(String filePath) {
        if (!(new File(filePath).exists())) {
            return;
        }
        playMsgId = message.getMsgId();
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();

        //TODO:扬声器模式
//        if (EaseUI.getInstance().getSettingsProvider().isSpeakerOpened()) {
//            audioManager.setMode(AudioManager.MODE_NORMAL);
//            audioManager.setSpeakerphoneOn(true);
//            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
//        } else {
        audioManager.setSpeakerphoneOn(false);// 关闭扬声器
        // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
//        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    stopPlayVoice(); // stop animation
                }

            });
            isPlaying = true;
            isStop = false;
            currentPlayListener = this;
            mediaPlayer.start();
            showProgress();

            // 如果是接收的消息
            if (message.direct() == EMMessage.Direct.RECEIVE) {
                if (!message.isAcked() && chatType == EMMessage.ChatType.Chat) {
                    // 告知对方已读这条消息
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                }
                if (!message.isListened()) {
                    // 隐藏自己未播放这条语音消息的标志
                    mTextView.setTextColor(Color.WHITE);
                    message.setListened(true);
                    EMClient.getInstance().chatManager().setVoiceMessageListened(message);
                }

            }

        } catch (Exception e) {
            System.out.println();
        }
    }

    private void showProgress() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isStop) {
                        break;
                    }
                    Message handleMessage = new Message();
                    handleMessage.what = (mediaPlayer.getCurrentPosition()+500)/1000;
                    handler.sendMessage(handleMessage);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        String st = activity.getResources().getString(R.string.Is_download_voice_click_later);
        if (isPlaying) {
            if (playMsgId != null && playMsgId.equals(message.getMsgId())) {
                currentPlayListener.stopPlayVoice();
                return;
            }
            currentPlayListener.stopPlayVoice();
        }

        if (message.direct() == EMMessage.Direct.SEND) {
            // for sent msg, we will try to play the voice file directly
            playVoice(voiceBody.getLocalUrl());
        } else {
            if (message.status() == EMMessage.Status.SUCCESS) {
                File file = new File(voiceBody.getLocalUrl());
                if (file.exists() && file.isFile())
                    playVoice(voiceBody.getLocalUrl());
                else
                    EMLog.e(TAG, "file not exist");

            } else if (message.status() == EMMessage.Status.INPROGRESS) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
            } else if (message.status() == EMMessage.Status.FAIL) {
                Toast.makeText(activity, st, Toast.LENGTH_SHORT).show();
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        EMClient.getInstance().chatManager().downloadAttachment(message);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        super.onPostExecute(result);
                        adapter.notifyDataSetChanged();
                    }

                }.execute();
            }

        }
    }
}
