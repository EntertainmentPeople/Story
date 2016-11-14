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
 * 修改昵称页面
 */
public class UpdateNickNameActivity extends AppCompatActivity {

    private ImageView ivUpdateNickNameBack;
    private EditText etNickName;
    private ImageView ivUpdateNickNameSave;
    User user;  //用户信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick_name);
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
        ivUpdateNickNameBack.setOnClickListener(new MyOnClickLisstener());
        ivUpdateNickNameSave.setOnClickListener(new MyOnClickLisstener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivUpdateNickNameBack = (ImageView) findViewById(R.id.iv_updatenickname_back);
        ivUpdateNickNameSave = (ImageView) findViewById(R.id.iv_updatenickname_save);
        etNickName = (EditText) findViewById(R.id.et_updatenickname_nickname);
    }

    /**
     * 为控件设置点击监听
     */
    private class MyOnClickLisstener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_updatenickname_back:
                    finish();
                    break;
                case R.id.iv_updatenickname_save:
                    save();
                    break;
            }
        }
    }

    /**
     * 保存修改
     */
    private void save() {
        user=UserUtils.getUser(UpdateNickNameActivity.this);
        final String nickName = etNickName.getText().toString().trim();
        if(nickName!=null&&!"".equals(nickName)){
            OkHttpUtils.post(UrlUtils.ROOT_PATH+UrlUtils.INTERFACE_PATH+"changeNickName")
                    .params("uid", user.getId())
                    .params("userpass", user.getUserPass())
                    .params("nickname",nickName)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                int result = jsonObject.optInt("result");
                                if (result == 1) {
                                    UserUtils.changeNickName(UpdateNickNameActivity.this,nickName);
                                    Intent intent = new Intent();
                                    intent.putExtra("nickName", nickName);
                                    setResult(ConstantUtil.CHANGE_NICKNAME_RESULT_CODE, intent);
                                    finish();
                                }
                                Toast.makeText(UpdateNickNameActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else {
            Toast.makeText(UpdateNickNameActivity.this, "昵称不能为空，请输入昵称", Toast.LENGTH_SHORT).show();
        }

    }
}
