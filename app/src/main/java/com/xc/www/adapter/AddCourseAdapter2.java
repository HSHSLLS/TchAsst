package com.xc.www.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xc.www.app.AppApplication;
import com.xc.www.bean.Course;
import com.xc.www.bean.CourseItem;
import com.xc.www.tchasst.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by Administrator on 2016/10/9.
 */
public class AddCourseAdapter2 extends RecyclerView.Adapter<AddCourseAdapter2.MyViewHolder> {

    private Context context;
    private List<CourseItem> courseList;
    private OnItemClickListener onItemClickListener;

    public AddCourseAdapter2(Context context) {
        this.context=context;
        courseList=new ArrayList<>();
    }

    public void getAddCourseList(String tch_number,ArrayList<String> courseNumberList){
        BmobQuery<Course> query = new BmobQuery<Course>();
        query.addWhereEqualTo("tch_number", tch_number);
        query.addWhereNotContainedIn("course_number", courseNumberList);
        query.addQueryKeys("course_number,name,time,location,stu_count,user");
        query.include("user");
        query.setLimit(20);
        query.findObjects(new FindListener<Course>() {
            @Override
            public void done(List<Course> object, BmobException e) {
                if(e==null){
                    courseList.clear();
                    for (Course course:object){
                        CourseItem courseItem=new CourseItem();
                        courseItem.setCourse_number(course.getCourse_number());
                        courseItem.setName(course.getName());
                        courseItem.setLocation(course.getLocation());
                        courseItem.setTime(course.getTime());
                        courseItem.setStu_count(course.getStu_count());
                        courseItem.setTeacher_name(course.getUser().getTch_name());
                        courseList.add(courseItem);
                    }
                    notifyDataSetChanged();
                }else{
                    Log.e("错误", e.toString() );
                    Toast.makeText(AppApplication.getInstance(),"查询失败"+e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_add_course, null);
        MyViewHolder myViewHolder=new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final CourseItem courseItem=courseList.get(position);
        holder.courseName.setText(courseItem.getName());
        holder.teacherName.setText("教师："+courseItem.getTeacher_name());
        holder.courseTime.setText("时间："+courseItem.getTime());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener!=null){
                    int poi=holder.getLayoutPosition();
                    onItemClickListener.add(v,courseItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public interface OnItemClickListener{
        void add(View view,CourseItem courseItem);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_course_name)
        TextView courseName;
        @BindView(R.id.tv_tch_name)
        TextView teacherName;
        @BindView(R.id.tv_course_time)
        TextView courseTime;
        @BindView(R.id.iv_add)
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
