package com.mottc.coze.message;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
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
 * Date: 2017/3/17
 * Time: 21:41
 */
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<InviteMessage> mInviteMessageList;
    private InviteMessageDao mInviteMessageDao;
    private Context mContext;

    public MessageAdapter(List<InviteMessage> inviteMessageList, Context context) {
        mInviteMessageList = inviteMessageList;
        mInviteMessageDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getInviteMessageDao();
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.invite_msg_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final InviteMessage inviteMessage = mInviteMessageList.get(position);
        holder.mFrom.setText(inviteMessage.getFrom());
        holder.mReason.setText(inviteMessage.getReason());
        holder.mTime.setText(inviteMessage.getTime());
        if (inviteMessage.getStatus().equals(Constant.AGREE)) {
            holder.mUndo.setVisibility(View.GONE);
            holder.mDone.setVisibility(View.VISIBLE);
            holder.mDone.setText(R.string.agree);
        } else if (inviteMessage.getStatus().equals(Constant.REFUSE)) {
            holder.mUndo.setVisibility(View.GONE);
            holder.mDone.setVisibility(View.VISIBLE);
            holder.mDone.setText(R.string.refuse);
        } else {
            holder.mDone.setVisibility(View.GONE);
            holder.mUndo.setVisibility(View.VISIBLE);
        }

        switch (inviteMessage.getType()) {
            case Constant.USER_WANT_TO_BE_FRIEND:
                holder.mType.setText("请求加你为好友");
                holder.mAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mAgree.setClickable(false);
                        holder.mRefuse.setClickable(false);


                            EMClient.getInstance().contactManager().asyncAcceptInvitation(inviteMessage.getFrom(), new EMCallBack() {
                                @Override
                                public void onSuccess() {
                                    InviteMessage aNewInviteMessage = new InviteMessage();
                                    aNewInviteMessage.setId(inviteMessage.getId());
                                    aNewInviteMessage.setStatus(Constant.AGREE);
                                    aNewInviteMessage.setFrom(inviteMessage.getFrom());
                                    aNewInviteMessage.setType(inviteMessage.getType());
                                    aNewInviteMessage.setReason(inviteMessage.getReason());
                                    aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                                    aNewInviteMessage.setTime(inviteMessage.getTime());

                                    mInviteMessageDao.update(aNewInviteMessage);
                                }

                                @Override
                                public void onError(int code, String error) {

                                }

                                @Override
                                public void onProgress(int progress, String status) {

                                }
                            });
                    }
                });
                holder.mRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mAgree.setClickable(false);
                        holder.mRefuse.setClickable(false);

                        EMClient.getInstance().contactManager().asyncDeclineInvitation(inviteMessage.getFrom(), new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                InviteMessage aNewInviteMessage = new InviteMessage();
                                aNewInviteMessage.setId(inviteMessage.getId());
                                aNewInviteMessage.setStatus(Constant.REFUSE);
                                aNewInviteMessage.setFrom(inviteMessage.getFrom());
                                aNewInviteMessage.setType(inviteMessage.getType());
                                aNewInviteMessage.setReason(inviteMessage.getReason());
                                aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                                aNewInviteMessage.setTime(inviteMessage.getTime());
                                mInviteMessageDao.update(aNewInviteMessage);
                            }

                            @Override
                            public void onError(int code, String error) {

                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });
                    }
                });
                break;
            case Constant.USER_INVITE_TO_GROUP:
                holder.mType.setText("邀请你加入群组-" + inviteMessage.getGroupName());
                holder.mAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mAgree.setClickable(false);
                        holder.mRefuse.setClickable(false);

                        EMClient.getInstance().groupManager().asyncAcceptInvitation(inviteMessage.getGroupId(), inviteMessage.getFrom(), new EMValueCallBack<EMGroup>() {
                            @Override
                            public void onSuccess(EMGroup value) {
                                InviteMessage aNewInviteMessage = new InviteMessage();
                                aNewInviteMessage.setId(inviteMessage.getId());
                                aNewInviteMessage.setStatus(Constant.AGREE);
                                aNewInviteMessage.setFrom(inviteMessage.getFrom());
                                aNewInviteMessage.setType(inviteMessage.getType());
                                aNewInviteMessage.setReason(inviteMessage.getReason());
                                aNewInviteMessage.setGroupId(inviteMessage.getGroupId());
                                aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                                aNewInviteMessage.setTime(inviteMessage.getTime());
                                mInviteMessageDao.update(aNewInviteMessage);
                            }

                            @Override
                            public void onError(int error, String errorMsg) {

                            }
                        });
                    }
                });
                holder.mRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mAgree.setClickable(false);
                        holder.mRefuse.setClickable(false);
                        EMClient.getInstance().groupManager().asyncDeclineInvitation(inviteMessage.getGroupId(), inviteMessage.getFrom(), "", new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                InviteMessage aNewInviteMessage = new InviteMessage();
                                aNewInviteMessage.setId(inviteMessage.getId());
                                aNewInviteMessage.setStatus(Constant.REFUSE);
                                aNewInviteMessage.setFrom(inviteMessage.getFrom());
                                aNewInviteMessage.setType(inviteMessage.getType());
                                aNewInviteMessage.setReason(inviteMessage.getReason());
                                aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                                aNewInviteMessage.setGroupId(inviteMessage.getGroupId());
                                aNewInviteMessage.setTime(inviteMessage.getTime());
                                mInviteMessageDao.update(aNewInviteMessage);
                            }

                            @Override
                            public void onError(int code, String error) {

                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });

                    }
                });

                break;
            case Constant.USER_WANT_TO_IN_GROUP:
                holder.mType.setText("申请加入群组-" + inviteMessage.getGroupName());
                holder.mAgree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mAgree.setClickable(false);
                        holder.mRefuse.setClickable(false);


                        EMClient.getInstance().groupManager().asyncAcceptApplication(inviteMessage.getFrom(), inviteMessage.getGroupId(), new EMCallBack() {
                            @Override
                            public void onSuccess() {

                                InviteMessage aNewInviteMessage = new InviteMessage();
                                aNewInviteMessage.setId(inviteMessage.getId());
                                aNewInviteMessage.setStatus(Constant.AGREE);
                                aNewInviteMessage.setFrom(inviteMessage.getFrom());
                                aNewInviteMessage.setType(inviteMessage.getType());
                                aNewInviteMessage.setReason(inviteMessage.getReason());
                                aNewInviteMessage.setGroupId(inviteMessage.getGroupId());
                                aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                                aNewInviteMessage.setTime(inviteMessage.getTime());
                                mInviteMessageDao.update(aNewInviteMessage);
                            }

                            @Override
                            public void onError(int code, String error) {

                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });

                    }
                });
                holder.mRefuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.mAgree.setClickable(false);
                        holder.mRefuse.setClickable(false);


                        EMClient.getInstance().groupManager().asyncDeclineApplication(inviteMessage.getFrom(), inviteMessage.getGroupId(),
                                "", new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        InviteMessage aNewInviteMessage = new InviteMessage();
                                        aNewInviteMessage.setId(inviteMessage.getId());
                                        aNewInviteMessage.setStatus(Constant.REFUSE);
                                        aNewInviteMessage.setFrom(inviteMessage.getFrom());
                                        aNewInviteMessage.setType(inviteMessage.getType());
                                        aNewInviteMessage.setReason(inviteMessage.getReason());
                                        aNewInviteMessage.setGroupName(inviteMessage.getGroupName());
                                        aNewInviteMessage.setGroupId(inviteMessage.getGroupId());
                                        aNewInviteMessage.setTime(inviteMessage.getTime());
                                        mInviteMessageDao.update(aNewInviteMessage);
                                    }

                                    @Override
                                    public void onError(int code, String error) {

                                    }

                                    @Override
                                    public void onProgress(int progress, String status) {

                                    }
                                });

                    }
                });
                break;

        }


        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                return true;
            }
        });
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
        RelativeLayout mUndo;
        @BindView(R.id.done)
        TextView mDone;
        @BindView(R.id.time)
        TextView mTime;
        @BindView(R.id.cardView)
        CardView mCardView;

        ViewHolder(View view) {

            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
