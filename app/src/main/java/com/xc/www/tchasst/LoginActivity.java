package com.xc.www.tchasst;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xc.www.app.AppApplication;
import com.xc.www.bean.User;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Created by Administrator on 2016/10/22.
 */
public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.et_tch_number)
    EditText mTchNumber;
    @BindView(R.id.et_pwd)
    EditText mPassward;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_ok,R.id.btn_cancel})
    public void login(Button button){
        switch (button.getId()){
            case R.id.btn_ok:
                String tch_number=mTchNumber.getText().toString().trim();
                String pwd=mPassward.getText().toString().trim();
                if (!TextUtils.isEmpty(tch_number) && !TextUtils.isEmpty(pwd)){
                    final BmobUser bu2 = new BmobUser();
                    bu2.setUsername(tch_number);
                    bu2.setPassword(pwd);
                    bu2.login(new SaveListener<BmobUser>() {

                        @Override
                        public void done(BmobUser bmobUser, BmobException e) {
                            if(e==null){
                                Intent intent=new Intent();
                                Bundle bundle=new Bundle();
                                bundle.putString("userName",bmobUser.getUsername());
                                intent.putExtras(bundle);
                                setResult(RESULT_OK,intent);
                                AppApplication appContext=AppApplication.getInstance();
                                appContext.currentUser= BmobUser.getCurrentUser(User.class);
                                finish();
                                Toast.makeText(LoginActivity.this,"登录成功",Toast.LENGTH_SHORT).show();
                                //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                                //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                            }else{
                                if (e.toString().contains("username or password incorrect")){
                                    Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }

                break;
            case R.id.btn_cancel:
                finish();
                break;
            default:
                break;
        }
    }


}
