package com.mottc.coze.main;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
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
                groupList.clear();
                EMClient.getInstance().groupManager().asyncGetJoinedGroupsFromServer(new EMValueCallBack<List<EMGroup>>() {
                    @Override
                    public void onSuccess(List<EMGroup> value) {
                        groupList.addAll(value);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGroupRecyclerViewAdapter.notifyDataSetChanged();
                                mWaveSwipeRefreshLayout.setRefreshing(false);
                            }
                        });

                    }
                    @Override
                    public void onError(int error, String errorMsg) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity(), "请重试", Toast.LENGTH_SHORT).show();
                                mWaveSwipeRefreshLayout.setRefreshing(false);

                            }
                        });
                    }
                });

            }
        });

        View recyclerView = view.findViewById(R.id.contact_list);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) recyclerView;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            mGroupRecyclerViewAdapter = new GroupRecyclerViewAdapter(groupList, mListener,getContext());
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

    public interface OnGroupItemListener {
        void onGroupItemClick(EMGroup item,View view);
    }
}