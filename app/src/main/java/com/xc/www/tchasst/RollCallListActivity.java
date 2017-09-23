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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 课程所有点名结果列表页面
 * Created by Administrator on 2016/10/20.
 */
public class RollCallListActivity extends AppCompatActivity {

    @BindView(R.id.lv_attendance_manager)
    ListView listView;
    @BindView(R.id.tb_roll_call_manager)
    Toolbar mToolbar;

    private String path;
    private List<String> mRollCallTables;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_manager);
        initData();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        mToolbar.setTitle("每课点名表");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<String> simpleAdapter=new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,mRollCallTables);
        listView.setAdapter(simpleAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(RollCallListActivity.this,RollCallEditActivity.class);
                intent.putExtra("courseNumber",getIntent().getStringExtra("courseNumber"));
                intent.putExtra("path", path+File.separator+mRollCallTables.get(position));
                startActivity(intent);
            }
        });
    }

    private void initData() {
        mRollCallTables=new ArrayList<>();
        path = getIntent().getStringExtra("path");
        File file=new File(path);
        String[] fileNames = file.list();
        for (String f:fileNames){
            mRollCallTables.add(f);
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
