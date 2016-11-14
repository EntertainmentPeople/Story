package com.klj.story;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
 * 修改性别页面
 */
public class UpdateSexActivity extends AppCompatActivity {
    private ImageView ivUpdateSexBack;
    private ImageView ivUpdateSexSave;
    private RadioGroup rgUpdateSex;
    private RadioButton rbUpdateMan;
    private RadioButton rbUpdateWoman;
    User user;    //用户信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_sex);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        initData();
        setListener();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        user =UserUtils.getUser(UpdateSexActivity.this);
        String sex = user.getUserSex();
        if(sex.equals("0")){
            rbUpdateMan.setChecked(true);
        }else if(sex.equals("1")){
            rbUpdateWoman.setChecked(true);
        }
    }

    /**
     * 设置监听
     */
    private void setListener() {
        ivUpdateSexBack.setOnClickListener(new MyImageViewClickListener());
        ivUpdateSexSave.setOnClickListener(new MyImageViewClickListener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivUpdateSexBack = (ImageView) findViewById(R.id.iv_updatesex_back);
        ivUpdateSexSave = (ImageView) findViewById(R.id.iv_updatesex_save);
        rgUpdateSex = (RadioGroup) findViewById(R.id.rg_updatesex_sex);
        rbUpdateMan = (RadioButton) findViewById(R.id.rb_updatesex_man);
        rbUpdateWoman = (RadioButton) findViewById(R.id.rb_updatesex_woman);
    }

    /**
     * 为图片设置点击监听事件
     */
    private class MyImageViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_updatesex_back:
                    finish();
                    break;
                case R.id.iv_updatesex_save:
                    save();
                    break;
            }
        }
    }

    /**
     * 保存修改
     */
    private void save() {
        if (rbUpdateMan.isChecked()) {
            changeSex(0+"");
        } else if (rbUpdateWoman.isChecked()) {
            changeSex(1+"");
        } else {
            Toast.makeText(this, "请选择性别", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 修改性别
     * @param sex
     */
    private void changeSex(final String sex) {
        try {
            user = UserUtils.getUser(UpdateSexActivity.this);
            OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "changeSex")
                    .params("uid", user.getId())
                    .params("userpass", user.getUserPass())
                    .params("usersex", sex)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(String s, Call call, Response response) {
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                int result = jsonObject.optInt("result");
                                if (result == 1) {
                                    UserUtils.changeSex(UpdateSexActivity.this,sex);
                                    Intent intent = new Intent();
                                    intent.putExtra("sex", sex);
                                    setResult(ConstantUtil.CHANGE_SEX_RESULT_CODE, intent);
                                    finish();
                                }
                                Toast.makeText(UpdateSexActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}