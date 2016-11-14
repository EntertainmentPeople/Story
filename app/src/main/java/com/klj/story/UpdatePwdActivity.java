package com.klj.story;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.klj.story.entity.User;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.UserUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 修改密码页面
 */
public class UpdatePwdActivity extends AppCompatActivity {
    private ImageView ivUpdatePwdBack;
    private EditText etOldPwd;
    private EditText etNewPwd;
    private EditText etRePwd;
    private Button btnUpdatePwd;
    User user;      //用户信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_pwd);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        setListener();
    }

    /**
     * 为控件设置监听
     */
    private void setListener() {
        ivUpdatePwdBack.setOnClickListener(new MyOnClickListener());
        btnUpdatePwd.setOnClickListener(new MyOnClickListener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivUpdatePwdBack = (ImageView) findViewById(R.id.iv_updatepwd_back);
        etOldPwd = (EditText) findViewById(R.id.et_updatepwd_oldpwd);
        etNewPwd = (EditText) findViewById(R.id.et_updatepwd_newpwd);
        etRePwd = (EditText) findViewById(R.id.et_updatepwd_repwd);
        btnUpdatePwd = (Button) findViewById(R.id.btn_updatepwd_updatepwd);
    }

    /**
     * 设置点击监听
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_updatepwd_back:
                    finish();
                    break;
                case R.id.btn_updatepwd_updatepwd:
                    save();
                    break;
            }
        }
    }

    /**
     * 保存修改
     */
    private void save() {
        if(isEmpty(etOldPwd)){
            showMessage("原密码不能为空，请输入原密码");
            etOldPwd.setFocusable(true);
        }else if(isEmpty(etNewPwd)){
            showMessage("新密码不能为空，请输入新密码");
            etNewPwd.setFocusable(true);
        }else if(isEmpty(etRePwd)){
            showMessage("重复密码不能为空，请输入重复密码");
            etRePwd.setFocusable(true);
        }else if(!etNewPwd.getText().toString().equals(etRePwd.getText().toString())){
            showMessage("两次密码输入不一致，请重新输入");
            etNewPwd.setText("");
            etRePwd.setText("");
            etNewPwd.setFocusable(true);
        }else {
            changePwd(etOldPwd.getText().toString(),etNewPwd.getText().toString());
        }
    }

    /**
     * 修改密码
     */
    private void changePwd(String oldPwd,String newPwd) {
        user = UserUtils.getUser(UpdatePwdActivity.this);
        OkHttpUtils.post(UrlUtils.ROOT_PATH+UrlUtils.INTERFACE_PATH+"changePassword")
                .params("uid",user.getId())
                .params("oldpass",oldPwd)
                .params("newpass",newPwd)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject=new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if(result==1){
                                String data = jsonObject.optString("data");
                                UserUtils.changeUserPass(UpdatePwdActivity.this,data);
                                //实例化SharedPreferences对象（第一步）
                                SharedPreferences mySharedPreferences = getSharedPreferences("login",
                                        Activity.MODE_PRIVATE);
                                //实例化SharedPreferences.Editor对象（第二步）
                                SharedPreferences.Editor editor = mySharedPreferences.edit();
                                //用putString的方法保存数据
                                editor.putString("username", user.getUserName());
                                editor.putString("password", "");
                                //提交当前数据
                                editor.commit();
                                Intent intent =new Intent(UpdatePwdActivity.this,LoginActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 判断EditText还是否为空
     * @param et
     * @return
     */
    private boolean isEmpty(EditText et){
        return TextUtils.isEmpty(et.getText().toString());
    }

    /**
     * 显示信息
     * @param msg
     */
    private void showMessage(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
