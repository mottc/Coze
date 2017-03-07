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
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.db.CozeUserDao;
import com.mottc.coze.db.DaoSession;

import java.util.ArrayList;
import java.util.List;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/3
 * Time: 16:33
 */
public class ContactFragment extends Fragment {


    protected List<CozeUser> contactList = new ArrayList<>();
    private OnContactItemClickListener mListener;
    private WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    private ContactRecyclerViewAdapter mContactRecyclerViewAdapter;
    RecyclerView mRecyclerView;

    public ContactFragment() {

    }

    @SuppressWarnings("unused")
    public static ContactFragment newInstance() {
        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getContactList();
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
                new ContactRefreshTask().execute();
            }
        });

        View recyclerView = view.findViewById(R.id.contact_list);
        // Set the adapter
        if (recyclerView instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) recyclerView;
            mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
            mContactRecyclerViewAdapter = new ContactRecyclerViewAdapter(contactList, mListener);
            mRecyclerView.setAdapter(mContactRecyclerViewAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContactItemClickListener) {
            mListener = (OnContactItemClickListener) context;
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


    /**
     * 获取联系人列表
     */
    protected void getContactList() {
        contactList.clear();
        // 获取联系人列表
        DaoSession daoSession = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser());
        contactList.addAll(daoSession.getCozeUserDao().loadAll());
    }


    private class ContactRefreshTask extends AsyncTask<Void, Void, String[]> {
        CozeUserDao cozeUserDao;
        @Override
        protected String[] doInBackground(Void... params) {

            contactList.clear();
            DaoSession daoSession = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser());
            cozeUserDao = daoSession.getCozeUserDao();
            cozeUserDao.deleteAll();
            try {
//            从服务器获取好友列表
                List<String> userNames = EMClient.getInstance().contactManager().getAllContactsFromServer();
//            存入数据库
                for (String userName : userNames) {
                    CozeUser cozeUser = new CozeUser(null, userName, null, null);
                    cozeUserDao.insert(cozeUser);
                }

            } catch (HyphenateException e) {
                e.printStackTrace();
            }
            return new String[0];
        }

        @Override
        protected void onPostExecute(String[] result) {
            // Call setRefreshing(false) when the list has been refreshed.

            super.onPostExecute(result);
            contactList.addAll(cozeUserDao.loadAll());
            mContactRecyclerViewAdapter.notifyDataSetChanged();
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public interface OnContactItemClickListener {
        void onContactItemClick(CozeUser item,View view);
    }
}
