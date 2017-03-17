package com.mottc.coze.chat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.util.ImageUtils;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.mottc.coze.cache.ImageCache;
import com.mottc.coze.detail.UserDetailActivity;
import com.mottc.coze.utils.AvatarUtils;
import com.mottc.coze.utils.CommonUtils;

import java.io.File;
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
    private Context context;
    private Activity mActivity;

    public ChatAdapter(List<EMMessage> values, int chat_type, Context context) {
        mValues = values;
        this.chat_type = chat_type;
        this.context = context;
        mActivity = (Activity) context;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ReceiveTxtHolder) {
            final ReceiveTxtHolder receiveTxtHolder = (ReceiveTxtHolder) holder;
            EMTextMessageBody emTextMessageBody = (EMTextMessageBody) mValues.get(position).getBody();
            String msg = emTextMessageBody.getMessage();
            receiveTxtHolder.mTvChatContent.setText(msg);
            if (chat_type == Constant.GROUP) {
                receiveTxtHolder.mTvUserName.setVisibility(View.VISIBLE);
                receiveTxtHolder.mTvUserName.setText(mValues.get(position).getFrom());
            } else {
                receiveTxtHolder.mTvUserName.setVisibility(View.GONE);
            }

            AvatarUtils.setAvatar(context, mValues.get(position).getFrom(), receiveTxtHolder.mIvUserAvatar);
            receiveTxtHolder.mIvUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, receiveTxtHolder.mIvUserAvatar, "TransImage");
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("username", mValues.get(position).getFrom());
                    context.startActivity(intent, options.toBundle());
                }
            });

        }

        if (holder instanceof SendTxtHolder) {
            SendTxtHolder sendTxtHolder = (SendTxtHolder) holder;
            EMTextMessageBody emTextMessageBody = (EMTextMessageBody) mValues.get(position).getBody();
            String msg = emTextMessageBody.getMessage();
            sendTxtHolder.mTvChatContent.setText(msg);
        }

        if (holder instanceof SendImageHolder) {
            SendImageHolder sendImageHolder = (SendImageHolder) holder;
            EMImageMessageBody emImageMessageBody = (EMImageMessageBody) mValues.get(position).getBody();
            Glide
                    .with(context)
                    .load(emImageMessageBody.getLocalUrl())
                    .into(sendImageHolder.mSendImage);
        }

        if (holder instanceof ReceiveImageHolder) {
            final ReceiveImageHolder receiveImageHolder = (ReceiveImageHolder) holder;
            final EMImageMessageBody emImageMessageBody = (EMImageMessageBody) mValues.get(position).getBody();

            if (chat_type == Constant.GROUP) {
                receiveImageHolder.mTvUserName.setVisibility(View.VISIBLE);
                receiveImageHolder.mTvUserName.setText(mValues.get(position).getFrom());
            } else {
                receiveImageHolder.mTvUserName.setVisibility(View.GONE);
            }

            AvatarUtils.setAvatar(context, mValues.get(position).getFrom(), receiveImageHolder.mIvUserAvatar);
            receiveImageHolder.mIvUserAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, receiveImageHolder.mIvUserAvatar, "TransImage");
                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("username", mValues.get(position).getFrom());
                    context.startActivity(intent, options.toBundle());
                }
            });
//TODO:
            if (emImageMessageBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    emImageMessageBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING) {
                receiveImageHolder.mReceiveImage.setImageResource(R.drawable.image);

                mValues.get(position).setMessageStatusCallback(new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                receiveImageHolder.mProgressBar.setVisibility(View.GONE);
                                receiveImageHolder.mPercentage.setVisibility(View.GONE);
                                String thumbPath = emImageMessageBody.thumbnailLocalPath();
                                if (!new File(thumbPath).exists()) {
                                    // to make it compatible with thumbnail received in previous version
                                    thumbPath = CommonUtils.getThumbnailImagePath(emImageMessageBody.getLocalUrl());
                                }
                                showImageView(thumbPath, receiveImageHolder.mReceiveImage, emImageMessageBody.getLocalUrl(), mValues.get(position));
                            }
                        });

                    }

                    @Override
                    public void onError(int code, String error) {

                    }

                    @Override
                    public void onProgress(final int progress, String status) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.i("ChatAdapter", "run: " + progress);
                                receiveImageHolder.mPercentage.setText(String.valueOf(progress));
                            }
                        });

                    }
                });

            } else {
                receiveImageHolder.mPercentage.setVisibility(View.GONE);
                receiveImageHolder.mProgressBar.setVisibility(View.GONE);
                String thumbPath = emImageMessageBody.thumbnailLocalPath();
                if (!new File(thumbPath).exists()) {
                    // to make it compatible with thumbnail received in previous version
                    thumbPath = CommonUtils.getThumbnailImagePath(emImageMessageBody.getLocalUrl());
                }
                showImageView(thumbPath, receiveImageHolder.mReceiveImage, emImageMessageBody.getLocalUrl(), mValues.get(position));

            }


        }

    }


    private boolean showImageView(final String thumbernailPath, final ImageView iv, final String localFullSizePath, final EMMessage message) {
        // first check if the thumbnail image already loaded into cache
        Bitmap bitmap = ImageCache.getInstance().get(thumbernailPath);
        if (bitmap != null) {
            // thumbnail image is already loaded, reuse the drawable
            iv.setImageBitmap(bitmap);
            return true;
        } else {
            new AsyncTask<Object, Void, Bitmap>() {

                @Override
                protected Bitmap doInBackground(Object... args) {
                    File file = new File(thumbernailPath);
                    if (file.exists()) {
                        return ImageUtils.decodeScaleImage(thumbernailPath, 160, 160);
                    } else if (new File(((EMImageMessageBody) (message.getBody())).thumbnailLocalPath()).exists()) {
                        return ImageUtils.decodeScaleImage(((EMImageMessageBody) (message.getBody())).thumbnailLocalPath(), 160, 160);
                    } else {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            if (localFullSizePath != null && new File(localFullSizePath).exists()) {
                                return ImageUtils.decodeScaleImage(localFullSizePath, 160, 160);
                            } else {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                }

                protected void onPostExecute(Bitmap image) {
                    if (image != null) {
                        iv.setImageBitmap(image);
                        ImageCache.getInstance().put(thumbernailPath, image);
                    } else {
                        if (message.status() == EMMessage.Status.FAIL) {
                            if (CommonUtils.isNetWorkConnected(context)) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        EMClient.getInstance().chatManager().downloadThumbnail(message);
                                    }
                                }).start();
                            }
                        }

                    }
                }
            }.execute();

            return true;
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }


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

    static class ReceiveTxtHolder extends RecyclerView.ViewHolder {
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
