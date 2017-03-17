package com.mottc.coze.main;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.util.NetUtils;
import com.mottc.coze.Constant;
import com.mottc.coze.CozeApplication;
import com.mottc.coze.R;
import com.mottc.coze.add.AddGroupActivity;
import com.mottc.coze.add.AddNewFriendActivity;
import com.mottc.coze.add.CreateGroupActivity;
import com.mottc.coze.bean.CozeUser;
import com.mottc.coze.bean.InviteMessage;
import com.mottc.coze.chat.ChatActivity;
import com.mottc.coze.db.CozeUserDao;
import com.mottc.coze.db.InviteMessageDao;
import com.mottc.coze.message.MessageActivity;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private CozeContactListener mCozeContactListener;
    private CozeGroupChangeListener mCozeGroupChangeListener;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;
    private InviteMessageDao mInviteMessageDao;
    private CozeUserDao mCozeUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        mInviteMessageDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getInviteMessageDao();
        mCozeUserDao = CozeApplication.getInstance().getDaoSession(EMClient.getInstance().getCurrentUser()).getCozeUserDao();
        setupToolbar();
        setupMenu();
        initMenuFragment();
        setupViewpager();

        //注册一个监听连接状态的listener
        mCozeConnectionListener = new CozeConnectionListener(this);
        mCozeContactListener = new CozeContactListener(this);
        mCozeGroupChangeListener = new CozeGroupChangeListener(this);
        EMClient.getInstance().addConnectionListener(mCozeConnectionListener);
        EMClient.getInstance().contactManager().setContactListener(mCozeContactListener);
        EMClient.getInstance().groupManager().addGroupChangeListener(mCozeGroupChangeListener);

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
                switch (position) {
                    case 1:
                        startActivity(new Intent(MainActivity.this, AddNewFriendActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, AddGroupActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, CreateGroupActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this, MessageActivity.class));
                        break;
                }
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
        EMClient.getInstance().contactManager().removeContactListener(mCozeContactListener);
        EMClient.getInstance().groupManager().removeGroupChangeListener(mCozeGroupChangeListener);
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

    private class CozeContactListener implements EMContactListener {
        private Context mContext;

        public CozeContactListener(Context context) {
            mContext = context;
        }

        @Override
        public void onContactAdded(String username) {
            mCozeUserDao.insertOrReplace(new CozeUser(null, username, null, null));

        }

        @Override
        public void onContactDeleted(String username) {
            CozeUser deletedCozeUser = mCozeUserDao.queryBuilder().where(CozeUserDao.Properties.UserName.eq(username)).build().unique();
            mCozeUserDao.deleteByKey(deletedCozeUser.getId());
        }

        @Override
        public void onContactInvited(String username, String reason) {

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date curDate = new Date(System.currentTimeMillis());
            String time = formatter.format(curDate);
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setReason(reason);
            msg.setType(Constant.USER_WANT_TO_BE_FRIEND);
            msg.setTime(time);
            msg.setStatus(Constant.UNDO);
            mInviteMessageDao.insert(msg);
//            TODO:通知

        }

        @Override
        public void onFriendRequestAccepted(final String username) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.add)
                    .setContentTitle(username)
                    .setContentText("请求加你为好友")
                    .setAutoCancel(true);

            NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(1, builder.build());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, username + "同意了你的好友请求", Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public void onFriendRequestDeclined(final String username) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, username + "拒绝了你的好友请求", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class CozeGroupChangeListener implements EMGroupChangeListener {
        private Context mContext;

        public CozeGroupChangeListener(Context context) {
            mContext = context;
        }

        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
//          inviter邀请当前用户进群
            InviteMessage inviteMessage = new InviteMessage();
            inviteMessage.setFrom(inviter);
            inviteMessage.setGroupName(groupName);
            inviteMessage.setReason(reason);
            inviteMessage.setType(Constant.USER_INVITE_TO_GROUP);
            inviteMessage.setStatus(Constant.UNDO);
            mInviteMessageDao.insert(inviteMessage);
        }

        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
//          applicant要求进群
            InviteMessage inviteMessage = new InviteMessage();
            inviteMessage.setFrom(applicant);
            inviteMessage.setGroupName(groupName);
            inviteMessage.setReason(reason);
            inviteMessage.setType(Constant.USER_WANT_TO_IN_GROUP);
            inviteMessage.setStatus(Constant.UNDO);
            mInviteMessageDao.insert(inviteMessage);

        }

        @Override
        public void onRequestToJoinAccepted(String groupId, final String groupName, final String accepter) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, accepter + "已同意你加入" + groupName, Toast.LENGTH_SHORT).show();

                }
            });

        }

        @Override
        public void onRequestToJoinDeclined(String groupId, final String groupName, final String decliner, String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(mContext, decliner + "已拒绝你加入" + groupName, Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public void onInvitationAccepted(String groupId, final String invitee, String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(mContext, invitee + "已同意你的加群邀请", Toast.LENGTH_SHORT).show();

                }
            });
        }


        @Override
        public void onInvitationDeclined(String groupId, final String invitee, String reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(mContext, invitee + "拒绝了你的加群邀请", Toast.LENGTH_SHORT).show();

                }
            });
        }

        @Override
        public void onUserRemoved(String groupId, String groupName) {

        }

        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {

        }
    }
}
