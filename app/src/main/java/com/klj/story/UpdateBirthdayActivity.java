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
 * 修改生日页面
 */
public class UpdateBirthdayActivity extends AppCompatActivity {
    private ImageView ivUpdateBirthdayBack;
    private ImageView ivUpdateBirthdaySave;
    private EditText etBirthday;
    private User user;    //用户信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_birthday);
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
        ivUpdateBirthdayBack.setOnClickListener(new MyOnClickLisstener());
        ivUpdateBirthdaySave.setOnClickListener(new MyOnClickLisstener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivUpdateBirthdayBack = (ImageView) findViewById(R.id.iv_updatebirthday_back);
        ivUpdateBirthdaySave = (ImageView) findViewById(R.id.iv_updatebirthday_save);
        etBirthday = (EditText) findViewById(R.id.et_updatebirthday_birthday);
    }

    /**
     * 为控件设置点击监听
     */
    private class MyOnClickLisstener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.iv_updatebirthday_back:
                    finish();
                    break;
                case R.id.iv_updatebirthday_save:
                    save();
                    break;
            }
        }
    }

    /**
     * 保存修改
     */
    private void save() {
        user= UserUtils.getUser(UpdateBirthdayActivity.this);
        final String birthday = etBirthday.getText().toString().trim();
        if(birthday!=null&&!"".equals(birthday)){
            OkHttpUtils.post(UrlUtils.ROOT_PATH+UrlUtils.INTERFACE_PATH+"changeBirthday")
                    .params("uid", user.getId())
                    .params("userpass", user.getUserPass())
                    .params("birthday",birthday)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                int result = jsonObject.optInt("result");
                                if (result == 1) {
                                    UserUtils.changeBirthday(UpdateBirthdayActivity.this,birthday);
                                    Intent intent = new Intent();
                                    intent.putExtra("birthday", birthday);
                                    setResult(ConstantUtil.CHANGE_BIRTHDAY_RESULT_CODE, intent);
                                    finish();
                                }
                                Toast.makeText(UpdateBirthdayActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }else {
            Toast.makeText(UpdateBirthdayActivity.this, "生日不能为空，请输入生日", Toast.LENGTH_SHORT).show();
        }
    }
}
