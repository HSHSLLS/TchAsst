package com.xc.www.fragment;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xc.www.bean.Note;
import com.xc.www.db.NoteDb;
import com.xc.www.tchasst.NoteDetailActivity;
import com.xc.www.tchasst.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/11.
 */
public class NoteFragment extends Fragment {

    private int visible=View.GONE;

    @BindView(R.id.lv_note)
    ListView listView;

    private NoteDb mNoteDb;
    private List<Note> noteList;
    private NoteAdapter mNoteAdapter;
    private List<Note> deleteNoteList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);   //使得Fragment中的onCreateOptionsMenu生效
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_note,null);
        ButterKnife.bind(this,view);
        initData();
        initView();

        return view;
    }

    private void initView() {

        mNoteAdapter = new NoteAdapter();
        listView.setAdapter(mNoteAdapter);
    }

    private void initData() {
        mNoteDb = NoteDb.getInstance(getActivity());
        noteList = mNoteDb.queryAll();

        deleteNoteList=new ArrayList<>();
    }

    class NoteAdapter extends BaseAdapter {

        Note noteClick;
        Note note;

        @Override
        public int getCount() {
            return noteList.size();
        }

        @Override
        public Object getItem(int position) {
            return noteList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            final ViewHolder viewHolder;
            if (convertView == null) {
                view = LayoutInflater.from(getActivity()).inflate(R.layout.list_note_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title_tv = (TextView) view.findViewById(R.id.tv_title);
                viewHolder.write_time_tv = (TextView) view.findViewById(R.id.tv_write_time);
                viewHolder.select_iv = (ImageView) view.findViewById(R.id.iv_select);
                view.setTag(viewHolder);
            } else {
                view = convertView;
                viewHolder = (ViewHolder) view.getTag();
            }

            note=noteList.get(position);
            viewHolder.title_tv.setText(note.getTitle());
            viewHolder.write_time_tv.setText("创建时间："+note.getWrite_time());

            viewHolder.select_iv.setVisibility(visible);
            if (note.isSelect()){
                viewHolder.select_iv.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
            }else {
                viewHolder.select_iv.setImageResource(R.drawable.ic_radio_button_unchecked);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("note",noteList.get(position));
                    intent.putExtras(bundle);
                    intent.putExtra("opentype",1);
                    startActivityForResult(intent,1);
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    visible=View.VISIBLE;
                    notifyDataSetChanged();
                    return false;
                }
            });

            viewHolder.select_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noteClick=noteList.get(position);
                    boolean ischeck=note.isSelect();
                    if (ischeck){
                        deleteNoteList.remove(noteClick);
                        noteList.get(position).setSelect(false);
                        viewHolder.select_iv.setImageResource(R.drawable.ic_radio_button_unchecked);
                    }else {
                        deleteNoteList.add(noteClick);
                        noteList.get(position).setSelect(true);
                        viewHolder.select_iv.setImageResource(R.drawable.ic_radio_button_checked_black_24dp);
                    }
                }
            });

            return view;
        }
    }

    public static class ViewHolder {
        public TextView title_tv;
        public TextView write_time_tv;
        public ImageView select_iv;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
            case 2:
                if (resultCode== Activity.RESULT_OK){
                    noteList.clear();
                    noteList=mNoteDb.queryAll();
                    mNoteAdapter.notifyDataSetChanged();
                    listView.setSelection(mNoteAdapter.getCount());
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                if (deleteNoteList.size()!=0){
                    AlertDialog ad=new AlertDialog.Builder(getActivity())
                            .setTitle("删除")
                            .setMessage("是否删除这"+deleteNoteList.size()+"条备忘")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    visible=View.GONE;
                                    mNoteDb.deleteNote(deleteNoteList);
                                    for (Note note:deleteNoteList){
                                        noteList.remove(note);
                                    }
                                    deleteNoteList.clear();
                                    mNoteAdapter.notifyDataSetChanged();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    visible=View.GONE;
                                    recoverNotes();
                                    deleteNoteList.clear();
                                    mNoteAdapter.notifyDataSetChanged();
                                }
                            })
                            .create();
                    ad.show();
                }
                break;
            case R.id.select_all:
                visible=View.VISIBLE;
                for (Note note:noteList){
                    if (!deleteNoteList.contains(note)){
                        deleteNoteList.add(note);
                        note.setSelect(true);
                    }
                }
                mNoteAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void recoverNotes(){
        for (Note note:deleteNoteList){
            note.setSelect(false);
        }
    }

    public void fabClick(FloatingActionButton button){
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.putExtra("opentype",2);
        startActivityForResult(intent,2);
    }

    //按下返回键时的操作
    public boolean onBackPressedFragment(){
        if (visible==View.VISIBLE){
            visible=View.GONE;
            recoverNotes();
            deleteNoteList.clear();
            mNoteAdapter.notifyDataSetChanged();
            return true;
        }else
            return false;
    }

}
