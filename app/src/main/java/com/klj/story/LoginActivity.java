package com.klj.story;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.story.entity.User;
import com.klj.story.fragment.NewsFragment;
import com.klj.story.utils.ConstantUtil;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.UserUtils;
import com.klj.story.utils.Utils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 登录页面
 */
public class LoginActivity extends AppCompatActivity {

    private ImageView imLoginBack;
    private EditText etAccount;
    private EditText etPwd;
    private Button btnLogin;
    private TextView tvToRegister;
    //List<String> list = new ArrayList<>();  // 存储获取到的用户名和密码
    public User user;                       //用户信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        //initData();
        setClickListener();
    }

    /**
     * 初始化数据
     *//*
    private void initData() {
        list = Utils.getUserNameAndPwd(LoginActivity.this);
        if (list != null) {
            etAccount.setText(list.get(0));
            etPwd.setText(list.get(1));
            if(!"".equals(etAccount.getText().toString().trim())&&!"".equals(etPwd.getText().toString().trim())){
                login();
            }
        }
    }*/

    /**
     * 为控件设置点击坚挺事件
     */
    private void setClickListener() {
        imLoginBack.setOnClickListener(new MyClickListener());
        btnLogin.setOnClickListener(new MyClickListener());
        tvToRegister.setOnClickListener(new MyClickListener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        imLoginBack = (ImageView) findViewById(R.id.iv_login_back);
        etAccount = (EditText) findViewById(R.id.et_login_account);
        etPwd = (EditText) findViewById(R.id.et_login_pwd);
        btnLogin = (Button) findViewById(R.id.btn_login);
        tvToRegister = (TextView) findViewById(R.id.tv_toregister);
    }

    /**
     * 这就是为上面的返回图标设置监听
     */
    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_login_back:
                    finish();
                    break;
                case R.id.btn_login:
                    login();
                    break;
                case R.id.tv_toregister:
                    toRegister();
                    break;
            }
        }
    }

    /**
     * 登录
     */
    private void login() {
        final String userName = etAccount.getText().toString().trim();
        final String pwd = etPwd.getText().toString();
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "login")
                .params("username", userName)
                .params("password", pwd)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                getData(jsonObject);
                                Utils.saveSPFile(LoginActivity.this, "login", "userName", userName);
                                Utils.saveSPFile(LoginActivity.this, "login", "password", pwd);
                                Intent intent =new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            Toast.makeText(LoginActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 将返回的数据保存到User类中
     *
     * @param jsonObject
     * @throws JSONException
     */
    private void getData(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String id = data.optString("id");
        String userName = data.optString("username");
        String userPass = data.optString("userpass");
        String userSex = data.optString("usersex");
        String userEmail = data.optString("useremail");
        String nickName = data.optString("nickname");
        String birthday = data.optString("birthday");
        String portrait = data.optString("portrait");
        String signature = data.optString("signature");
        user = new User(id, userName, userPass, userSex, userEmail, nickName, birthday, portrait, signature);
        UserUtils.saveUserToSP(LoginActivity.this, user);
    }

    /**
     * 跳转到注册界面
     */
    private void toRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, ConstantUtil.REGISTER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RESULT_OK，判断另外一个activity已经结束数据输入功能，Standard activity result:
        // operation succeeded. 默认值是-1
        if (resultCode == ConstantUtil.REGISTER_RESULT_CODE) {
            if (requestCode == ConstantUtil.REGISTER_REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                //设置结果显示框的显示数值
                etAccount.setText(bundle.getString("username"));
                etPwd.setText(bundle.getString("pwd"));
            }

        }
    }

    /**
     * 双击退出程序
     */
    long exitTime = 0;
    @Override
    public void onBackPressed() {
       finish();
    }

    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);

    }*/
}
