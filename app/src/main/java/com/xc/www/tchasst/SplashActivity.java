package com.xc.www.tchasst;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.xc.www.app.AppApplication;
import com.xc.www.bean.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;

/**
 * Created by Administrator on 2016/10/8.
 */
public class SplashActivity extends AppCompatActivity {

    @BindView(R.id.tv_splash)
    TextView textView;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent=new Intent(SplashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        //可设置调试模式，当为true的时候，会在logcat的BmobChat下输出一些日志，包括推送服务是否正常运行，如果服务端返回错误，也会一并打印出来。方便开发者调试
//        BmobChat.DEBUG_MODE = true;

        //第二：自v3.4.7版本开始,设置BmobConfig,允许设置请求超时时间、文件分片上传时每片的大小、文件的过期时间(单位为秒)，
        BmobConfig config =new BmobConfig.Builder(this)
                //设置appkey
                .setApplicationId("4d9ccf0a98fe87ca8d1833c3494d2bc2")
                //请求超时时间（单位为秒）：默认15s
                .setConnectTimeout(30)
                //文件分片上传时每片的大小（单位字节），默认512*1024
                .setUploadBlockSize(1024*1024)
                //文件的过期时间(单位为秒)：默认1800s
                .setFileExpiration(2500)
                .build();
        Bmob.initialize(config);

        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);

        initData();

        handler.sendEmptyMessageDelayed(0,1000);

        //无法显示出控件
//        try {
//            Thread.sleep(1000);
//            Intent intent=new Intent(this,MainActivity.class);
//            startActivity(intent);
//            finish();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    private void initData() {
        AppApplication appContext=AppApplication.getInstance();
        appContext.currentUser=getCurrentUser();
    }

    public User getCurrentUser() {
        User currentUser = BmobUser.getCurrentUser(User.class);
        return currentUser;
    }
}
