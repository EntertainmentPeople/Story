package com.klj.story;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.klj.story.utils.Utils;

/**
 * 引导页面
 */
public class GuideActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        gotoMainActivity();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
    }

    /**
     * 判断是否已登录
     *
     * @return
     */
    private boolean isLoginOrNot() {
        String userName = (String) Utils.getSPFile(GuideActivity.this, "login", "userName", "");
        String pwd = (String) Utils.getSPFile(GuideActivity.this, "login", "password", "");
        if (!Utils.isEmpty(userName) && !Utils.isEmpty(pwd)) {
            return true;
        }
        return false;
    }

    /**
     * 转到主界面
     */
    private void gotoMainActivity() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    Intent intent=null;
                    if (!isLoginOrNot()) {
                        intent = new Intent(GuideActivity.this, LoginActivity.class);
                    } else {
                        intent = new Intent(GuideActivity.this, MainActivity.class);
                    }
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
