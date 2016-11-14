package com.klj.story;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.klj.story.entity.User;
import com.klj.story.utils.ConstantUtil;
import com.klj.story.utils.UrlUtils;
import com.klj.story.utils.UserUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.Call;
import okhttp3.Response;

/**
 * 我的故事页面
 */
public class MineActivity extends AppCompatActivity {

    private ImageView ivMineBack;
    private ImageView ivMinePhoto;
    private TextView tvMineAccount;
    private TextView tvMineNickName;
    private TextView tvMineSex;
    private TextView tvMineEmail;
    private TextView tvMineBirthday;
    private LinearLayout llMineNickName;
    private LinearLayout llMineSex;
    private LinearLayout llMineEmail;
    private LinearLayout llMineBirthday;
    private Button btnUpdatePwd;
    private ImageView ivMineSex;
    MyOnClickListener myOnClickListener;    //点击事件对象
    User user = null;                         //用户信息


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mine);
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
        getUserInfo();
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            user = (User) bundle.getSerializable("user");
        } else {
            user = UserUtils.getUser(MineActivity.this);
        }
        if (null != user) {
            Log.e("----mine---", user.toString());
            Picasso.with(MineActivity.this).load(UrlUtils.ROOT_PATH + UrlUtils.PORTRAIT_PATH + user.getPortrait()).placeholder(R.drawable.icon_portrait).into(ivMinePhoto);
            tvMineAccount.setText(user.getUserName());
            tvMineNickName.setText(user.getNickName());
            String sex = user.getUserSex();
            if (sex.equals("0")) {
                ivMineSex.setImageResource(R.drawable.icon_man);
                sex = "男";
            } else if (sex.equals("1")) {
                ivMineSex.setImageResource(R.drawable.icon_woman);
                sex = "女";
            }
            tvMineSex.setText(sex);
            tvMineEmail.setText(user.getUserEmail());
            tvMineBirthday.setText(user.getBirthday());
        }
    }

    /**
     *
     */
    private void getUserInfo() {

    }


    /**
     * 设置监听
     */
    private void setListener() {
        myOnClickListener = new MyOnClickListener();
        ivMineBack.setOnClickListener(myOnClickListener);
        if (user.getId().equals(UserUtils.getUser(MineActivity.this).getId())) {
            llMineNickName.setOnClickListener(myOnClickListener);
            llMineSex.setOnClickListener(myOnClickListener);
            llMineEmail.setOnClickListener(myOnClickListener);
            llMineBirthday.setOnClickListener(myOnClickListener);
            btnUpdatePwd.setOnClickListener(myOnClickListener);
            ivMinePhoto.setOnClickListener(myOnClickListener);
        } else {
            btnUpdatePwd.setVisibility(View.GONE);
        }
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivMineBack = (ImageView) findViewById(R.id.iv_mine_back);
        ivMinePhoto = (ImageView) findViewById(R.id.iv_mine_photo);
        tvMineAccount = (TextView) findViewById(R.id.tv_mine_account);
        tvMineNickName = (TextView) findViewById(R.id.tv_mine_nickname);
        tvMineSex = (TextView) findViewById(R.id.tv_mine_sex);
        tvMineEmail = (TextView) findViewById(R.id.tv_mine_email);
        tvMineBirthday = (TextView) findViewById(R.id.tv_mine_birthday);
        llMineNickName = (LinearLayout) findViewById(R.id.ll_mine_nickname);
        llMineSex = (LinearLayout) findViewById(R.id.ll_mine_sex);
        llMineEmail = (LinearLayout) findViewById(R.id.ll_mine_email);
        llMineBirthday = (LinearLayout) findViewById(R.id.ll_mine_birthday);
        btnUpdatePwd = (Button) findViewById(R.id.btn_mine_updatepwd);
        ivMineSex = (ImageView) findViewById(R.id.iv_mine_sex);
    }

    /**
     * 为控件设置点击监听
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.iv_mine_back:
                    finish();
                    break;
                case R.id.iv_mine_photo:
                    choosePic();
                    break;
                case R.id.ll_mine_nickname:
                    intent = new Intent(MineActivity.this, UpdateNickNameActivity.class);
                    startActivityForResult(intent, ConstantUtil.CHANGE_NICKNAME_REQUEST_CODE);
                    break;
                case R.id.ll_mine_sex:
                    intent = new Intent(MineActivity.this, UpdateSexActivity.class);
                    startActivityForResult(intent, ConstantUtil.CHANGE_SEX_REQUEST_CODE);
                    break;
                case R.id.ll_mine_email:
                    intent = new Intent(MineActivity.this, UpdateEmailActivity.class);
                    startActivityForResult(intent, ConstantUtil.CHANGE_EMAIL_REQUEST_CODE);
                    break;
                case R.id.ll_mine_birthday:
                    intent = new Intent(MineActivity.this, UpdateBirthdayActivity.class);
                    startActivityForResult(intent, ConstantUtil.CHANGE_BIRTHDAY_REQUEST_CODE);
                    break;
                case R.id.btn_mine_updatepwd:
                    intent = new Intent(MineActivity.this, UpdatePwdActivity.class);
                    startActivity(intent);
                    break;
            }

        }
    }


    Uri uri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (requestCode == ConstantUtil.CHANGE_PORRTRAILT_REQUEST_CODE) {
                    showPortrailt(data);
                }
                break;
            case ConstantUtil.CHANGE_NICKNAME_RESULT_CODE:
                if (requestCode == ConstantUtil.CHANGE_NICKNAME_REQUEST_CODE) {
                    setText(data, tvMineNickName, "nickName");
                }
                break;
            case ConstantUtil.CHANGE_BIRTHDAY_RESULT_CODE:
                if (requestCode == ConstantUtil.CHANGE_BIRTHDAY_REQUEST_CODE) {
                    setText(data, tvMineBirthday, "birthday");
                }
                break;
            case ConstantUtil.CHANGE_EMAIL_RESULT_CODE:
                if (requestCode == ConstantUtil.CHANGE_EMAIL_REQUEST_CODE) {
                    setText(data, tvMineEmail, "email");
                }
                break;
            case ConstantUtil.CHANGE_SEX_RESULT_CODE:
                if (requestCode == ConstantUtil.CHANGE_SEX_REQUEST_CODE) {
                    setText(data, tvMineSex, "sex");
                }
                break;
        }

    }

    /**
     * 修改后设置值
     */
    private void setText(Intent data, TextView tv, String key) {
        Bundle bundle = data.getExtras();
        String string = bundle.getString(key);
        if (!"".equals(string) && null != string) {
            if (key.equals("sex")) {
                if (string.equals("0")) {
                    ivMineSex.setImageResource(R.drawable.icon_man);
                    string = "男";
                } else if (string.equals("1")) {
                    ivMineSex.setImageResource(R.drawable.icon_woman);
                    string = "女";
                }
            }
            tv.setText(string);
        }
    }

    /**
     * 显示头像
     */
    private void showPortrailt(Intent data) {
        uri = data.getData();
        ContentResolver cr = this.getContentResolver();
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
            ivMinePhoto.setImageBitmap(bitmap);
            updatePortrait();
        } catch (FileNotFoundException e) {
            Log.e("Exception", e.getMessage(), e);
        }
    }

    /**
     * 更新头像
     */
    private void updatePortrait() {
        final String path = getAbsoluteImagePath(uri);
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "changePortrait")
                .params("uid", user.getId())
                .params("userpass", user.getUserPass())
                .params("portrait", new File(path))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            int result = jsonObject.optInt("result");
                            if (result == 1) {
                                UserUtils.changePortrait(MineActivity.this, jsonObject.optString("data"));
                            }
                            Toast.makeText(MineActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 选择图片
     */
    private void choosePic() {
        Intent intent = new Intent();
                /* 开启Pictures画面Type设定为image */
        intent.setType("image/*");
                /* 使用Intent.ACTION_GET_CONTENT这个Action */
        intent.setAction(Intent.ACTION_GET_CONTENT);
                /* 取得相片后返回本画面 */
        startActivityForResult(intent, ConstantUtil.CHANGE_PORRTRAILT_REQUEST_CODE);
    }

    /**
     * 通过uri获取文件的绝对路径
     *
     * @param uri
     * @return
     */
    private String getAbsoluteImagePath(Uri uri) {
        String imagePath = "";
        String[] proj = {MediaStore.Images.Media.DATA};
        if (uri != null) {
            Cursor cursor = MineActivity.this.managedQuery(uri, proj,
                    null,
                    null,
                    null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.getCount() > 0 && cursor.moveToFirst()) {
                    imagePath = cursor.getString(column_index);
                }
            }
        }
        return imagePath;
    }

}
