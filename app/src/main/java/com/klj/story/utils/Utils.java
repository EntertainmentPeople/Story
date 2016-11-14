package com.klj.story.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.widget.Toast;

import com.klj.story.entity.User;

import java.util.HashMap;
import java.util.Map;

/**
 * 常用方法工具类
 */
public class Utils {
    /**
     * 时间格式化
     *
     * @param timestampString
     * @return
     */
    public static String toTransferTime(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 获取到年、月
     *
     * @param timestampString
     * @return
     */
    public static String getYearAndMonth(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("yyyy.MM").format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 获取到日、时、分
     *
     * @param timestampString
     * @return
     */
    public static String getDayAndHourAndMinute(String timestampString) {
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new java.text.SimpleDateFormat("dd日 HH:mm").format(new java.util.Date(timestamp));
        return date;
    }

    /**
     * 获取登录后的账号和密码
     *
     * @param context
     * @return
     */
    public static Map<String, String> getUserNameAndPwd(Context context) {
        //同样，在读取SharedPreferences数据前要实例化出一个SharedPreferences对象
        SharedPreferences sharedPreferences = context.getSharedPreferences("login",
                Activity.MODE_PRIVATE);
        // 使用getString方法获得value，注意第2个参数是value的默认值
        String userName = sharedPreferences.getString("userName", "");
        String pwd = sharedPreferences.getString("password", "");
        Map<String, String> map = new HashMap<>();
        map.put("userName", userName);
        map.put("password", pwd);
        return map;
    }

    /**
     * 将登陆账号和密码保存起来
     *
     * @param userName
     * @param pwd
     */
    public static void saveUserNameAndPwd(Context context, String userName, String pwd) {
        //实例化SharedPreferences对象（第一步）
        SharedPreferences mySharedPreferences = context.getSharedPreferences("login",
                Activity.MODE_PRIVATE);
        //实例化SharedPreferences.Editor对象（第二步）
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        //用putString的方法保存数据
        editor.putString("userName", userName);
        editor.putString("password", pwd);
        //提交当前数据
        editor.commit();
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isEmpty(String s) {
        return TextUtils.isEmpty(s);
    }

    /**
     * 显示提示
     *
     * @param context
     * @param msg
     */
    public static void showMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 将用户信息保存到sp文件中
     *
     * @param context
     * @param user
     */
    public static void saveUserInfo(Context context, User user) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences("user",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        editor.putString("id", user.getId());
        editor.putString("userName", user.getUserName());
        editor.putString("userPass", user.getUserPass());
        editor.putString("userSex", user.getUserSex());
        editor.putString("userEmail", user.getUserEmail());
        editor.putString("nickName", user.getNickName());
        editor.putString("birthday", user.getBirthday());
        editor.putString("portrait", user.getPortrait());
        editor.putString("signature", user.getSignature());
        //提交当前数据
        editor.commit();
    }


    /**
     * 保存信息到SP文件中
     *
     * @param context
     * @param fileName
     * @param map
     */
    public static void saveSPFile(Context context, String fileName, String key, Object value) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = mySharedPreferences.edit();
        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else {
            editor.putString(key, value.toString());
        }
        //提交当前数据
        editor.commit();
    }

    /**
     * 获取保存的SP文件
     *
     * @param context
     * @param fileName
     * @param key
     * @param defaultValue
     * @return
     */
    public static Object getSPFile(Context context, String fileName, String key, Object defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(fileName,
                Activity.MODE_PRIVATE);

        if (defaultValue instanceof String) {
            return sharedPreferences.getString(key, (String) defaultValue);
        } else if (defaultValue instanceof Integer) {
            return sharedPreferences.getInt(key, (Integer) defaultValue);
        } else if (defaultValue instanceof Boolean) {
            return sharedPreferences.getBoolean(key, (Boolean) defaultValue);
        } else if (defaultValue instanceof Float) {
            return sharedPreferences.getFloat(key, (Float) defaultValue);
        } else if (defaultValue instanceof Long) {
            return sharedPreferences.getLong(key, (Long) defaultValue);
        }
        return null;
    }
}
