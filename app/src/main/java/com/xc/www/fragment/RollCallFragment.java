package com.xc.www.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xc.www.adapter.CourseRvAdapter;
import com.xc.www.bean.CourseItem;
import com.xc.www.db.RollCallDb;
import com.xc.www.tchasst.AddCourseActivity;
import com.xc.www.tchasst.R;
import com.xc.www.tchasst.RollCallDetailActivity;
import com.xc.www.utils.StringJsonObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/6.
 */
public class RollCallFragment extends Fragment {

    @BindView(R.id.rv_course)
    RecyclerView mRecyclerView;
    @BindView(R.id.cv_default)
    CardView mCardViewDefault;

    private List<CourseItem> localCourseList;
    private ArrayList<String> courseNumberList;
    private CourseRvAdapter mCourseRvAdapter;
    private int selectedPosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_roll_call,null);
        ButterKnife.bind(this,view);
        initView();
        initListener();
        return view;
    }

    private void initListener() {
        mCourseRvAdapter.setOnItemClickListener(new CourseRvAdapter.OnItemClickListener() {
            @Override
            public void enter(String courseNumber,String courseName,int position) {
                Intent intent=new Intent(getActivity(), RollCallDetailActivity.class);
                intent.putExtra("courseName",courseName);
                intent.putExtra("courseNumber",courseNumber);
                startActivityForResult(intent,2);
                selectedPosition=position;
            }

            @Override
            public void deleteItem(int position) {
                delete(position);
            }
        });
    }

    public void delete(final int position){
        final AlertDialog alertDialog=new AlertDialog.Builder(getActivity())
                .setTitle("删除")
                .setMessage("确定删除该课程学生信息")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String course_number = localCourseList.get(position).getCourse_number();
                        RollCallDb mRollCallDb = RollCallDb.getInstance(getActivity());
                        if (mRollCallDb.isTableExist(course_number)){
                            mRollCallDb.deleteTable(course_number);
                        }
                        localCourseList.remove(position);
                        courseNumberList.remove(position);
                        mCourseRvAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        alertDialog.show();
    }

    private void initView() {
        localCourseList=new ArrayList<>();
        localCourseList= StringJsonObject.getObjectListFromJson();
        mCourseRvAdapter = new CourseRvAdapter(getContext(),localCourseList);
        RecyclerView.LayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mCourseRvAdapter);
        setDefaultCardVisiable();

        courseNumberList=new ArrayList<>();
        for (CourseItem courseItem:localCourseList){
            courseNumberList.add(courseItem.getCourse_number());
        }
    }

    private void setDefaultCardVisiable() {
        if (localCourseList.size()==0){
            mCardViewDefault.setVisibility(View.VISIBLE);
        }else
            mCardViewDefault.setVisibility(View.GONE);
    }

    public void fabClick(FloatingActionButton button){
        Intent intent=new Intent(getActivity(),AddCourseActivity.class);
        Bundle bundle=new Bundle();
        bundle.putStringArrayList("courseNumberList",courseNumberList);
        intent.putExtras(bundle);
        startActivityForResult(intent,1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1: //添加课程后返回
                if (resultCode== Activity.RESULT_OK){
                    addCourse((List<CourseItem>) data.getExtras().get("courseList"));
                }
                break;
            case 2:  //点名后返回
                if (resultCode== Activity.RESULT_OK) {
                    int stu_count = data.getIntExtra("stu_count", -1);
                    if (stu_count != -1) {
                        localCourseList.get(selectedPosition).setStu_count(stu_count);
                        mCourseRvAdapter.notifyItemChanged(selectedPosition);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void addCourse(List<CourseItem> courseItems){
//        mCourseRvAdapter.addCourse(courseItems);
        localCourseList.addAll(courseItems);
        mCourseRvAdapter.notifyDataSetChanged();
        setDefaultCardVisiable();
        for (CourseItem courseItem:courseItems){
            courseNumberList.add(courseItem.getCourse_number());
        }
    }

    @Override
    public void onDestroyView() {
//        LocalCourse loclaCourse=new LocalCourse();
//        loclaCourse.setCourseItems(localCourseList);
//        StringJsonObject.setJsonFromObjectList(loclaCourse);
        StringJsonObject.setJsonFromObjectList(localCourseList);
        super.onDestroyView();
    }

}
