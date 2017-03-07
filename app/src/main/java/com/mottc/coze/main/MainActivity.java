package com.mottc.coze.main;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.chat.ChatActivity;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements ContactFragment.OnContactItemClickListener,
        ConversationFragment.OnConversationItemClickListener,
        GroupFragment.OnGroupItemListener {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewpager;
    @BindView(R.id.drawer_layout)
    FlowingDrawer mDrawerLayout;

    private CozeConnectionListener mCozeConnectionListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupToolbar();
        setupMenu();
        setupViewpager();
        //注册一个监听连接状态的listener
        mCozeConnectionListener = new CozeConnectionListener(this);
        EMClient.getInstance().addConnectionListener(mCozeConnectionListener);

    }

    private void setupToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("Coze");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.toggleMenu();
            }
        });
    }

    private void setupMenu() {
        FragmentManager fm = getSupportFragmentManager();
        MenuListFragment mMenuFragment = (MenuListFragment) fm.findFragmentById(R.id.id_container_menu);
        if (mMenuFragment == null) {
            mMenuFragment = new MenuListFragment();
            fm.beginTransaction().add(R.id.id_container_menu, mMenuFragment).commit();
        }
    }

    private void setupViewpager() {


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(ConversationFragment.newInstance(), "消息");//添加Fragment
        viewPagerAdapter.addFragment(ContactFragment.newInstance(), "通讯录");
        viewPagerAdapter.addFragment(GroupFragment.newInstance(), "群组");
        mViewpager.setAdapter(viewPagerAdapter);//设置适配器


        mTabLayout.addTab(mTabLayout.newTab().setText("消息"));//给TabLayout添加Tab
        mTabLayout.addTab(mTabLayout.newTab().setText("通讯录"));
        mTabLayout.addTab(mTabLayout.newTab().setText("群组"));
        //给TabLayout设置关联ViewPager，如果设置了ViewPager，那么ViewPagerAdapter中的getPageTitle()方法返回的就是Tab上的标题
        mTabLayout.setupWithViewPager(mViewpager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(mCozeConnectionListener);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isMenuVisible()) {
            mDrawerLayout.closeMenu();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onContactItemClick(CozeUser item, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view, "chatToUserName");
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("toUsername", item.getUserName()).putExtra("chat_type", Constant.USER);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onGroupItemClick(EMGroup item, View view) {
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view, "chatToUserName");
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("toUsername", item.getGroupId()).putExtra("chat_type", Constant.GROUP);
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onConversationItemClick(EMConversation item, View view) {

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this, view, "chatToUserName");
        Intent intent = new Intent(this, ChatActivity.class);
        if (item.isGroup()) {
            intent.putExtra("toUsername", item.conversationId()).putExtra("chat_type", Constant.GROUP);
        } else {
            intent.putExtra("toUsername", item.conversationId()).putExtra("chat_type", Constant.USER);
        }
        startActivity(intent, options.toBundle());
    }
}
