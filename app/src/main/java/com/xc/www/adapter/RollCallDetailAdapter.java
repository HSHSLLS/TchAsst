package com.xc.www.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xc.www.bean.StudentItem;
import com.xc.www.tchasst.R;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/12.
 */
public class RollCallDetailAdapter extends RecyclerView.Adapter<RollCallDetailAdapter.MyViewHolder> {

    private Context context;
    private List<StudentItem> studentItems;
    private OnItemClickListener onItemClickListener;
    private Map<String,String> mChangeAttnMap;
    private String stu_number;
    private int loadItems=0; //初始化spinner时会调用onItemSelected方法，当selecTimes大于studentItems大小时，说明加载完列表

    public RollCallDetailAdapter(Context context, List<StudentItem> studentItems) {
        this.context = context;
        this.studentItems = studentItems;
//        if (studentItems.size()!=0){
//            sortList();
//        }
    }

    private void sortList() {
        Collections.sort(studentItems, new Comparator() {
            public int compare(Object a, Object b) {
                String one = ((StudentItem) a).getMajorGroup();
                String two = ((StudentItem) b).getMajorGroup();
                return one.compareTo(two);
            }
        });
    }

    public void addItem(StudentItem studentItem){
        studentItems.add(studentItems.size(),studentItem);
        notifyItemInserted(studentItems.size());
    }

    public void setChangeAttendance(Map<String,String> changeAttnMap){
        this.mChangeAttnMap=changeAttnMap;
    }

    //viewType:前者与后者的studentItem.getMajorGroup()是否相同分类
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.item_student, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        StudentItem studentItem=studentItems.get(position);
        holder.stu_name.setText(studentItem.getName());
        holder.stu_number.setText("学号："+studentItem.getStu_number());
        holder.stu_mac.setText("MAC："+studentItem.getMac());
        switch (studentItem.getAttendence()){
            case 0:
                holder.spinner.setSelection(0);
                break;
            case 1:
                holder.spinner.setSelection(1);
                break;
            case 2:
                holder.spinner.setSelection(2);
                break;
            case 3:
                holder.spinner.setSelection(3);
                break;
            default:
                break;
        }
        final int pos=position;
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mChangeAttnMap!=null){
                    stu_number=studentItems.get(pos).getStu_number();
                    if (mChangeAttnMap.containsKey(stu_number)){
                        mChangeAttnMap.put(stu_number,mChangeAttnMap.get(stu_number)+String.valueOf(position));
                    }else mChangeAttnMap.put(stu_number,
                            String.valueOf(studentItems.get(pos).getAttendence()));
                }
                studentItems.get(pos).setAttendence(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        @BindView(R.id.tv_stu_mac)
        TextView stu_mac;
        @BindView(R.id.spinner)
        Spinner spinner;
        public MyViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener!=null){
                        onItemClickListener.onClick(getLayoutPosition());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onItemClickListener!=null){
                        onItemClickListener.onLongClick(getLayoutPosition());
                    }
                    return true;
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
        void onLongClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

}
