package com.xc.www.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xc.www.adapter.NotificationAdapter;
import com.xc.www.bean.NotificationItem;
import com.xc.www.db.NotificationDb;
import com.xc.www.tchasst.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/12/11.
 */
public class NotificationFragment extends Fragment {

    @BindView(R.id.rv_message)
    ListView mListView;

    private List<NotificationItem> notificationList;
    private NotificationAdapter mAdapter;
    InnerBroadCastReceiver mBroadCastReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_notificaiton,null);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
        initView();
    }

    private void initView() {
        mAdapter=new NotificationAdapter(getActivity(),notificationList);
        mListView.setAdapter(mAdapter);
        mListView.setSelection(mAdapter.getCount()-1);
    }

    private void initData() {
        getData();
        mBroadCastReceiver=new InnerBroadCastReceiver();
        IntentFilter intentFilter=new IntentFilter("com.xc.www.NOTIFICATION");
        getActivity().registerReceiver(mBroadCastReceiver,intentFilter);
    }

    private void getData(){
        notificationList= NotificationDb.getInstance(getActivity()).queryAll();
    }

    public class InnerBroadCastReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mAdapter.updateData();
            mListView.smoothScrollToPosition(mAdapter.getCount()-1);
        }
    }

    @Override
    public void onDestroy() {
        if (mBroadCastReceiver!=null){
            getActivity().unregisterReceiver(mBroadCastReceiver);
        }
        super.onDestroy();
    }
}
