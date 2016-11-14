package com.klj.story.entity;

import java.io.Serializable;

/**
 * 用户信息实体类
 */
public class User implements Serializable{
    private String id;
    private String userName;
    private String userPass;
    private String userSex;
    private String userEmail;
    private String nickName;
    private String birthday;
    private String portrait;
    private String signature;


    public User() {
    }

    public User(String id, String userName, String userPass, String userSex, String userEmail, String nickName, String birthday, String portrait,String signature) {
        this.id = id;
        this.userName = userName;
        this.userPass = userPass;
        this.userSex = userSex;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.birthday = birthday;
        this.portrait = portrait;
        this.signature = signature;
    }

    public User(String id, String userName, String userSex, String userEmail, String nickName, String birthday, String portrait,String signature) {
        this.id = id;
        this.userName = userName;
        this.userSex = userSex;
        this.userEmail = userEmail;
        this.nickName = nickName;
        this.birthday = birthday;
        this.portrait = portrait;
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPortrait() {
        return portrait;
    }

    public void setPortrait(String portrait) {
        this.portrait = portrait;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", userPass='" + userPass + '\'' +
                ", userSex='" + userSex + '\'' +
                ", userEmail='" + userEmail + '\'' +
                ", nickName='" + nickName + '\'' +
                ", birthday='" + birthday + '\'' +
                ", portrait='" + portrait + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }
}
