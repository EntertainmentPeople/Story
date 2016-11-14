package com.klj.story;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.klj.story.entity.User;
import com.klj.story.utils.ConstantUtil;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.UserUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 修改邮箱页面
 */
public class UpdateEmailActivity extends AppCompatActivity {
    private ImageView ivUpdateEmailBack;
    private ImageView ivUpdateEmailSave;
    private EditText etEmail;
    private User user;  //用户信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
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
     * 设置监听事件
     */
    private void setListener() {
        ivUpdateEmailBack.setOnClickListener(new MyOnClickLisstener());
        ivUpdateEmailSave.setOnClickListener(new MyOnClickLisstener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivUpdateEmailBack = (ImageView) findViewById(R.id.iv_updateemail_back);
        ivUpdateEmailSave = (ImageView) findViewById(R.id.iv_updateemail_save);
        etEmail = (EditText) findViewById(R.id.et_updateemail_email);
    }

    /**
     * 为控件设置点击监听
     */
    private class MyOnClickLisstener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_updateemail_back:
                    finish();
                    break;
                case R.id.iv_updateemail_save:
                    save();
                    break;
            }
        }
    }

    /**
     * 保存修改
     */
    private void save() {
        user= UserUtils.getUser(UpdateEmailActivity.this);
        final String email = etEmail.getText().toString().trim();
        if(email!=null&&!"".equals(email)){
            OkHttpUtils.post(UrlUtils.ROOT_PATH+UrlUtils.INTERFACE_PATH+"changeEmail")
                    .params("uid", user.getId())
                    .params("userpass", user.getUserPass())
                    .params("useremail",email)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                int result = jsonObject.optInt("result");
                                if (result == 1) {
                                    UserUtils.changeEmail(UpdateEmailActivity.this,email);
                                    Intent intent = new Intent();
                                    intent.putExtra("email", email);
                                    setResult(ConstantUtil.CHANGE_EMAIL_RESULT_CODE, intent);
                                    finish();
                                }
                                Toast.makeText(UpdateEmailActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else {
            Toast.makeText(UpdateEmailActivity.this, "邮箱不能为空，请输入邮箱", Toast.LENGTH_SHORT).show();
        }

    }
}