package com.xc.www.tchasst;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xc.www.adapter.MainViewPagerAdapter;
import com.xc.www.app.AppSharedPreferences;
import com.xc.www.app.ConstantValues;
import com.xc.www.fragment.NoteFragment;
import com.xc.www.fragment.NotificationFragment;
import com.xc.www.fragment.RollCallFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.dl_main_drawer)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nv_main_navigation)
    NavigationView mNavigationView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tabs)
    TabLayout mTabLayout;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.fab)
    FloatingActionButton mFloatingActionButton;

    private TextView login_tv;
    private LinearLayout login_ll;
    private LinearLayout logout_ll;

    private RollCallFragment mRollCallFragment;
    private NoteFragment mNoteFragment;
    private TextView userNmae_tv;

    private static int currentPagerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setupViewPager();
        selectPager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentPagerItem=mViewPager.getCurrentItem();  //配合onPause对currentPagerItem的操作

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String notificationIds = AppSharedPreferences.getString(ConstantValues.NOTIFICATION_ID, "");
        String ids[]=notificationIds.split(",");
        if (ids.length>1){
            for (int i=1;i<ids.length;i++){
                notificationManager.cancel(Integer.parseInt(ids[i]));
            }
            AppSharedPreferences.putStirng(ConstantValues.NOTIFICATION_ID,"");
        }
    }

    private void selectPager() {
        boolean message= AppSharedPreferences.getBoolean(ConstantValues.NOTIFICATION,false);
        if (message==true){
            mViewPager.setCurrentItem(2);
        }
    }

    private void setupViewPager() {
        List<String> titiles=new ArrayList<>();
        titiles.add("课程");
        titiles.add("备忘录");
        titiles.add("教务通知");
        mTabLayout.addTab(mTabLayout.newTab().setText(titiles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titiles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titiles.get(2)));

        List<Fragment> fragments=new ArrayList<>();
        mRollCallFragment = new RollCallFragment();
        mNoteFragment=new NoteFragment();
        fragments.add(mRollCallFragment);
        fragments.add(mNoteFragment);
        fragments.add(new NotificationFragment());
        MainViewPagerAdapter viewpagerAdapter=new MainViewPagerAdapter(getSupportFragmentManager(),titiles,fragments);
        mViewPager.setAdapter(viewpagerAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(viewpagerAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                invalidateOptionsMenu();  //刷新菜单

                currentPagerItem=position;

                if (position==2){
                    mFloatingActionButton.setVisibility(View.INVISIBLE);
                }else {
                    mFloatingActionButton.setVisibility(View.VISIBLE);
                }

                AppSharedPreferences.putBoolean(ConstantValues.NOTIFICATION,false);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initView() {
        ButterKnife.bind(this);

        mToolbar.setTitle("TchAsst");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //侧边栏菜单项视图设置
        setupDrawerContent(mNavigationView);

        View headerView = mNavigationView.getHeaderView(0);

        ImageView imageView= (ImageView) headerView.findViewById(R.id.iv_icon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                startActivityForResult(intent,1);
            }
        });

        login_tv= (TextView) headerView.findViewById(R.id.tv_login);
        login_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivityForResult(intent,100);
            }
        });

        login_ll = (LinearLayout) headerView.findViewById(R.id.ll_login);
        logout_ll = (LinearLayout) headerView.findViewById(R.id.ll_logout);

        userNmae_tv = (TextView) headerView.findViewById(R.id.tv_username);
        BmobUser currentUser = BmobUser.getCurrentUser();
        if (currentUser!=null){
            login_ll.setVisibility(View.GONE);
            logout_ll.setVisibility(View.VISIBLE);
            userNmae_tv.setText(currentUser.getUsername());
        }

        TextView logout_tv= (TextView) headerView.findViewById(R.id.tv_logout);
        logout_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BmobUser.logOut();   //清除缓存用户对象
                BmobUser currentUser = BmobUser.getCurrentUser(); // 现在的currentUser是null了

                login_ll.setVisibility(View.VISIBLE);
                logout_ll.setVisibility(View.GONE);
            }
        });

    }

    @OnClick(R.id.fab)
    public void fabClick(FloatingActionButton button){
        switch (mViewPager.getCurrentItem()){
            case 0:
                mRollCallFragment.fabClick(button);
                break;
            case 1:
                mNoteFragment.fabClick(button);
                break;
            case 2:
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 100:
                if (resultCode==RESULT_OK){
                    String userName=data.getExtras().getString("userName");
                    login_ll.setVisibility(View.GONE);
                    logout_ll.setVisibility(View.VISIBLE);
                    userNmae_tv.setText(userName);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();   //清空菜单
        MenuInflater menuInflater=getMenuInflater();
        switch (mViewPager.getCurrentItem()){
            case 1:
                menuInflater.inflate(R.menu.note_menu,menu);
                break;
            default:
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);//点击Home键时，将NavigationView拖出
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView mNavigationView) {
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.nav_attendance_manager:
                                Intent RollCallManagerIntent=new Intent(MainActivity.this,RollCallManagerActivity.class);
                                startActivity(RollCallManagerIntent);
                                mDrawerLayout.closeDrawers();
                                break;
//                            case R.id.nav_notification:
//                                break;
                            case R.id.nav_input_mac:
                                Intent InputMacIntent=new Intent(MainActivity.this,InputMacActivity2.class);
                                startActivity(InputMacIntent);
                                mDrawerLayout.closeDrawers();
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed() {
        switch (mViewPager.getCurrentItem()){
            case 1:
                if (mDrawerLayout.isDrawerOpen(mNavigationView)){
                    mDrawerLayout.closeDrawers();
                }else if (!mNoteFragment.onBackPressedFragment()){
                    super.onBackPressed();
                }
                break;
            default:
                if (mDrawerLayout.isDrawerOpen(mNavigationView)){
                    mDrawerLayout.closeDrawers();
                }else
                    super.onBackPressed();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPagerItem=0;  //因为currentPagerItem是static变量，所以就算应用退出后，MyPushMessageReceiver调用getViewPagerCurrentItem时，得到的是之前保留的值
    }

    public static int getViewPagerCurrentItem(){
        return currentPagerItem;
    }
}
