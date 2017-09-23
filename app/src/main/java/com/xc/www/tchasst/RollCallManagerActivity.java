package com.xc.www.tchasst;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.xc.www.app.ConstantValues;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 有点名记录的课程列表
 * Created by Administrator on 2016/10/19.
 */
public class RollCallManagerActivity extends AppCompatActivity {

    @BindView(R.id.lv_attendance_manager)
    ListView listView;
    @BindView(R.id.tb_roll_call_manager)
    Toolbar mToolbar;

    private List<String> mCourses;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_manager);
        initData();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        mToolbar.setTitle("课程");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<String> simpleAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,mCourses);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(RollCallManagerActivity.this,RollCallListActivity.class);
                String courseNumber=mCourses.get(position).substring(0,mCourses.get(position).indexOf("-"));
                intent.putExtra("courseNumber",courseNumber);
                intent.putExtra("path",ConstantValues.PATH+"/点名表管理/"+mCourses.get(position));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mCourses = new ArrayList<>();
        File file=new File(ConstantValues.PATH+"/点名表管理/");
        String[] fileNames = file.list();
        for (String f:fileNames){
            mCourses.add(f);
        }
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
