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
import com.mottc.coze.R;
import com.mottc.coze.utils.DisplayUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddNewFriendActivity extends AppCompatActivity {


    ProgressDialog progressDialog;
    @BindView(R.id.add_friend_toolbar)
    Toolbar mAddFriendToolbar;
    @BindView(R.id.et_username)
    EditText mEtUsername;
    @BindView(R.id.et_reason)
    EditText mEtReason;
    @BindView(R.id.btn_add)
    Button mBtnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_friend);
        ButterKnife.bind(this);
        String new_name = this.getIntent().getStringExtra("new_name");
        if (new_name != null) {
            mEtReason.setText(new_name);
        }
        setSupportActionBar(mAddFriendToolbar);
        mAddFriendToolbar.setTitle("添加好友");
        mAddFriendToolbar.setNavigationIcon(R.drawable.back);
        mAddFriendToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick(R.id.btn_add)
    public void onClick() {

        String username = mEtUsername.getText().toString().trim();
        String reason = mEtReason.getText().toString().trim();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(getApplicationContext(), "请输入用户名", Toast.LENGTH_SHORT).show();
            return;

        }
        addContact(username, reason);
    }

    private void addContact(final String username, final String reason) {

        progressDialog = new ProgressDialog(this);
        String sending = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(sending);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    EMClient.getInstance().contactManager().addContact(username, reason);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
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
