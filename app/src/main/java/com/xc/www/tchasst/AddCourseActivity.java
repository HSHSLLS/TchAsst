package com.xc.www.tchasst;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.xc.www.adapter.AddCourseAdapter2;
import com.xc.www.app.AppApplication;
import com.xc.www.bean.CourseItem;
import com.xc.www.bean.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/8.
 */
public class AddCourseActivity extends AppCompatActivity {

//    private List<Course> courseList;
    private List<CourseItem> courseAddList;
    private String tch_name="";
    private String tch_number="";
    private AddCourseAdapter2 mAdapter;
    private ArrayList<String> courseNumberList;

    @BindView(R.id.tb_add_course)
    Toolbar mToolbar;
    @BindView(R.id.rv_course)
    RecyclerView mRecyclerView;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
//                    mAdapter.changeData(courseList,tch_name);
//                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);
        initData();
        init();
    }

    public void initData() {
        courseNumberList= getIntent().getExtras().getStringArrayList("courseNumberList");
    }

    private void init() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab=getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        ab.setDisplayHomeAsUpEnabled(true);

        RecyclerView.LayoutManager linearLayoutManager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
//        courseList=new ArrayList<Course>();
//        mAdapter=new AddCourseAdapter(this,courseList,tch_name);
        mAdapter=new AddCourseAdapter2(this);
        mRecyclerView.setAdapter(mAdapter);

        courseAddList=new ArrayList<>();
        mAdapter.setOnItemClickListener(new AddCourseAdapter2.OnItemClickListener() {
            @Override
            public void add(View view, CourseItem courseItem) {
                courseAddList.add(courseItem);
                ImageView img= (ImageView) view;
                img.setImageResource(R.drawable.ic_done_black);
            }
        });

        User currentUser= AppApplication.getInstance().currentUser;
        if (currentUser!=null){
            tch_number=currentUser.getUsername();
//            getAddCourseList();
            mAdapter.getAddCourseList(tch_number,courseNumberList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.add_course,menu);
        MenuItem menuItem=menu.findItem(R.id.search_course);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //点击搜索时调用
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!TextUtils.isEmpty(query)){
                    tch_number=query;
//                    getAddCourseList();
                    mAdapter.getAddCourseList(query,courseNumberList);
                }
                return true;
            }

            //输入搜索关键字时调用
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setSubmitButtonEnabled(true);//设置是否显示搜索按钮

        SpannableString spanText = new SpannableString("教职工号");
        // 设置字体大小
        spanText.setSpan(new AbsoluteSizeSpan(16, true), 0, spanText.length(),
                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 设置字体颜色
        spanText.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0,
                spanText.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        searchView.setQueryHint(spanText);//设置提示信息

        searchView.setIconifiedByDefault(false);//设置搜索默认为展开

        return true;
    }

    /*public void getAddCourseList(){
        BmobQuery<Course> query = new BmobQuery<Course>();
//查询playerName叫“比目”的数据
        query.addWhereEqualTo("tch_number", tch_number);
        query.addQueryKeys("course_number,name,time");
//返回50条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(20);
//执行查询方法
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> object, BmobException e) {
                if(e==null){
                    courseList=object;
                    getTeacherName();
                }else{
                    Toast.makeText(AppApplication.getInstance(),"查询失败",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getTeacherName(){
        BmobQuery<User> bmobQuery = new BmobQuery<User>();
        bmobQuery.addWhereEqualTo("tch_number", tch_number);
        bmobQuery.addQueryKeys("username");
        bmobQuery.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> object, BmobException e) {
                if(e==null){
                    tch_name=object.get(0).getUsername();
                    Message message=Message.obtain();
                    handler.sendEmptyMessage(0);
                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                convertData();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        convertData();
        super.onBackPressed();
    }

    public void convertData(){
        Intent intent=new Intent();
        Bundle bundle=new Bundle();
        bundle.putSerializable("courseList", (Serializable) courseAddList);
        intent.putExtras(bundle);
        setResult(RESULT_OK,intent);
    }
}
