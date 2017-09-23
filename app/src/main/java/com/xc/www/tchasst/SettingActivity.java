package com.xc.www.tchasst;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xc.www.app.AppSharedPreferences;
import com.xc.www.app.ConstantValues;
import com.xc.www.utils.TableTimer;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置页面
 * Created by Administrator on 2016/11/9.
 */
public class SettingActivity extends AppCompatActivity{

    @BindView(R.id.tb_setting)
    Toolbar mToolbar;
    @BindView(R.id.tv_week)
    TextView week_tv;
    @BindView(R.id.tv_decrease)
    TextView dercease_tv;
    @BindView(R.id.rl_week)
    RelativeLayout week_rl;
    @BindView(R.id.rl_decrease)
    RelativeLayout decrease_rl;

    private int mLateGrade;     //迟到扣的分数
    private int mTruancyGrade;  //旷课扣的分数
    private int setWeek;        //设置的该周周数

    private AlertDialog mWeekDialog;
    private AlertDialog mDecreaseDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
        mToolbar.setTitle("设置");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        initWeek();
        initDecrease();
        initWeekClick();
        initDecreaseClick();
    }

    private void initDecrease() {
        //默认迟到扣0分
        mLateGrade = AppSharedPreferences.getInt(ConstantValues.LATEGRADE,0);
        //默认旷课扣0分
        mTruancyGrade = AppSharedPreferences.getInt(ConstantValues.TRUANCYGRADE,0);
        dercease_tv.setText("迟到扣"+ mLateGrade +"分"+"|"+"旷课扣"+ mTruancyGrade +"分");
    }

    private void initWeek() {
        int week=AppSharedPreferences.getInt(ConstantValues.SETWEEK,0);
        if (week>0){
            SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
            String todayDate=sdf.format(new Date());
            String result=TableTimer.daysOfTwo(todayDate);
            week_tv.setText("第"+result.substring(result.indexOf("第")+1,result.indexOf("周"))+"周");
        }
    }

    private void initWeekClick() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_week, null);
        final NumberPicker weekPicker= (NumberPicker) view.findViewById(R.id.np_week);
        weekPicker.setMaxValue(20);
        weekPicker.setMinValue(0);
        weekPicker.setValue(0);
        weekPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setWeek=newVal;
            }
        });

        mWeekDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                        String setDate=sdf.format(new Date());                   //得到今日期
                        Calendar aCalendar = Calendar.getInstance();
                        int weekday = aCalendar.get(Calendar.DAY_OF_WEEK) - 1;   //得出今天周几

                        AppSharedPreferences.putStirng(ConstantValues.SETDATE,setDate);
                        AppSharedPreferences.putInt(ConstantValues.SETWEEK,setWeek);
                        AppSharedPreferences.putInt(ConstantValues.WEEKDAY,weekday);

                        initWeek();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDecreaseDialog.dismiss();
                    }
                }).create();

        week_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeekDialog.show();
            }
        });

    }

    private void initDecreaseClick() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_decrease, null);

        final NumberPicker latePicker= (NumberPicker) view.findViewById(R.id.np_late);
        latePicker.setMaxValue(10);
        latePicker.setMinValue(0);
        latePicker.setValue(0);
        latePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mLateGrade=newVal;
            }
        });

        final NumberPicker truancyPicker= (NumberPicker) view.findViewById(R.id.np_truancy);
        truancyPicker.setMaxValue(10);
        truancyPicker.setMinValue(0);
        truancyPicker.setValue(0);
        truancyPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                mTruancyGrade=newVal;
            }
        });

        mDecreaseDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppSharedPreferences.putInt(ConstantValues.LATEGRADE,mLateGrade);
                        AppSharedPreferences.putInt(ConstantValues.TRUANCYGRADE,mTruancyGrade);
                        initDecrease();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDecreaseDialog.dismiss();
                    }
                }).create();

        decrease_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDecreaseDialog.show();
            }
        });

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

}
