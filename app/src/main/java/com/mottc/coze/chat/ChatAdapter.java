package com.mottc.coze.chat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.mottc.coze.Constant;
import com.mottc.coze.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/7
 * Time: 19:56
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<EMMessage> mValues;
    private int chat_type;

    public ChatAdapter(List<EMMessage> values,int chat_type) {
        mValues = values;
        this.chat_type = chat_type;
    }

    @Override
    public int getItemViewType(int position) {

        EMMessage emMessage = mValues.get(position);
        if (emMessage.direct() == EMMessage.Direct.RECEIVE) {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                return 0;
            } else if (emMessage.getType().equals(EMMessage.Type.IMAGE)) {
                return 1;
            } else {
                return 2;
            }

        } else {
            if (emMessage.getType().equals(EMMessage.Type.TXT)) {
                return 3;
            } else if (emMessage.getType().equals(EMMessage.Type.IMAGE)) {
                return 4;
            } else {
                return 5;
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = null;

        switch (viewType) {
            case 0:
                view = inflater.inflate(R.layout.receive_txt, parent, false);
                return new ReceiveTxtHolder(view);

            case 1:
                view = inflater.inflate(R.layout.receive_image, parent, false);
                return new ReceiveImageHolder(view);

            case 2:
                view = inflater.inflate(R.layout.receive_voice, parent, false);
                return new ReceiveVoice(view);

            case 3:
                view = inflater.inflate(R.layout.send_txt, parent, false);
                return new SendTxtHolder(view);

            case 4:
                view = inflater.inflate(R.layout.send_image, parent, false);
                return new SendImageHolder(view);

            case 5:
                view = inflater.inflate(R.layout.send_voice, parent, false);
                return new SendVoiceHolder(view);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ReceiveTxtHolder) {
            ReceiveTxtHolder receiveTxtHolder = (ReceiveTxtHolder) holder;
            String msg = mValues.get(position).getBody().toString();
            int start = msg.indexOf("txt:\"");
            int end = msg.lastIndexOf("\"");
            msg = msg.substring((start + 5), end);
            receiveTxtHolder.mTvChatContent.setText(msg);
            if (chat_type == Constant.GROUP) {
                receiveTxtHolder.mTvUserName.setVisibility(View.VISIBLE);
                receiveTxtHolder.mTvUserName.setText(mValues.get(position).getFrom());
            } else {
                receiveTxtHolder.mTvUserName.setVisibility(View.GONE);
            }
        }

        if (holder instanceof SendTxtHolder) {
            SendTxtHolder sendTxtHolder = (SendTxtHolder) holder;
            String msg = mValues.get(position).getBody().toString();
            int start = msg.indexOf("txt:\"");
            int end = msg.lastIndexOf("\"");
            msg = msg.substring((start + 5), end);
            sendTxtHolder.mTvChatContent.setText(msg);
        }






    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

//    public class ReceiveTxtHolder extends RecyclerView.ViewHolder {
//        public ReceiveTxtHolder(View itemView) {
//            super(itemView);
//
//
//        }
//    }
//
//    public class ReceiveImageHolder extends RecyclerView.ViewHolder {
//        public ReceiveImageHolder(View itemView) {
//            super(itemView);
//
//        }
//    }
//
//    public class ReceiveVoice extends RecyclerView.ViewHolder {
//        public ReceiveVoice(View itemView) {
//            super(itemView);
//
//        }
//    }
//
//    public class SendTxtHolder extends RecyclerView.ViewHolder {
//        public SendTxtHolder(View itemView) {
//            super(itemView);
//
//        }
//    }
//
//    public class SendImageHolder extends RecyclerView.ViewHolder {
//        public SendImageHolder(View itemView) {
//            super(itemView);
//
//        }
//    }


    static class SendVoiceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.iv_voice)
        ProgressBar mIvVoice;
        @BindView(R.id.tv_length)
        TextView mTvLength;
        @BindView(R.id.unread_dot)
        ImageView mUnreadDot;
        @BindView(R.id.bubble)
        RelativeLayout mBubble;

        SendVoiceHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class SendImageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.send_image)
        ImageView mSendImage;
        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;
        @BindView(R.id.percentage)
        TextView mPercentage;

        SendImageHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class SendTxtHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.tv_chat_content)
        TextView mTvChatContent;

        SendTxtHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ReceiveVoice extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.tv_userName)
        TextView mTvUserName;
        @BindView(R.id.iv_userAvatar)
        ImageView mIvUserAvatar;
        @BindView(R.id.iv_voice)
        ProgressBar mIvVoice;
        @BindView(R.id.tv_length)
        TextView mTvLength;
        @BindView(R.id.unread_dot)
        ImageView mUnreadDot;

        ReceiveVoice(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ReceiveImageHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.tv_userName)
        TextView mTvUserName;
        @BindView(R.id.iv_userAvatar)
        ImageView mIvUserAvatar;
        @BindView(R.id.receive_image)
        ImageView mReceiveImage;
        @BindView(R.id.progress_bar)
        ProgressBar mProgressBar;
        @BindView(R.id.percentage)
        TextView mPercentage;

        ReceiveImageHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ReceiveTxtHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.timestamp)
        TextView mTimestamp;
        @BindView(R.id.tv_userName)
        TextView mTvUserName;
        @BindView(R.id.iv_userAvatar)
        ImageView mIvUserAvatar;
        @BindView(R.id.tv_chatContent)
        TextView mTvChatContent;

        ReceiveTxtHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
