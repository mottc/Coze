package com.mottc.coze.add;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.R;
import com.mottc.coze.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddGroupActivity extends AppCompatActivity {

    @BindView(R.id.add_friend_toolbar)
    Toolbar mAddFriendToolbar;
    @BindView(R.id.group_num)
    EditText mGroupNum;
    @BindView(R.id.search_for_group)
    Button mSearchForGroup;
    @BindView(R.id.search_view)
    LinearLayout mSearchView;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.add)
    Button mAdd;
    @BindView(R.id.rl_searched_group)
    RelativeLayout mRlSearchedGroup;


    public static EMGroup searchedGroup;

    String groupNum;

    Boolean isVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        ButterKnife.bind(this);
        mAddFriendToolbar.setTitle("加入群组");
        setSupportActionBar(mAddFriendToolbar);
        mAddFriendToolbar.setNavigationIcon(R.drawable.back);
        mAddFriendToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.search_for_group, R.id.add})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_for_group:

                groupNum = mGroupNum.getText().toString().trim();

                if (TextUtils.isEmpty(groupNum)) {
                    Toast.makeText(this, "群号不能为空", Toast.LENGTH_SHORT).show();
                } else {

                    final ProgressDialog pd = new ProgressDialog(this);
                    pd.setMessage(getResources().getString(R.string.searching));
                    pd.setCancelable(false);
                    pd.show();

                    new Thread(new Runnable() {

                        public void run() {
                            try {
                                searchedGroup = EMClient.getInstance().groupManager().getGroupFromServer(groupNum);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.dismiss();
                                        mRlSearchedGroup.setVisibility(View.VISIBLE);
                                        mSearchView.setVisibility(View.GONE);
                                        isVisible = true;
                                        mName.setText(searchedGroup.getGroupName());
                                    }
                                });

                            } catch (final HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        pd.dismiss();
                                        searchedGroup = null;
                                        mRlSearchedGroup.setVisibility(View.GONE);
                                        if (e.getErrorCode() == EMError.GROUP_INVALID_ID) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_not_existed), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.group_search_failed) + " : " + getString(R.string.connect_failuer_toast), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();

                }
                break;
            case R.id.add:
                final ProgressDialog pd = new ProgressDialog(this);
                pd.setMessage("正在发送请求");
                pd.setCanceledOnTouchOutside(false);
                pd.show();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (searchedGroup.isMemberOnly()) {
                                EMClient.getInstance().groupManager().applyJoinToGroup(groupNum, "我是" + EMClient.getInstance().getCurrentUser());
                            } else {
                                EMClient.getInstance().groupManager().joinGroup(groupNum);
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pd.dismiss();
                                    if (searchedGroup.isMembersOnly()) {
                                        Toast.makeText(AddGroupActivity.this, "请求已发送，等待同意", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AddGroupActivity.this, "已加入群组", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (final HyphenateException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    pd.dismiss();
                                    Toast.makeText(AddGroupActivity.this, "加入群组失败" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /*点击非键盘区，键盘落下*/
        DisplayUtils.hideInputWhenTouchOtherView(this, event, null);
        return super.dispatchTouchEvent(event);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (isVisible) {
                mRlSearchedGroup.setVisibility(View.GONE);
                mSearchView.setVisibility(View.VISIBLE);
                isVisible = false;
                return false;
            } else {
                finish();
                return false;
            }

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
