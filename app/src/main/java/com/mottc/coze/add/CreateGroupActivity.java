package com.mottc.coze.add;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.R;
import com.mottc.coze.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateGroupActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.groupName)
    EditText mGroupName;
    @BindView(R.id.groupIntroduce)
    EditText mGroupIntroduce;
    @BindView(R.id.create)
    Button mCreate;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
        mToolbar.setTitle("创建群组");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(R.drawable.back);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.create)
    public void onClick() {
        String groupName = mGroupName.getText().toString().trim();
        String desc = mGroupIntroduce.getText().toString().trim();
        String reason = EMClient.getInstance().getCurrentUser() + "邀请加入群" + groupName;
        if (TextUtils.isEmpty(groupName)) {
            Toast.makeText(this, "群组名称不能为空", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在创建群组");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            createNewGroup(groupName, desc, new String[0], reason);
        }
    }
    private void createNewGroup(final String groupName, final String desc, final String[] allMembers, final String reason) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMGroupManager.EMGroupOptions option = new EMGroupManager.EMGroupOptions();
                    option.maxUsers = 200;
                    option.style = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    EMGroup group = EMClient.getInstance().groupManager().createGroup(groupName, desc, allMembers, reason, option);
                    group.getGroupId();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this, "创建成功", Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (final HyphenateException e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(CreateGroupActivity.this, "创建群组失败"+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
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
}
