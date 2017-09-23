package com.xc.www.tchasst;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.xc.www.adapter.RollCallDetailAdapter;
import com.xc.www.bean.StudentItem;
import com.xc.www.db.RollCallDb;
import com.xc.www.utils.DividerItemDecoration;
import com.xc.www.utils.StringJsonObject;
import com.xc.www.utils.TitleItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 点名表结果编辑页面
 * Created by Administrator on 2016/10/20.
 */
public class RollCallEditActivity extends AppCompatActivity {

    @BindView(R.id.tb_rcm)
    Toolbar mToolbar;
    @BindView(R.id.rv_rcm)
    RecyclerView mRecyclerView;

    private String path;
    private String courseNumber;
    private List<StudentItem> studentItems;
    private RollCallDetailAdapter mRollCallDetailAdapter;
    private List<String> titles;
    private TitleItemDecoration mDecoration;

    private Map<String,String> mChangeAttnMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollcall_edit);
        initData();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        mToolbar.setTitle("点名表");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RollCallEditActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRollCallDetailAdapter = new RollCallDetailAdapter(RollCallEditActivity.this, studentItems);
        mRecyclerView.setAdapter(mRollCallDetailAdapter);
        mRecyclerView.addItemDecoration(mDecoration = new TitleItemDecoration(RollCallEditActivity.this, titles));
        //如果add两个，那么按照先后顺序，依次渲染。
        //mRv.addItemDecoration(new TitleItemDecoration2(this,mDatas));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(RollCallEditActivity.this, DividerItemDecoration.VERTICAL_LIST));

        mRollCallDetailAdapter.setChangeAttendance(mChangeAttnMap);
    }

    private void initData() {
        titles=new ArrayList<>();
        studentItems = new ArrayList<>();
        mChangeAttnMap=new HashMap<>();

        path=getIntent().getStringExtra("path");
        courseNumber=getIntent().getStringExtra("courseNumber");
        studentItems = StringJsonObject.readRollCallDetail(path);
        sortList();
    }

    private void sortList() {
        Collections.sort(studentItems, new Comparator() {
            public int compare(Object a, Object b) {
                String one = ((StudentItem) a).getMajorGroup();
                String two = ((StudentItem) b).getMajorGroup();
                return one.compareTo(two);
            }
        });

        for (StudentItem studentItem : studentItems) {
            String title = studentItem.getMajorGroup();
            titles.add(title);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.roll_call_edit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.save:
                RollCallDb rollCallDb=RollCallDb.getInstance(RollCallEditActivity.this);
                rollCallDb.upDateAttendance(courseNumber,mChangeAttnMap);
                StringJsonObject.saveRollCallDetail( 2,new File(path),studentItems);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
