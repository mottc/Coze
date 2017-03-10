package com.mottc.coze.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mottc.coze.R;

import java.util.List;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/10
 * Time: 14:32
 */
public class GroupMembersAdapter extends BaseAdapter {
    private List<String> group_members;
    private Context context;
    private LayoutInflater inflater;
    private String group_owner;
    private OnGroupMembersListClickListener mOnGroupMembersListClickListener;


    public GroupMembersAdapter(List<String> group_members, Context context,String owner) {
        this.group_members = group_members;
        this.context = context;
        group_owner = owner;
        inflater = LayoutInflater.from(context);
    }

    public interface OnGroupMembersListClickListener {
        // TODO: Update argument type and name
        void OnGroupMembersListClick(String item);
    }

    public void setOnGroupMembersListClickListener(OnGroupMembersListClickListener mOnGroupMembersListClickListener) {
        this.mOnGroupMembersListClickListener = mOnGroupMembersListClickListener;
    }
    @Override
    public int getCount() {
        return group_members.size();
    }

    @Override
    public Object getItem(int position) {
        return group_members.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.group_members, parent, false);
        View layout = convertView.findViewById(R.id.group_members_item);
        TextView member_name = (TextView) convertView.findViewById(R.id.member_name);
        TextView owner = (TextView) convertView.findViewById(R.id.owner);
        member_name.setText(group_members.get(position));
        if (group_owner.equals(group_members.get(position))) {
            owner.setVisibility(View.VISIBLE);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mOnGroupMembersListClickListener.OnGroupMembersListClick(group_members.get(position));

            }
        });
        return convertView;
    }

}
