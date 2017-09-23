package com.xc.www.tchasst;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.xc.www.adapter.RollCallDetailAdapter;
import com.xc.www.app.ConstantValues;
import com.xc.www.bean.Student;
import com.xc.www.bean.StudentCourse;
import com.xc.www.bean.StudentItem;
import com.xc.www.db.RollCallDb;
import com.xc.www.utils.DividerItemDecoration;
import com.xc.www.utils.StringJsonObject;
import com.xc.www.utils.TableTimer;
import com.xc.www.utils.TitleItemDecoration;
import com.xc.www.utils.UpDateMacUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * 点名表
 * Created by Administrator on 2016/10/11.
 */
public class RollCallDetailActivity extends AppCompatActivity {

    @BindView(R.id.rv_roll_call)
    RecyclerView mRecyclerView;
    @BindView(R.id.tb_roll_call_detail)
    Toolbar mToolbar;

    private String courseNumber;
    private String courseName;
    private RollCallDb mRollCallDb;
    private List<StudentItem> studentItems;
    private TitleItemDecoration mDecoration;
    private RollCallDetailAdapter mRollCallDetailAdapter;
    private List<String> titles;
    private boolean bluetoothState=false;
    private BluetoothAdapter mBluetoothAdapter;
    private BroadcastReceiver mReceiver;
    private Map<String,String> mDevicesMap;
    private ProgressDialog mProgressDialog;
    private AlertDialog mAlertDialog;
    private int addGrade=0;
    private int reduceGrade=0;
    private int selectedPosition;

    private File file;
    private String  path;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    studentItems = mRollCallDb.queryAll(courseNumber);
                    sortList();
                    adapterAddData();
                    break;
                case 1:
                    mRollCallDb.queryMAC(courseNumber,studentItems);
                    mRollCallDetailAdapter.notifyItemRangeChanged(0,studentItems.size());

                    Bundle data = msg.getData();
                    Toast.makeText(RollCallDetailActivity.this,
                            "云端更新："+data.getInt("successCount")+"\n"+"本地更新："+data.getInt("localSuccessCount"),
                            Toast.LENGTH_SHORT).show();

                    initBroadCast(); //执行完UpDateMacUtil里的doDiscovery()后重新注册广播
                    break;
                default:
                    break;
            }
        }
    };

    private void sortList() {
        Collections.sort(studentItems, new Comparator() {
            public int compare(Object a, Object b) {
                String one = ((StudentItem) a).getMajorGroup();
                String two = ((StudentItem) b).getMajorGroup();
                return one.compareTo(two);
            }
        });
    }

    public void adapterAddData(){
        for (StudentItem studentItem : studentItems) {
            String title = studentItem.getMajorGroup();
            titles.add(title);
            mRollCallDetailAdapter.addItem(studentItem);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollcall_detail);

        initView();
        initData();
        initBroadCast();
        initItemClick();
    }

    private void initItemClick() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_student, null);
        final TextView grade_tv= (TextView) view.findViewById(R.id.tv_grade);

        NumberPicker addPicker= (NumberPicker) view.findViewById(R.id.np_grade_add);
        addPicker.setMaxValue(10);
        addPicker.setMinValue(0);
        addPicker.setValue(0);
        addPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                addGrade=newVal;
            }
        });

        final NumberPicker reducePicker= (NumberPicker) view.findViewById(R.id.np_grade_reduce);
        reducePicker.setMaxValue(10);
        reducePicker.setMinValue(0);
        reducePicker.setValue(0);
        reducePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                reduceGrade=newVal;
            }
        });

        mAlertDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int editGrade=addGrade-reduceGrade;
                        mRollCallDb.updateGrade(courseNumber,studentItems.get(selectedPosition).getStu_number(),editGrade);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAlertDialog.dismiss();
                    }
                }).create();

        mRollCallDetailAdapter.setOnItemClickListener(new RollCallDetailAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                selectedPosition=position;
                grade_tv.setText("平时分："+mRollCallDb.queryGrade(courseNumber,studentItems.get(position).getStu_number()));
                mAlertDialog.show();
            }

            @Override
            public void onLongClick(int position) {

            }
        });

    }

    private void initBroadCast() {
        mReceiver = new innerBroadcastReceiver();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        filter=new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.registerReceiver(mReceiver,filter);
    }


    private void initData() {
        courseNumber = getIntent().getStringExtra("courseNumber");
        courseName=getIntent().getStringExtra("courseName");
        mRollCallDb = RollCallDb.getInstance(this);
        if (!mRollCallDb.isTableExist(courseNumber)) {
            BmobQuery<StudentCourse> query = new BmobQuery<StudentCourse>();
            query.addWhereEqualTo("course_number", courseNumber);
            query.setLimit(200);
            query.findObjects(new FindListener<StudentCourse>() {
                @Override
                public void done(List<StudentCourse> object, BmobException e) {
                    if (e == null) {
                        if (object.size() != 0) {
                            final List<String> stuNumberList = new ArrayList<String>();
                            for (StudentCourse studentCourse : object) {
                                stuNumberList.add(studentCourse.getStu_number());
                            }
                            BmobQuery<Student> query = new BmobQuery<Student>();
                            query.addWhereContainedIn("stu_number", stuNumberList);
                            query.setLimit(200);
                            query.findObjects(new FindListener<Student>() {
                                @Override
                                public void done(List<Student> object, BmobException e) {
                                    if (e == null) {
                                        int count=mRollCallDb.createTable(courseNumber, object);
//                                        for (Student student : object) {
//                                            StudentItem studentItem = new StudentItem();
//                                            studentItem.setId(student.getObjectId());
//                                            studentItem.setName(student.getName());
//                                            studentItem.setStu_number(student.getStu_number());
//                                            studentItem.setMac(student.getMac());
//                                            studentItem.setMajorGroup(student.getMajor() + student.getCl());
//                                            studentItems.add(studentItem);
//                                        }
                                        if (count>0){
                                            handler.sendEmptyMessage(0);
                                        }

                                    } else {
                                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RollCallDetailActivity.this, "该课程没有学生选修", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        } else
            handler.sendEmptyMessage(0);

    }

    private void initView() {
        ButterKnife.bind(this);

        mToolbar.setTitle("蓝牙点名");
        setSupportActionBar(mToolbar);
        mToolbar.setPopupTheme(R.style.PopupMenu);//自定义的PopupMenu风格，使得菜单显示在toolbar下方

        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter!=null){
            bluetoothState= mBluetoothAdapter.isEnabled();
        }else {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
        }

        mDevicesMap=new HashMap<>();
        titles=new ArrayList<>();

        studentItems = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RollCallDetailActivity.this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRollCallDetailAdapter = new RollCallDetailAdapter(RollCallDetailActivity.this, studentItems);
        mRecyclerView.setAdapter(mRollCallDetailAdapter);
        mRecyclerView.addItemDecoration(mDecoration = new TitleItemDecoration(RollCallDetailActivity.this, titles));
        //如果add两个，那么按照先后顺序，依次渲染。
        //mRv.addItemDecoration(new TitleItemDecoration2(this,mDatas));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(RollCallDetailActivity.this, DividerItemDecoration.VERTICAL_LIST));

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        mProgressDialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        mProgressDialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条
        mProgressDialog.setIcon(R.drawable.ic_bluetooth_searching);//
        // 设置提示的title的图标，默认是没有的，如果没有设置title的话只设置Icon是不会显示图标的
        mProgressDialog.setTitle("点名中");
    }

    private void doDiscovery() {
        // If we're already discovering, stop it
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        mBluetoothAdapter.startDiscovery();
    }

    public void matchMac(){
        for (StudentItem studentItem:studentItems){
            String stu_number=studentItem.getStu_number();
            if (mDevicesMap.containsKey(stu_number)){
                if (mDevicesMap.get(stu_number).equals(studentItem.getMac())){
                    studentItem.setAttendence(0);
                }else studentItem.setAttendence(2);
            }else studentItem.setAttendence(2);
        }
        mRollCallDetailAdapter.notifyItemRangeChanged(0,studentItems.size());
        mProgressDialog.dismiss();
    }

    class innerBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                mDevicesMap.put(device.getName(),device.getAddress());     //bug：首次打开蓝牙扫描时无法得到蓝牙名字
                // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //结束搜索
                matchMac();
            }else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
//                        Log.d("aaa", "STATE_OFF 手机蓝牙关闭");
                        bluetoothState=false;
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
//                        Log.d("aaa", "STATE_TURNING_OFF 手机蓝牙正在关闭");
                        break;
                    case BluetoothAdapter.STATE_ON:
//                        Log.d("aaa", "STATE_ON 手机蓝牙开启");
                        bluetoothState=true;
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
//                        Log.d("aaa", "STATE_TURNING_ON 手机蓝牙正在开启");
                        break;
                }
                invalidateOptionsMenu();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.roll_call_detail,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logToggle = menu.findItem(R.id.menu_bluetooth);
        logToggle.setIcon(bluetoothState ? R.drawable.ic_bluetooth : R.drawable.ic_bluetooth_disabled);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
//                mBluetoothAdapter.disable();
                finish();
                break;
            case R.id.menu_bluetooth:
            if (bluetoothState){
                    mBluetoothAdapter.disable();
                }else {
                    mBluetoothAdapter.enable();
                }
            bluetoothState=!bluetoothState;
            invalidateOptionsMenu();
                break;
            case R.id.roll_call:
                if (bluetoothState==true){
                    mDevicesMap.clear();
                    doDiscovery();
                    mProgressDialog.show();
                }else Toast.makeText(this,"请打开蓝牙",Toast.LENGTH_SHORT).show();
                break;
            case R.id.save_table:
                path= ConstantValues.PATH+"/点名表管理/"+courseNumber+"-"+courseName;
                file=new File(path);
                if (!file.exists()){
                    file.mkdirs();
                }
                StringJsonObject stringJsonObject=new StringJsonObject();
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
                String today = sdf.format(new Date());

                path=path+File.separator+TableTimer.daysOfTwo(today);
                boolean result = stringJsonObject.saveRollCallDetail(1,new File(path),studentItems);
                if (result){
                    mRollCallDb.saveAttendance(courseNumber,studentItems);
                }
                break;
            case R.id.update_mac:
                if (mReceiver!=null){
                    unregisterReceiver(mReceiver);    //因为UpDateMacUtil里注册的广播一样，所以先注销这个
                    mReceiver=null;
                }
                UpDateMacUtil upDateMacUtil=new UpDateMacUtil(this,courseNumber,handler);
                upDateMacUtil.doDiscovery();
                break;
            case R.id.stu_situation:
                Intent intent=new Intent(this,StudentSituationActivity.class);
                intent.putExtra("courseNumber",courseNumber);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("stu_count", studentItems.size());
        setResult(RESULT_OK, intent);
//        mBluetoothAdapter.disable();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
