package com.mottc.coze.main;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.R;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 16:46
 */
public class GroupFragment extends Fragment {
    private OnGroupItemListener mListener;
    private RecyclerView mRecyclerView;
    private GroupRecyclerViewAdapter mGroupRecyclerViewAdapter;
    private List<EMGroup> groupList = new ArrayList<>();
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;


    public GroupFragment() {
    }

    @SuppressWarnings("unused")
    public static GroupFragment newInstance() {
        GroupFragment fragment = new GroupFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getGroupList();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);

        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) view.findViewById(R.id.contact_swipe);
        mWaveSwipeRefreshLayout.setWaveRGBColor(63, 81, 181);
        mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Do work to refresh the list here.
                new GroupRefreshTask().execute();
            }
        });

        View recyclerView = view.findViewById(R.id.contact_list);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) recyclerView;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            mGroupRecyclerViewAdapter = new GroupRecyclerViewAdapter(groupList, mListener);
            mRecyclerView.setAdapter(mGroupRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnGroupItemListener) {
            mListener = (OnGroupItemListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    private void getGroupList() {
        groupList.clear();
        groupList.addAll(EMClient.getInstance().groupManager().getAllGroups());
    }

    private class GroupRefreshTask extends AsyncTask<Void, Void, String[]> {

        List<EMGroup> groupListFromServer = new ArrayList<>();
        @Override
        protected String[] doInBackground(Void... params) {

            groupList.clear();

            try {
                groupListFromServer = EMClient.getInstance().groupManager().getJoinedGroupsFromServer();//需异步处理
            } catch (HyphenateException e) {
                e.printStackTrace();
            }

            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.

            super.onPostExecute(result);
            groupList.addAll(groupListFromServer);
            mGroupRecyclerViewAdapter.notifyDataSetChanged();
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }
    }


    public interface OnGroupItemListener {
        void onGroupItemClick(EMGroup item,View view);
    }
}