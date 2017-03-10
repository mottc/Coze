package com.mottc.coze.message;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.Constant;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.bean.InviteMessage;
import com.mottc.coze.db.InviteMessageDao;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/10
 * Time: 20:39
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<InviteMessage> mInviteMessageList;
    private InviteMessageDao mInviteMessageDao;

    public MessageAdapter(List<InviteMessage> inviteMessageList) {
        mInviteMessageList = inviteMessageList;
        mInviteMessageDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getInviteMessageDao();

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final InviteMessage inviteMessage = mInviteMessageList.get(position);
        holder.mFrom.setText(inviteMessage.getFrom());
        holder.mReason.setText(inviteMessage.getReason());
        if (inviteMessage.getStatus().equals(Constant.AGREE)) {
            holder.mUndo.setVisibility(View.GONE);
            holder.mDone.setText(R.string.agree);
        } else if (inviteMessage.getStatus().equals(Constant.REFUSE)) {
            holder.mUndo.setVisibility(View.GONE);
            holder.mDone.setText(R.string.refuse);
        } else {
            holder.mDone.setVisibility(View.GONE);
        }

        switch (inviteMessage.getType()) {
            case Constant.USER_WANT_TO_BE_FRIEND:
                holder.mType.setText("请求加你为好友");
                holder.mAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InviteMessage aNewInviteMessage = new InviteMessage();
                        aNewInviteMessage.setId(inviteMessage.getId());
                        aNewInviteMessage.setStatus(Constant.AGREE);
                        aNewInviteMessage.setFrom(inviteMessage.getFrom());
                        aNewInviteMessage.setType(inviteMessage.getType());
                        aNewInviteMessage.setReason(inviteMessage.getReason());
                        aNewInviteMessage.setGroupName(inviteMessage.getGroupName());

                        mInviteMessageDao.update(aNewInviteMessage);
                        try {
                            EMClient.getInstance().contactManager().acceptInvitation(mInviteMessageList.get(position).getFrom());
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });
                holder.mRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        InviteMessage aNewInviteMessage = new InviteMessage();
                        aNewInviteMessage.setId(inviteMessage.getId());
                        aNewInviteMessage.setStatus(Constant.REFUSE);
                        aNewInviteMessage.setFrom(inviteMessage.getFrom());
                        aNewInviteMessage.setType(inviteMessage.getType());
                        aNewInviteMessage.setReason(inviteMessage.getReason());
                        aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                        mInviteMessageDao.update(aNewInviteMessage);
                        try {
                            EMClient.getInstance().contactManager().declineInvitation(mInviteMessageList.get(position).getFrom());
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case Constant.USER_INVITE_TO_GROUP:

               break;
            case Constant.USER_WANT_TO_IN_GROUP:
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mInviteMessageList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.from)
        TextView mFrom;
        @BindView(R.id.type)
        TextView mType;
        @BindView(R.id.reason)
        TextView mReason;
        @BindView(R.id.refuse)
        Button mRefuse;
        @BindView(R.id.agree)
        Button mAgree;
        @BindView(R.id.undo)
        LinearLayout mUndo;
        @BindView(R.id.done)
        TextView mDone;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
