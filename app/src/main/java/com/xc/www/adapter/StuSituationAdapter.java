package com.xc.www.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xc.www.bean.StudentItem;
import com.xc.www.tchasst.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/12.
 */
public class StuSituationAdapter extends RecyclerView.Adapter<StuSituationAdapter.MyViewHolder> {

    private Context context;
    private List<StudentItem> studentItems;

    public StuSituationAdapter(Context context, List<StudentItem> studentItems) {
        this.context = context;
        this.studentItems = studentItems;
    }

    public void addItem(StudentItem studentItem){
        studentItems.add(studentItems.size(),studentItem);
        notifyItemInserted(studentItems.size());
    }

    //viewType:前者与后者的studentItem.getMajorGroup()是否相同分类
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_stu_situation, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentItem studentItem=studentItems.get(position);
        holder.stu_name.setText(studentItem.getName());
        holder.stu_number.setText("学号："+studentItem.getStu_number());
        holder.stu_grade.setText("平时分："+studentItem.getGrade());
        holder.late_times.setText("迟到："+studentItem.getLate_times()+"次");
        holder.truancy_times.setText("旷课："+studentItem.getTruancy_times()+"次");
        holder.leave_times.setText("请假："+studentItem.getLeave_times()+"次");
    }

    @Override
    public int getItemCount() {
        return studentItems.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_stu_name)
        TextView stu_name;
        @BindView(R.id.tv_stu_number)
        TextView stu_number;
        @BindView(R.id.tv_stu_grade)
        TextView stu_grade;
        @BindView(R.id.tv_late_times)
        TextView late_times;
        @BindView(R.id.tv_truancy_times)
        TextView truancy_times;
        @BindView(R.id.tv_leave_times)
        TextView leave_times;


        public MyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

}
