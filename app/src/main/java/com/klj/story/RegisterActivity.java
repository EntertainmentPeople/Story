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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.klj.story.utils.ConstantUtil;
import com.klj.story.utils.UrlUtils;
import com.lzy.okhttputils.OkHttpUtils;
import com.lzy.okhttputils.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

import okhttp3.Call;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private ImageView ivRegisterBack;
    private ImageView ivRegisterProtrait;
    private EditText etRegisterAccount;
    private EditText etRegisterNickName;
    private EditText etRegisterPwd;
    private Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        findView();
        setListener();

    }

    private void setListener() {
        ivRegisterBack.setOnClickListener(new MyClickListener());
        ivRegisterProtrait.setOnClickListener(new MyClickListener());
        btnRegister.setOnClickListener(new MyClickListener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        ivRegisterBack = (ImageView) findViewById(R.id.iv_register_back);
        ivRegisterProtrait = (ImageView) findViewById(R.id.iv_register_protrait);
        etRegisterAccount = (EditText) findViewById(R.id.et_register_account);
        etRegisterNickName = (EditText) findViewById(R.id.et_register_nickname);
        etRegisterPwd = (EditText) findViewById(R.id.et_register_pwd);
        btnRegister = (Button) findViewById(R.id.btn_register);
    }

    /**
     * 设置点击监听
     */
    private class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.iv_register_back:
                    finish();
                    break;
                case R.id.iv_register_protrait:
                    choosePic();
                    break;
                case R.id.btn_register:
                    register();
                    break;
            }
        }
    }

    Uri uri = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            uri = data.getData();
            ContentResolver cr = this.getContentResolver();
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
                ivRegisterProtrait.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                Log.e("Exception", e.getMessage(), e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        startActivityForResult(intent, 1);
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

            Cursor cursor = RegisterActivity.this.managedQuery(uri, proj,
                    null,
                    null,
                    null);
            //Cursor cursor=getContentResolver().query(uri,proj,null,null,null);
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

    /**
     * 注册
     */
    private void register() {
        final String username = getText(etRegisterAccount);
        String nickNmae = getText(etRegisterNickName);
        final String pwd = getText(etRegisterPwd);
        String path = getAbsoluteImagePath(uri);
        OkHttpUtils.post(UrlUtils.ROOT_PATH + UrlUtils.INTERFACE_PATH + "regist")
                .params("username", username)
                .params("nikename", nickNmae)
                .params("password", pwd)
                .params("portrait", new File(path))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(String s, Call call, Response response) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            Toast.makeText(RegisterActivity.this, jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.putExtra("username", username);
                            intent.putExtra("pwd", pwd);
                            setResult(ConstantUtil.REGISTER_RESULT_CODE, intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
    }

    /**
     * 获取控件的值
     */
    private String getText(EditText editText) {
        return editText.getText().toString().trim();
    }

}
