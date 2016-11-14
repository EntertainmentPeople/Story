package com.klj.story;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.klj.story.utils.FragmentUtil;
import com.klj.story.utils.Utils;
import com.klj.story.view.MySlidingPaneLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * 主页面
 */
public class MainActivity extends AppCompatActivity {
    private MySlidingPaneLayout splBar;
    private ImageView ivMenu;
    private ImageView ivEdit;
    private RadioGroup rgMenu;
    private RadioButton rbNews;
    private RadioButton rbHots;
    private ViewPager vpShow;
    private LinearLayout llMenuHome;
    private LinearLayout llMenuMyStory;
    private LinearLayout llMenuRecode;
    private LinearLayout llMenuMine;
    private LinearLayout llMenuSetting;
    private Button btnExit;
    private boolean openOrNot = false;    //用来标记左边菜单是否打开，默认是没有打开的
    MyOnClickListener myOnClickListener;    //点击事件对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

 /*   @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.activity_main);
        init();
    }*/


    /**
     * 初始化
     */
    private void init() {
        findView();
        setAdapter();
        setListener();
    }

    /**
     * 设置监听事件
     */
    private void setListener() {
        rgMenu.setOnCheckedChangeListener(new MyRadioGroupChangeListener());
        vpShow.addOnPageChangeListener(new MyViewPagerChangeListener());
        myOnClickListener = new MyOnClickListener();
        ivMenu.setOnClickListener(myOnClickListener);
        ivEdit.setOnClickListener(myOnClickListener);
        llMenuHome.setOnClickListener(myOnClickListener);
        llMenuMyStory.setOnClickListener(myOnClickListener);
        llMenuRecode.setOnClickListener(myOnClickListener);
        llMenuMine.setOnClickListener(myOnClickListener);
        llMenuSetting.setOnClickListener(myOnClickListener);
        btnExit.setOnClickListener(myOnClickListener);
    }


    /**
     * 设置适配器
     */
    private void setAdapter() {
        vpShow.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
    }

    /**
     * 找到控件
     */
    private void findView() {
        splBar = (MySlidingPaneLayout) findViewById(R.id.spl_bar);
        ivMenu = (ImageView) findViewById(R.id.iv_menu);
        ivEdit = (ImageView) findViewById(R.id.iv_edit);
        rgMenu = (RadioGroup) findViewById(R.id.rg_menu);
        rbNews = (RadioButton) findViewById(R.id.rb_news);
        rbHots = (RadioButton) findViewById(R.id.rb_hots);
        vpShow = (ViewPager) findViewById(R.id.vp_main);
        llMenuHome = (LinearLayout) findViewById(R.id.ll_menu_home);
        llMenuMyStory = (LinearLayout) findViewById(R.id.ll_menu_mystory);
        llMenuRecode = (LinearLayout) findViewById(R.id.ll_menu_recode);
        llMenuMine = (LinearLayout) findViewById(R.id.ll_menu_mine);
        llMenuSetting = (LinearLayout) findViewById(R.id.ll_menu_setting);
        btnExit = (Button) findViewById(R.id.btn_menu_exit);
    }

    /**
     * 为ViewPager设置适配器
     */
    private class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FragmentUtil.getFragments().get(position);
        }

        @Override
        public int getCount() {
            return FragmentUtil.getFragmentSize();
        }
    }

    /**
     * 为RadioGroup设置RadioButton选中的监听事件
     */
    private class MyRadioGroupChangeListener implements RadioGroup.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_news:
                    vpShow.setCurrentItem(0);
                    break;
                case R.id.rb_hots:
                    vpShow.setCurrentItem(1);
                    break;
            }
        }
    }

    /**
     * 为ViewPager设置页面改变监听事件
     */
    private class MyViewPagerChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            RadioButton radioButton = (RadioButton) rgMenu.getChildAt(position);
            radioButton.setChecked(true);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * 为控件设置点击的监听事件
     */
    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = null;
            switch (v.getId()) {
                case R.id.iv_menu:
                    openMenu();
                    break;
                case R.id.iv_edit:
                    intent = new Intent(MainActivity.this, NewStoryActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_menu_home:
                    splBar.closePane();
                    break;
                case R.id.ll_menu_mystory:
                    intent = new Intent(MainActivity.this, MyStoryActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_menu_recode:
                    intent = new Intent(MainActivity.this, BrowseRecodeActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_menu_mine:
                    intent = new Intent(MainActivity.this, MineActivity.class);
                    startActivity(intent);
                    break;
                case R.id.ll_menu_setting:
                    intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                    break;
                case R.id.btn_menu_exit:
                    finish();
                    break;
            }
        }
    }

    /**
     * 打开左边菜单栏
     */
    private void openMenu() {
        splBar.isOpen();   //判定那个菜单是否是开着的
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("---------", "我执行了...");
                if (openOrNot == false) {
                    splBar.openPane();
                } else {
                    splBar.closePane();
                }
                openOrNot = !openOrNot;
            }
        });
        splBar.setPanelSlideListener(new SlidingPaneLayout.PanelSlideListener() {
            /**
             * 表示的是正在滑动
             */
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            /**
             * 打开菜单之后的回调
             */
            @Override
            public void onPanelOpened(View panel) {
                openOrNot = true;
            }

            /**
             * 关闭菜单之后的回调
             */
            @Override
            public void onPanelClosed(View panel) {
                openOrNot = false;
            }
        });
    }


    long exitTime = 0;

    @Override
    public void onBackPressed() {
        long mNowTime = System.currentTimeMillis();//获取第一次按键时间
        if ((mNowTime - exitTime) > 2000) {//比较两次按键时间差
            Utils.showMessage(MainActivity.this, "再点一次退出！");
            exitTime = mNowTime;
        } else {//退出程序
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}
