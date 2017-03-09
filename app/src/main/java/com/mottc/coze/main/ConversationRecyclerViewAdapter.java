package com.mottc.coze.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.util.DateUtils;
import com.mottc.coze.R;
import com.mottc.coze.main.ConversationFragment.OnConversationItemClickListener;
import com.mottc.coze.utils.AvatarUtils;

import java.util.Date;
import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 16:43
 */
public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    private final List<EMConversation> mValues;
    private final OnConversationItemClickListener mListener;
    private Context context;

    public ConversationRecyclerViewAdapter(List<EMConversation> items, OnConversationItemClickListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversation_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.mItem = mValues.get(position);
        String conversationId = mValues.get(position).conversationId();
        if (mValues.get(position).isGroup()) {
            String groupName = EMClient.getInstance().groupManager().getGroup(conversationId).getGroupName();
            holder.mUsername.setText(groupName);
            holder.mIsGroup.setVisibility(View.VISIBLE);
            String userName = mValues.get(position).getLastMessage().getFrom() + ":";
            setContent(holder, position, userName);
        } else {
//            TODO:
//            String nickname = CommonUtils.getNickName(conversationId);
            holder.mUsername.setText(conversationId);
            holder.mIsGroup.setVisibility(View.GONE);
            setContent(holder, position, "");
        }

        AvatarUtils.setAvatar(context, conversationId, holder.mAvatar);

        holder.mTime.setText(DateUtils.getTimestampString(new Date(mValues.get(position).getLastMessage().getMsgTime())));
        int unread = mValues.get(position).getUnreadMsgCount();
        if (unread == 0) {
            holder.mUnread.setVisibility(View.INVISIBLE);
        } else {
            holder.mUnread.setVisibility(View.VISIBLE);
            holder.mUnread.setText(String.valueOf(unread));
        }


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {

                    mListener.onConversationItemClick(holder.mItem, holder.mUsername);
                }
            }
        });
    }

    public void setContent(ViewHolder holder, int position, String userName) {
        if (mValues.get(position).getLastMessage().getType().equals(EMMessage.Type.TXT)) {
            String msg = mValues.get(position).getLastMessage().getBody().toString();
            int start = msg.indexOf("txt:\"");
            int end = msg.lastIndexOf("\"");
            msg = msg.substring((start + 5), end);
            holder.mContent.setText(String.format("%s%s", userName, msg));
        } else if (mValues.get(position).getLastMessage().getType().equals(EMMessage.Type.IMAGE)) {
            holder.mContent.setText(String.format("%s[图片]", userName));
        } else if (mValues.get(position).getLastMessage().getType().equals(EMMessage.Type.VOICE)) {
            holder.mContent.setText(String.format("%s[语音]", userName));
        } else {
            holder.mContent.setText(String.format("%s···", userName));
        }
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvatar;
        public final TextView mUsername;
        public final TextView mContent;
        public final TextView mTime;
        public final TextView mUnread;
        public final TextView mIsGroup;

        public EMConversation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatar = (ImageView) view.findViewById(R.id.avatar);
            mUsername = (TextView) view.findViewById(R.id.userName);
            mContent = (TextView) view.findViewById(R.id.msg_content);
            mTime = (TextView) view.findViewById(R.id.time);
            mUnread = (TextView) view.findViewById(R.id.unread_num);
            mIsGroup = (TextView) view.findViewById(R.id.isGroup);

        }

    }
}
