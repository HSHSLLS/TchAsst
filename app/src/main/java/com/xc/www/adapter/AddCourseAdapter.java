package com.xc.www.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xc.www.bean.Course;
import com.xc.www.tchasst.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/8.
 */
public class AddCourseAdapter extends RecyclerView.Adapter<AddCourseAdapter.MyViewHolder> {

    private Context context;
    private List<Course> courseList;
    private String tch_name;
    public AddCourseAdapter(Context context,List<Course> courseList,String teacherName) {
        this.context=context;
        this.courseList = courseList;
        this.tch_name=teacherName;
    }

    public void changeData(List<Course> list,String teacherName){
        courseList.clear();
        courseList.addAll(list);
        tch_name=teacherName;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_add_course, null);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Course courseItem=courseList.get(position);

        holder.courseName.setText(courseItem.getName());
        holder.teacherName.setText(tch_name);
        holder.courseTime.setText(courseItem.getTime());
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_course_name)
        TextView courseName;
        @BindView(R.id.tv_tch_name)
        TextView teacherName;
        @BindView(R.id.tv_course_time)
        TextView courseTime;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
