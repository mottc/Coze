package com.mottc.coze.avatar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.main.MainActivity;
import com.mottc.coze.utils.AvatarUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UploadAvatarActivity extends AppCompatActivity {

    @BindView(R.id.upload_avatar_toolbar)
    Toolbar mUploadAvatarToolbar;
    @BindView(R.id.upload_avatar)
    ImageView mUploadAvatar;
    @BindView(R.id.pic_from_image)
    Button mPicFromImage;
    @BindView(R.id.pic_from_camera)
    Button mPicFromCamera;
    @BindView(R.id.uploading)
    LinearLayout mUploading;

    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    private UploadManager mUploadManager;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_avatar);
        ButterKnife.bind(this);
        setupToolbar();
        mUploadManager = new UploadManager();
        username = this.getIntent().getStringExtra("username");
        password = this.getIntent().getStringExtra("loginPassword");
    }

    private void setupToolbar() {
        mUploadAvatarToolbar.setTitle("设置头像");
        setSupportActionBar(mUploadAvatarToolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 结果码不等于取消时候
        if (resultCode != RESULT_CANCELED) {

            switch (requestCode) {
                case IMAGE_REQUEST_CODE:
                    startPhotoZoom(data.getData());
                    break;
                case CAMERA_REQUEST_CODE:
                    File picture = new File(
                            Environment.getExternalStorageDirectory() + "/head/temp.jpg");
                    startPhotoZoom(Uri.fromFile(picture));
                    break;
                case RESULT_REQUEST_CODE:
                    if (data != null) {
                        getImageToView(data);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 320);
        intent.putExtra("outputY", 320);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }

    private void getImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            mUploadAvatar.setImageDrawable(drawable);
            upload(photo);
        }
    }

    private void upload(Bitmap photo) {
        mUploading.setVisibility(View.VISIBLE);
        mPicFromImage.setClickable(false);
        mPicFromCamera.setClickable(false);
        String token = AvatarUtils.createImageToken(username);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //设置上传后文件的key
        String upkey = username + ".png";
        mUploadManager.put(data, upkey, token, new UpCompletionHandler() {
            public void complete(String key, ResponseInfo rinfo, JSONObject response) {

                EMClient.getInstance().login(username, password, new EMCallBack() {

                    @Override
                    public void onSuccess() {
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
                        Intent intent = new Intent(UploadAvatarActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(final int code, final String message) {

                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), getString(R.string.login_failed) + message,
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }, null);

    }


    @OnClick({R.id.pic_from_image, R.id.pic_from_camera})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pic_from_image:
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*"); // 设置文件类型
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, IMAGE_REQUEST_CODE);
                break;
            case R.id.pic_from_camera:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/head/", "temp.jpg")));
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
                break;
        }
    }


    private void getFriends() {

        DaoSession daoSession = CozeApplication.getInstance().getDaoSession(username);
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
}