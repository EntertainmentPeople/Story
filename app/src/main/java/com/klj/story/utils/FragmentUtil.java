package com.klj.story.utils;

import android.support.v4.app.Fragment;

import com.klj.story.fragment.HotsFragment;
import com.klj.story.fragment.NewsFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment的帮助类
 */
public class FragmentUtil {
    private static NewsFragment newsFragment;
    private static HotsFragment hotsFragment;
    private static List<Fragment> fragments =new ArrayList<>();

    /**
     * 静态代码块，用于初始化fragment
     */
    static {
        newsFragment=new NewsFragment();
        hotsFragment=new HotsFragment();
        fragments.add(newsFragment);
        fragments.add(hotsFragment);
    }

    /**
     * 获取到所有的fragment
     * @return
     */
    public static List<Fragment> getFragments(){
        return fragments;
    }

    /**
     * 获取fragment的数量
     * @return
     */
    public static int getFragmentSize(){
        return fragments.size();
    }
}
