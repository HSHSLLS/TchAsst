package com.xc.www.utils;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.xc.www.bean.Student;
import com.xc.www.db.RollCallDb;
import com.xc.www.tchasst.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;

/**
 * Created by Administrator on 2016/10/19.
 */
public class UpDateMacUtil {
    private Context context;
    private String tableName;
    private Handler handler;

    private int successCount;
    private int errorCount;
    private Map<String,Integer> localCountMap;
    private int repeatCount;

    private boolean bluetoothState=false;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothBroadcastReceiver mReceiver;
    private ProgressDialog mProgressDialog;

    private Map<String,String> mDevicesMap;
    private List<String> stuNumbers;


    public UpDateMacUtil(Context context,String tableName,Handler handler) {
        this.context=context;
        this.tableName=tableName;
        this.handler=handler;
        init();
    }

    private void init() {
        mDevicesMap=new HashMap<>();
        stuNumbers=new ArrayList<>();
        localCountMap=new HashMap<>();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter!=null){
            bluetoothState= mBluetoothAdapter.isEnabled();
            if (bluetoothState==false){
                mBluetoothAdapter.enable();
            }
        }else {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        mProgressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        mProgressDialog.setIcon(R.drawable.ic_bluetooth_searching);//
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        if (tableName==null){
            mProgressDialog.setTitle("MAC录入中");
        }else mProgressDialog.setTitle("MAC更新中");

    }


    public void doDiscovery() {

        mDevicesMap.clear();
        stuNumbers.clear();
        successCount=0;
        errorCount=0;
        repeatCount=0;

        initBroadCast();

        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        mProgressDialog.show();
        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    private void initBroadCast() {
        mReceiver = new BluetoothBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mReceiver, filter);

    }

    class BluetoothBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                mDevicesMap.put(device.getName(),device.getAddress());
                stuNumbers.add(device.getName());
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //结束搜索
                if (tableName!=null){
                    updateLocalMac();
                }
                updateBmobMac();
            }
        }
    }

    private void updateLocalMac() {
        localCountMap.clear();
        RollCallDb instance = RollCallDb.getInstance(context);
        localCountMap = instance.upDateMac(tableName, mDevicesMap);

    }

    private void updateBmobMac() {
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.addQueryKeys("objectId,stu_number");
        query.addWhereContainedIn("stu_number", stuNumbers);
        query.setLimit(1000);
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> object, BmobException e) {
                if(e==null){

                    List<BmobObject> students = new ArrayList<BmobObject>();

                    Student stu;
                    for (int i=0;i<object.size();i++){
                        Student student=new Student();
                        stu=object.get(i);
                        student.setObjectId(stu.getObjectId());
                        student.setMac(mDevicesMap.get(stu.getStu_number()));
                        students.add(student);
                    }

                    new BmobBatch().updateBatch(students).doBatch(new QueryListListener<BatchResult>() {

                        @Override
                        public void done(List<BatchResult> o, BmobException e) {
                            if(e==null){
                                for(int i=0;i<o.size();i++){
                                    BatchResult result = o.get(i);
                                    BmobException ex =result.getError();
                                    if(ex==null){
                                        successCount++;
                                        Log.e("MAC录入成功","第"+i+"个数据批量更新成功："+result.getUpdatedAt());
                                    }else{
                                        String er=ex.toString();
                                        if (er.contains("duplicate value")){
                                            repeatCount++;
                                        }else errorCount++;
                                        Log.e("MAC录入错误","第"+i+"个数据批量更新失败："+ex.getMessage()+","+ex.getErrorCode());
                                    }
                                }
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                            endUpDate();
                        }
                    });

                }else{
                    Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }

    private void endUpDate() {
        Message msg=Message.obtain();
        msg.what=1;
        Bundle bundle=new Bundle();
        bundle.putInt("successCount",successCount);
        bundle.putInt("errorCount",errorCount);
        bundle.putInt("repeatCount",repeatCount);

        if (localCountMap.size()!=0){
            bundle.putInt("localSuccessCount",localCountMap.get("successCount"));
            bundle.putInt("localErrorCount",localCountMap.get("errorCount"));
            bundle.putInt("localRepeatCount",localCountMap.get("repeatCount"));
        }

        msg.setData(bundle);
        handler.sendMessage(msg);
        mProgressDialog.dismiss();

        if(mReceiver!=null){
            context.unregisterReceiver(mReceiver);
        }
    }


}
