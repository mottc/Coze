package com.mottc.coze.detail;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mottc.coze.R;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/10
 * Time: 14:32
 */
public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.ViewHolder> {
    private List<String> group_members;
    private String group_owner;
    private OnGroupMembersListClickListener mOnGroupMembersListClickListener;


    public GroupMembersAdapter(List<String> group_members,String owner) {
        this.group_members = group_members;
        group_owner = owner;
    }

    public interface OnGroupMembersListClickListener {
        void OnGroupMembersListClick(String item);
    }

    public void setOnGroupMembersListClickListener(OnGroupMembersListClickListener mOnGroupMembersListClickListener) {
        this.mOnGroupMembersListClickListener = mOnGroupMembersListClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.member_name.setText(group_members.get(position));
        if (group_owner.equals(group_members.get(position))) {
            holder.owner.setVisibility(View.VISIBLE);
        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnGroupMembersListClickListener.OnGroupMembersListClick(group_members.get(position));

            }
        });

    }



    @Override
    public int getItemCount() {
        return group_members.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public TextView member_name;
        public TextView owner;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            member_name = (TextView) view.findViewById(R.id.member_name);
            owner = (TextView) view.findViewById(R.id.owner);
        }

    }

}
