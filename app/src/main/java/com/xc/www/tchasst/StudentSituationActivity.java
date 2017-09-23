package com.xc.www.tchasst;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.xc.www.adapter.StuSituationAdapter;
import com.xc.www.bean.StudentItem;
import com.xc.www.db.RollCallDb;
import com.xc.www.utils.DividerItemDecoration;
import com.xc.www.utils.TitleItemDecoration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *学生总况页面
 * Created by Administrator on 2016/10/22.
 */
public class StudentSituationActivity extends AppCompatActivity {

    @BindView(R.id.tb_stu_situation)
    Toolbar mToolbar;
    @BindView(R.id.rv_stu_situation)
    RecyclerView mRecyclerView;

    private List<StudentItem> studentItems;
    private StuSituationAdapter mStuSituationAdapter;
    private List<String> titles;
    private TitleItemDecoration mDecoration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_situation);
        initData();
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);

        mToolbar.setTitle("学生总况");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StudentSituationActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mStuSituationAdapter = new StuSituationAdapter(StudentSituationActivity.this, studentItems);
        mRecyclerView.setAdapter(mStuSituationAdapter);
        mRecyclerView.addItemDecoration(mDecoration = new TitleItemDecoration(StudentSituationActivity.this, titles));
        //如果add两个，那么按照先后顺序，依次渲染。
        //mRv.addItemDecoration(new TitleItemDecoration2(this,mDatas));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(StudentSituationActivity.this, DividerItemDecoration.VERTICAL_LIST));

    }

    private void initData() {
        titles=new ArrayList<>();
        studentItems=new ArrayList<>();

        String courseNumber=getIntent().getStringExtra("courseNumber");
        studentItems= RollCallDb.getInstance(this).getStudentSituation(courseNumber);
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
