package com.xc.www.tchasst;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.xc.www.bean.Note;
import com.xc.www.db.NoteDb;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 备忘录添加或编辑页面
 * Created by Administrator on 2016/10/30.
 */
public class NoteDetailActivity extends AppCompatActivity {

    private static final int ADDNOTE = 2;
    private static final int EDITNOTE = 1;

    private Toolbar mToolbar;
    private EditText title_et;
    private EditText content_et;

    private Note mNote;
    private int openType;
    private NoteDb mNoteDb;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        initView();
        initData();
    }

    private void initData() {
        mNoteDb = NoteDb.getInstance(this);
    }

    private void initView() {
        title_et= (EditText) findViewById(R.id.tv_title_detail);
        content_et= (EditText) findViewById(R.id.et_content_detail);

        mToolbar= (Toolbar) findViewById(R.id.tb_detail);
        mToolbar.setTitle("Edit");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        mNote=new Note();
        openType = getIntent().getIntExtra("opentype",0);
        switch (openType){
            case 1:
                mNote = (Note) getIntent().getExtras().getSerializable("note");
                title_et.setText(mNote.getTitle());
                content_et.setText(mNote.getContent());
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.detail_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.detail_save:
                mNote.setTitle(title_et.getText().toString());
                mNote.setContent(content_et.getText().toString());
                if (openType==EDITNOTE){
                    mNoteDb.upDateNote(mNote);
                }else if (openType==ADDNOTE){
                    String writeTime=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    mNote.setWrite_time(writeTime);
                    mNoteDb.insertNote(mNote);
                }
                setResult(RESULT_OK);
                finish();
                break;
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
