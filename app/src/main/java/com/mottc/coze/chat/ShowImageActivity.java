package com.mottc.coze.chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mottc.coze.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowImageActivity extends AppCompatActivity {

    @BindView(R.id.showImage)
    ImageView mShowImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ButterKnife.bind(this);
        String imageUrl = this.getIntent().getStringExtra("imageUrl");
        Glide
                .with(this)
                .load(imageUrl)
                .into(mShowImage);
    }

    @OnClick(R.id.showImage)
    public void onClick() {
        finish();
    }
}
