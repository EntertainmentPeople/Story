package com.klj.story;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 设置页面
 */
public class SettingActivity extends AppCompatActivity {

    private LinearLayout llSettingEncourage;
    private LinearLayout llSettingDonate;
    private LinearLayout llSettingMessage;
    private LinearLayout llSettingUpdate;
    private LinearLayout llSettingProblem;
    private LinearLayout llSettingAbout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
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
     * 设置监听
     */
    private void setListener() {
        llSettingEncourage.setOnClickListener(new MyOnClickListener());
    }

    /**
     * 找到控件
     */
    private void findView() {
        llSettingEncourage = (LinearLayout) findViewById(R.id.ll_setting_encourage);
        llSettingDonate = (LinearLayout) findViewById(R.id.ll_setting_donate);
        llSettingMessage = (LinearLayout) findViewById(R.id.ll_setting_message);
        llSettingUpdate = (LinearLayout) findViewById(R.id.ll_setting_update);
        llSettingProblem = (LinearLayout) findViewById(R.id.ll_setting_problem);
        llSettingAbout = (LinearLayout) findViewById(R.id.ll_setting_about);
    }

    /**
     * 设置点击的监听事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ll_setting_encourage:
                    break;
                case R.id.ll_setting_donate:
                    break;
                case R.id.ll_setting_message:
                    break;
                case R.id.ll_setting_update:
                    break;
                case R.id.ll_setting_problem:
                    break;
                case R.id.ll_setting_about:
                    break;

            }
        }
    }
}
