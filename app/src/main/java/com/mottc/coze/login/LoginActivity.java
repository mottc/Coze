package com.mottc.coze.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.avatar.UploadAvatarActivity;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.db.CozeUserDao;
import com.mottc.coze.db.DaoMaster;
import com.mottc.coze.db.DaoSession;
import com.mottc.coze.main.MainActivity;
import com.mottc.coze.utils.CommonUtils;
import com.mottc.coze.utils.DisplayUtils;

import org.greenrobot.greendao.database.Database;

import java.util.List;

import shem.com.materiallogin.DefaultLoginView;
import shem.com.materiallogin.DefaultRegisterView;
import shem.com.materiallogin.MaterialLoginView;

public class LoginActivity extends AppCompatActivity {

    private boolean progressShow;
    private boolean autoLogin = false;
    private String loginUserName;
    private String loginPassword;
    private String RegisterUserName;
    private String RegisterPassword;
    private String RegisterPasswordRep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //如果登录成功过，直接进入主页面
        if (EMClient.getInstance().isLoggedInBefore()) {
            autoLogin = true;
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

//      使界面可以接受点击事件，以便相应软键盘消失。点击非编辑框区域，软键盘消失
        findViewById(R.id.login_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });


        //登陆
        final MaterialLoginView login = (MaterialLoginView) findViewById(R.id.loginView);
        ((DefaultLoginView) login.getLoginView()).setListener(new DefaultLoginView.DefaultLoginViewListener() {
            @Override
            public void onLogin(TextInputLayout loginUser, TextInputLayout loginPass) {

                if (!CommonUtils.isNetWorkConnected(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, R.string.network_is_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }
                //Handle login
                loginUserName = loginUser.getEditText().getText().toString();
                if (loginUserName.isEmpty()) {
                    loginUser.setError("User name can't be empty");
                    return;
                }
                loginUser.setError("");

                loginPassword = loginPass.getEditText().getText().toString();
                if (loginPassword.isEmpty()) {
                    loginPass.setError("Password can't be empty");
                    return;
                }
                loginPass.setError("");

                progressShow = true;
                final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        progressShow = false;
                    }
                });
                pd.setMessage(getString(R.string.is_landing));
                pd.show();

                // reset current loginUserName name before login
                CozeApplication.getInstance().setCurrentUserName(loginUserName);
                // close it before login to make sure DemoDB not overlap

                DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), loginUserName + ".db");
                Database db = helper.getWritableDb();
                helper.close();
                db.close();

                // 调用sdk登陆方法登陆聊天服务器
                EMClient.getInstance().login(loginUserName, loginPassword, new EMCallBack() {

                    @Override
                    public void onSuccess() {

                        if (!LoginActivity.this.isFinishing() && pd.isShowing()) {
                            pd.dismiss();
                        }

                        // 第一次登录或者之前logout后再登录，加载所有本地群和回话
                        try {
                            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        EMClient.getInstance().groupManager().loadAllGroups();
                        EMClient.getInstance().chatManager().loadAllConversations();
                        getFriends();

                        // 进入主页面
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(final int code, final String message) {
                        if (!progressShow) {
                            return;
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                pd.dismiss();
                                Toast.makeText(getApplicationContext(), getString(R.string.login_failed) + message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }
        });


        //注册
        ((DefaultRegisterView) login.getRegisterView()).setListener(new DefaultRegisterView.DefaultRegisterViewListener() {
            @Override
            public void onRegister(TextInputLayout registerUser, TextInputLayout registerPass, TextInputLayout registerPassRep) {
                //Handle register
                RegisterUserName = registerUser.getEditText().getText().toString();
                if (RegisterUserName.isEmpty()) {
                    registerUser.setError("User name can't be empty");
                    return;
                }
                registerUser.setError("");

                for(int i=0;i<RegisterUserName.length();i++) {
                    char c = RegisterUserName.charAt(i);
                    if (Character.isUpperCase(c)) {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                RegisterPassword = registerPass.getEditText().getText().toString();
                if (RegisterPassword.isEmpty()) {
                    registerPass.setError("Password can't be empty");
                    return;
                }
                registerPass.setError("");

                RegisterPasswordRep = registerPassRep.getEditText().getText().toString();
                if (!RegisterPassword.equals(RegisterPasswordRep)) {
                    registerPassRep.setError("Passwords are different");
                    return;
                }
                registerPassRep.setError("");

                if (!TextUtils.isEmpty(RegisterUserName) && !TextUtils.isEmpty(RegisterPassword)) {
                    final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
                    pd.setMessage(getResources().getString(R.string.is_registering));
                    pd.show();

                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                // 调用sdk注册方法
                                EMClient.getInstance().createAccount(RegisterUserName, RegisterPassword);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!LoginActivity.this.isFinishing())
                                            pd.dismiss();
                                        // 保存用户名
                                        CozeApplication.getInstance().setCurrentUserName(RegisterUserName);
                                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.registered_successfully), Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(LoginActivity.this, UploadAvatarActivity.class).putExtra("username",RegisterUserName)
                                        .putExtra("loginPassword",RegisterPassword).putExtra("isRegister",true));
                                        finish();
                                    }
                                });
                            } catch (final HyphenateException e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (!LoginActivity.this.isFinishing())
                                            pd.dismiss();
                                        int errorCode = e.getErrorCode();
                                        if (errorCode == EMError.NETWORK_ERROR) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_anomalies), Toast.LENGTH_SHORT).show();
                                        } else if (errorCode == EMError.USER_ALREADY_EXIST) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.user_already_exists), Toast.LENGTH_SHORT).show();
                                        } else if (errorCode == EMError.USER_AUTHENTICATION_FAILED) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed_without_permission), Toast.LENGTH_SHORT).show();
                                        } else if (errorCode == EMError.USER_ILLEGAL_ARGUMENT) {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.illegal_user_name), Toast.LENGTH_LONG).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.registration_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }
                    }).start();

                }

            }
        });


    }


    private void getFriends() {

        DaoSession daoSession = CozeApplication.getInstance().getDaoSession(loginUserName);
        CozeUserDao cozeUserDao = daoSession.getCozeUserDao();
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
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        /*点击非键盘区，键盘落下*/
        DisplayUtils.hideInputWhenTouchOtherView(this, event, null);
        return super.dispatchTouchEvent(event);
    }
}
