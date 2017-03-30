package com.mottc.coze.main;

import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
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
import com.mottc.coze.utils.CommonUtils;
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
    private CozeContactListener mCozeContactListener;
    private CozeGroupChangeListener mCozeGroupChangeListener;
    private ContextMenuDialogFragment mMenuDialogFragment;
    private FragmentManager fragmentManager;
    private InviteMessageDao mInviteMessageDao;
    private CozeUserDao mCozeUserDao;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
            mMenuFragment = MenuListFragment.getInstance();
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
            showTips();
        }
    }

    private void showTips() {

        AlertDialog alertDialog = new AlertDialog
                .Builder(this)
                .setTitle("提醒")
                .setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        })
                .create(); // 创建对话框

        alertDialog.show(); // 显示对话框
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


            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setReason(reason);
            msg.setType(Constant.USER_WANT_TO_BE_FRIEND);
            msg.setTime(CommonUtils.getTime());
            msg.setStatus(Constant.UNDO);
            mInviteMessageDao.insert(msg);

            notificationWithIntent(username+"请求加你为好友", reason);

        }

        @Override
        public void onFriendRequestAccepted(String username) {
            notificationWithoutIntent(username,"同意了你的好友请求");
        }

        @Override
        public void onFriendRequestDeclined(String username) {
            notificationWithoutIntent(username,"拒绝了你的好友请求");
        }

        public void notificationWithIntent(String title, String text) {
            Intent intent = new Intent(mContext, MessageActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(mContext, 1, intent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.myicon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), builder.build());

        }

        public void notificationWithoutIntent(String title, String text) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.myicon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), builder.build());

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
            inviteMessage.setGroupId(groupId);
            inviteMessage.setReason(reason);
            inviteMessage.setTime(CommonUtils.getTime());
            inviteMessage.setType(Constant.USER_INVITE_TO_GROUP);
            inviteMessage.setStatus(Constant.UNDO);
            mInviteMessageDao.insert(inviteMessage);

            notificationWithIntent(inviter+"邀请你加入群组-"+groupName,reason);

        }



        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
//          applicant要求进群
            InviteMessage inviteMessage = new InviteMessage();
            inviteMessage.setFrom(applicant);
            inviteMessage.setGroupName(groupName);
            inviteMessage.setGroupId(groupId);
            inviteMessage.setReason(reason);
            inviteMessage.setTime(CommonUtils.getTime());
            inviteMessage.setType(Constant.USER_WANT_TO_IN_GROUP);
            inviteMessage.setStatus(Constant.UNDO);
            mInviteMessageDao.insert(inviteMessage);
            notificationWithIntent(applicant+"申请加入群组-"+groupName,reason);
        }

        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {

            notificationWithoutIntent(accepter,"已同意你加入" + groupName);

        }

        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {

            notificationWithoutIntent(decliner,"已拒绝你加入" + groupName);
        }

        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {

            notificationWithoutIntent(invitee,"接受了你的加群邀请");
        }


        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {

            notificationWithoutIntent(invitee,"拒绝了你的加群邀请");
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


        private void notificationWithIntent(String title, String text) {
            Intent intent = new Intent(mContext, MessageActivity.class);

            PendingIntent pIntent = PendingIntent.getActivity(mContext, 1, intent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.myicon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(pIntent)
                    .setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), builder.build());

        }
        public void notificationWithoutIntent(String title, String text) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext)
                    .setSmallIcon(R.drawable.myicon)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
            mNotificationManager.notify((int) System.currentTimeMillis(), builder.build());

        }
    }
}
