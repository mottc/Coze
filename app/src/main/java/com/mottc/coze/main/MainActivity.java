package com.mottc.coze.main;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.util.NetUtils;
import com.mottc.coze.Constant;
import com.mottc.coze.R;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.chat.ChatActivity;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.List;

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
    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();

        setupToolbar();
        setupMenu();
        initMenuFragment();
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

    private void initMenuFragment() {
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize(200);
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(new OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(View clickedView, int position) {
//                Toast.makeText(Parent(), String.valueOf(position), Toast.LENGTH_SHORT).show();
//TODO:

            }
        });
    }

    private List<MenuObject> getMenuObjects() {

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setBgResource(R.color.colorPrimary);
        close.setResource(R.drawable.cancel);

        MenuObject add_friend = new MenuObject(getString(R.string.add_friend));
        add_friend.setBgResource(R.color.colorPrimary);
        add_friend.setResource(R.drawable.person_add);

        MenuObject add_group = new MenuObject(getString(R.string.add_group));
        add_group.setBgResource(R.color.colorPrimary);
        add_group.setResource(R.drawable.group_add);

        MenuObject create_group = new MenuObject(getString(R.string.create_group));
        create_group.setBgResource(R.color.colorPrimary);
        create_group.setResource(R.drawable.group);

        MenuObject notification = new MenuObject(getString(R.string.notification));
        notification.setBgResource(R.color.colorPrimary);
        notification.setResource(R.drawable.notifications);

        menuObjects.add(close);
        menuObjects.add(add_friend);
        menuObjects.add(add_group);
        menuObjects.add(create_group);
        menuObjects.add(notification);
        return menuObjects;
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if (fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, ContextMenuDialogFragment.TAG);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
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


    private class CozeConnectionListener implements EMConnectionListener {

        private Context mContext;

        public CozeConnectionListener(Context context) {
            mContext = context;
        }

        @Override
        public void onConnected() {
        }

        @Override
        public void onDisconnected(final int error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (error == EMError.USER_REMOVED) {
                        // 显示帐号已经被移除
                        Toast.makeText(mContext, R.string.user_removed, Toast.LENGTH_SHORT).show();
                    } else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                        // 显示帐号在其他设备登录
                        Toast.makeText(mContext, R.string.user_login_another_device, Toast.LENGTH_SHORT).show();
                    } else if (NetUtils.hasNetwork(mContext)) {
                        //连接不到聊天服务器
                        Toast.makeText(mContext, R.string.can_not_connect, Toast.LENGTH_SHORT).show();
                    } else {
                        //当前网络不可用，请检查网络设置
                        Toast.makeText(mContext, R.string.network_anomalies, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
