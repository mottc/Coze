package com.mottc.coze.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mottc.coze.R;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.main.ContactFragment.OnContactItemClickListener;
import com.mottc.coze.utils.AvatarUtils;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 16:37
 */
public class ContactRecyclerViewAdapter extends RecyclerView.Adapter<ContactRecyclerViewAdapter.ViewHolder> {

    private final List<CozeUser> mValues;
    private final OnContactItemClickListener mListener;
    private Context context;

    public ContactRecyclerViewAdapter(List<CozeUser> items, OnContactItemClickListener listener, Context context) {
        mValues = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        String nickName = mValues.get(position).getNickName();
        String username = mValues.get(position).getUserName();
        if (nickName != null) {
            holder.mUserName.setText(nickName);
        } else {
            holder.mUserName.setText(username);
        }

        AvatarUtils.setAvatar(context,username,holder.mAvatar);


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onContactItemClick(holder.mItem,holder.mUserName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mAvatar;
        public final TextView mUserName;
        public CozeUser mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatar = (ImageView) view.findViewById(R.id.contact_avatar);
            mUserName = (TextView) view.findViewById(R.id.contact_userName);
        }

    }
}

