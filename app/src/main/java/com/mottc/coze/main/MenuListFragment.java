package com.mottc.coze.main;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.mottc.coze.Constant;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.login.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuListFragment extends Fragment {


    ImageView user_photo;
    TextView user_name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_navigation_menu, container, false);

        NavigationView vNavigation = (NavigationView) view.findViewById(R.id.vNavigation);
        View headerLayout = vNavigation.getHeaderView(0);
        user_photo = (ImageView) headerLayout.findViewById(R.id.userPhoto);
        user_name = (TextView) headerLayout.findViewById(R.id.userName);

        vNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();

                switch (id) {
                    case R.id.menu_change:
                        logout();
                        break;
                    case R.id.menu_feed:
                        Toast.makeText(getActivity(), "feed", Toast.LENGTH_SHORT).show();
                        break;

                }

                return false;
            }
        });
        setupHeader();
        return view;
    }

    private void logout() {
        CozeApplication.getInstance().logout(true, new EMCallBack() {
            @Override
            public void onSuccess() {
                getActivity().finish();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }

            @Override
            public void onError(int code, String error) {

                Toast.makeText(getActivity(), R.string.try_again, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    private void setupHeader() {

        Glide
                .with(getActivity())
                .load(Constant.SPLASH_URL)
                .asBitmap()
                .error(R.drawable.window_bg)
                .centerCrop()
                .into(new BitmapImageViewTarget(user_photo) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(getActivity().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        user_photo.setImageDrawable(circularBitmapDrawable);
                    }
                });

        user_name.setText(EMClient.getInstance().getCurrentUser());
    }


}
