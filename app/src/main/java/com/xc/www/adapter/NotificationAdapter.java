package com.xc.www.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xc.www.bean.NotificationItem;
import com.xc.www.db.NotificationDb;
import com.xc.www.tchasst.R;

import java.util.List;

/**
 * Created by Administrator on 2016/12/12.
 */
public class NotificationAdapter extends BaseAdapter {

    private Context context;
    private List<NotificationItem> notificationList;
    NotificationDb mNotificationDb=NotificationDb.getInstance(context);

    public NotificationAdapter(Context context, List<NotificationItem> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    public void updateData(){
        notificationList.clear();
        notificationList.addAll(mNotificationDb.queryAll());
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return notificationList.size();
    }

    @Override
    public Object getItem(int position) {
        return notificationList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView=LayoutInflater.from(context).inflate(R.layout.item_notification,null);
            viewHolder.receive_time_tv= (TextView) convertView.findViewById(R.id.tv_time);
            viewHolder.content_tv= (TextView) convertView.findViewById(R.id.tv_message);
            viewHolder.delete_img= (ImageView) convertView.findViewById(R.id.img_delete);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        viewHolder.receive_time_tv.setText(notificationList.get(position).getReceive_time());
        viewHolder.content_tv.setText(notificationList.get(position).getContent());
        viewHolder.delete_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNotificationDb.deleteNotification(notificationList.get(position).getId());
                notificationList.remove(position);
                notifyDataSetChanged();
            }
        });

        return convertView;
    }

    public static class ViewHolder{
        public TextView receive_time_tv;
        public TextView content_tv;
        public ImageView delete_img;
    }
}
