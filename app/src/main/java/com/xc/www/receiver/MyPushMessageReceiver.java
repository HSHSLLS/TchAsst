package com.xc.www.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xc.www.app.AppSharedPreferences;
import com.xc.www.app.ConstantValues;
import com.xc.www.db.NotificationDb;
import com.xc.www.tchasst.MainActivity;
import com.xc.www.tchasst.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cn.bmob.push.PushConstants;

/**
 * 自定义的广播接收者，接收推送通知
 * Created by Administrator on 2016/11/11.
 */
public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            String json=intent.getStringExtra("msg");
            Log.e("通知", json );
            try {
                JSONObject jsonObject=new JSONObject(json);
                JSONObject aps = jsonObject.getJSONObject("aps");
                String title=aps.getString("title");
                String content=aps.getString("content");

                NotificationDb.getInstance(context).insertNotification(title,content);
                AppSharedPreferences.putBoolean(ConstantValues.NOTIFICATION,true);
                Intent NotificationIntent=new Intent("com.xc.www.NOTIFICATION");
                context.sendBroadcast(NotificationIntent);

                int temp=MainActivity.getViewPagerCurrentItem();
                if (temp!=2){
                    NotificationManager notificationManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    //点击通知后打开APP意图
                    Intent clickIntent = new Intent(context, MainActivity.class);
                    clickIntent.setAction("com.xc.www.NOTIFICATION");
                    PendingIntent pi = PendingIntent.getActivity(context, 0, clickIntent,PendingIntent.FLAG_CANCEL_CURRENT);

                    Notification notification= null;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        notification = new Notification.Builder(context)
                                .setSmallIcon(R.drawable.notification_ta)
                                .setTicker("教学通知")
                                .setContentTitle(title)
                                .setContentText(content)
                                .setContentIntent(pi)
                                .build();
                        notification.flags=Notification.FLAG_AUTO_CANCEL;  //作用：点击通知时，通知自动消失
                        long[] vibrates = {0, 500, 0, 0};  //数组元素：手机静止的时长（距通知到达）、手机振动的时长、手机静止的时长（震动间隔）
                        notification.vibrate = vibrates;

                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY); // 时
                        int minutes = calendar.get(Calendar.MINUTE);    // 分
                        int seconds = calendar.get(Calendar.SECOND);    // 秒
                        String receive_time=String.valueOf(hours)+String.valueOf(minutes)+String.valueOf(seconds);

                        String notificationIds = AppSharedPreferences.getString(ConstantValues.NOTIFICATION_ID, "");
                        notificationIds=notificationIds+","+receive_time;
                        AppSharedPreferences.putStirng(ConstantValues.NOTIFICATION_ID,notificationIds);

                        notificationManager.notify(Integer.parseInt(receive_time),notification);
                    }
                }else {
                    AppSharedPreferences.putBoolean(ConstantValues.NOTIFICATION,false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
