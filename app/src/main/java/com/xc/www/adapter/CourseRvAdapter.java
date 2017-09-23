package com.xc.www.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xc.www.bean.CourseItem;
import com.xc.www.tchasst.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/6.
 */
public class CourseRvAdapter extends RecyclerView.Adapter<CourseRvAdapter.MyViewHoler> {

    private Context context;
    private List<CourseItem> courseItems=new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    public CourseRvAdapter(Context context,List<CourseItem> courseItems) {
        this.context=context;
        this.courseItems=courseItems;
    }

    public void addCourse(List<CourseItem> courseItem){
        courseItems.addAll(courseItem);
    }

    @Override
    public MyViewHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater mInflater=LayoutInflater.from(context);
        View view=mInflater.inflate(R.layout.item_card_course,parent,false);
        MyViewHoler myViewHoler=new MyViewHoler(view);
        return myViewHoler;
    }

    @Override
    public void onBindViewHolder(MyViewHoler holder, int position) {
        CourseItem courseItem=courseItems.get(position);
        /*holder.tvs.get(0).setText(courseItem.getName());
        holder.tvs.get(1).setText(courseItem.getTime());
        holder.tvs.get(2).setText(courseItem.getLocation());
        holder.tvs.get(3).setText(courseItem.getStu_count());*/
        holder.courseName.setText(courseItem.getName());
        holder.courseTime.setText("时间："+courseItem.getTime());
        holder.courseLocation.setText("地点："+courseItem.getLocation());
        holder.stuCount.setText("学生人数："+courseItem.getStu_count().toString());

        holder.itemView.setTag(R.id.tag_first,courseItem.getCourse_number());
        holder.itemView.setTag(R.id.tag_second,courseItem.getName());
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }

    class MyViewHoler extends RecyclerView.ViewHolder{
//        @BindViews({R.id.tv_course_name,R.id.tv_course_time,R.id.tv_course_location,R.id.tv_stu_count})
//        List<TextView> tvs;

        @BindView(R.id.tv_course_name)
        TextView courseName;
        @BindView(R.id.tv_course_time)
        TextView courseTime;
        @BindView(R.id.tv_course_location)
        TextView courseLocation;
        @BindView(R.id.tv_student_count)
        TextView stuCount;

        public MyViewHoler(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null){
                        onItemClickListener.enter((String) v.getTag(R.id.tag_first),(String) v.getTag(R.id.tag_second), getAdapterPosition());
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener!=null){
                        onItemClickListener.deleteItem(getAdapterPosition());
                    }
                    return true;
                }
            });

        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }


    public interface OnItemClickListener{
        void enter(String courseNumber,String courseName,int position);
        void deleteItem(int position);
    }

}
