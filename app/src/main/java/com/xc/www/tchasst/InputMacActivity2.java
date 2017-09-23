package com.xc.www.tchasst;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.xc.www.utils.UpDateMacUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/19.
 */
public class InputMacActivity2 extends AppCompatActivity {

    @BindView(R.id.btn_input)
    Button input_btn;
    @BindView(R.id.tv_success_count)
    TextView successCount_tv;
    @BindView(R.id.tv_error_count)
    TextView errorCount_tv;
    @BindView(R.id.tv_repeat_count)
    TextView repeatCount_tv;
    @BindView(R.id.tb_input_mac)
    Toolbar mToolbar;


    private UpDateMacUtil mUpDateMacUtil;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            successCount_tv.setText("录入成功条数："+data.getInt("successCount"));
            errorCount_tv.setText("录入失败条数："+data.getInt("errorCount"));
            repeatCount_tv.setText("MAC重复条数："+data.getInt("repeatCount"));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mac);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        mUpDateMacUtil = new UpDateMacUtil(this,null,mHandler);

        mToolbar.setTitle("MAC录入");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.btn_input)
    public void inputMac(){
        mUpDateMacUtil.doDiscovery();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
