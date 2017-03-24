package com.mottc.coze.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMGroup;
import com.mottc.coze.R;
import com.mottc.coze.main.GroupFragment.OnGroupItemListener;
import com.mottc.coze.utils.AvatarUtils;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/6
 * Time: 21:03
 */
public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {

    private final List<EMGroup> mValues;
    private final OnGroupItemListener mListener;
    private Context context;

    public GroupRecyclerViewAdapter(List<EMGroup> items, OnGroupItemListener listener, Context context) {
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
        holder.mUserName.setText(mValues.get(position).getGroupName());
        AvatarUtils.groupSetAvatar(context, mValues.get(position).getGroupId(), holder.mAvatar);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onGroupItemClick(holder.mItem, holder.mUserName);
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
        public EMGroup mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mAvatar = (ImageView) view.findViewById(R.id.contact_avatar);
            mUserName = (TextView) view.findViewById(R.id.contact_userName);
        }

    }
}
