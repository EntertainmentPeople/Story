package com.klj.story.utils;

import android.content.Context;

import com.klj.story.entity.User;

/**
 * 用户信息帮助类
 */
public class UserUtils {
    private static User user;

    public static User getUser(Context context) {
        String id = (String) Utils.getSPFile(context, "user", "id", "");
        String userName = (String) Utils.getSPFile(context, "user", "userName", "");
        String userPass = (String) Utils.getSPFile(context, "user", "userPass", "");
        String userSex = (String) Utils.getSPFile(context, "user", "userSex", "");
        String userEmail = (String) Utils.getSPFile(context, "user", "userEmail", "");
        String nickName = (String) Utils.getSPFile(context, "user", "nickName", "");
        String birthday = (String) Utils.getSPFile(context, "user", "birthday", "");
        String portrait = (String) Utils.getSPFile(context, "user", "portrait", "");
        String signature = (String) Utils.getSPFile(context, "user", "signature", "");
        user = new User(id, userName, userPass, userSex, userEmail, nickName, birthday, portrait, signature);
        return user;
    }

    /**
     * 将用户信息保存到SP文件
     *
     * @param context
     * @param user
     */
    public static void saveUserToSP(Context context, User user) {
        Utils.saveSPFile(context, "user", "id", user.getId());
        Utils.saveSPFile(context, "user", "userName", user.getUserName());
        Utils.saveSPFile(context, "user", "userPass", user.getUserPass());
        Utils.saveSPFile(context, "user", "userSex", user.getUserSex());
        Utils.saveSPFile(context, "user", "userEmail", user.getUserEmail());
        Utils.saveSPFile(context, "user", "nickName", user.getNickName());
        Utils.saveSPFile(context, "user", "birthday", user.getBirthday());
        Utils.saveSPFile(context, "user", "portrait", user.getPortrait());
        Utils.saveSPFile(context, "user", "signature", user.getSignature());
    }

    /**
     * 修改性别
     *
     * @param sex
     */
    public static void changeSex(Context context, String sex) {
        Utils.saveSPFile(context, "user", "userSex", user.getUserSex());
    }

    /**
     * 修改邮箱
     *
     * @param email
     */
    public static void changeEmail(Context context, String email) {
        Utils.saveSPFile(context, "user", "userEmail", user.getUserEmail());
    }

    /**
     * 修改生日
     *
     * @param birthday
     */
    public static void changeBirthday(Context context, String birthday) {
        //user.setBirthday(birthday);
        Utils.saveSPFile(context, "user", "birthday", user.getBirthday());
    }

    /**
     * 修改昵称
     *
     * @param nickName
     */
    public static void changeNickName(Context context, String nickName) {
        //user.setNickName(nickName);
        Utils.saveSPFile(context, "user", "nickName", user.getNickName());
    }

    /**
     * 修改头像
     *
     * @param portrait
     */
    public static void changePortrait(Context context, String portrait) {
        //user.setPortrait(portrait);
        Utils.saveSPFile(context, "user", "portrait", user.getPortrait());
    }

    /**
     * 修改唯一标示
     *
     * @param userPass
     */
    public static void changeUserPass(Context context, String userPass) {
        //user.setUserPass(userPass);
        Utils.saveSPFile(context, "user", "userPass", user.getUserPass());
    }

    /**
     * 修改签名
     *
     * @param signature
     */
    public static void changeSignature(Context context, String signature) {
        //user.setSignature(signature);
        Utils.saveSPFile(context, "user", "signature", user.getSignature());
    }

    @Override
    public String toString() {
        return "UserUtils{}";
    }
}
