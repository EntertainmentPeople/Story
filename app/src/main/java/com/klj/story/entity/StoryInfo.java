package com.klj.story.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 故事实体类
 */
public class StoryInfo implements Serializable {

    /**
     * uid : 1
     * readcount : 0
     * story_time : 1440642626
     * lng :
     * city :
     * story_info : 今天晚上吃火锅
     * comment : 0
     * id : 4
     * pics : ["20150827/55de764229a0b.png"]
     * user : {"usersex":"0","birthday":"2012-06-06","signature":"反反复复","nickname":"饭否否","id":"1","portrait":"20150827/55dde54b8cfcd.png","username":"kooeasy","useremail":"koo@sina.com"}
     * lat :
     */
    private String id;
    private String storyTime;
    private String storyInfo;
    private List<String> pics;
    private String uid;
    private String lng;
    private String lat;
    private String city;
    private String readcount;
    private String comment;
    private User user;

    public StoryInfo() {
    }

    public StoryInfo(String id, String storyTime, String storyInfo, List<String> pics, String uid, String lng, String lat, String city, String readcount, String comment) {
        this.id = id;
        this.storyTime = storyTime;
        this.pics = pics;
        this.storyInfo = storyInfo;
        this.uid = uid;
        this.lng = lng;
        this.lat = lat;
        this.city = city;
        this.readcount = readcount;
        this.comment = comment;
    }

    public StoryInfo(String id, String storyTime, String storyInfo, List<String> pics, String uid, String lng, String lat, String city, String readcount, String comment, User user) {
        this.user = user;
        this.id = id;
        this.storyTime = storyTime;
        this.pics = pics;
        this.storyInfo = storyInfo;
        this.uid = uid;
        this.lng = lng;
        this.lat = lat;
        this.city = city;
        this.readcount = readcount;
        this.comment = comment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoryTime() {
        return storyTime;
    }

    public void setStoryTime(String storyTime) {
        this.storyTime = storyTime;
    }

    public List<String> getPics() {
        return pics;
    }

    public void setPics(List<String> pics) {
        this.pics = pics;
    }

    public String getStoryInfo() {
        return storyInfo;
    }

    public void setStoryInfo(String storyInfo) {
        this.storyInfo = storyInfo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getReadcount() {
        return readcount;
    }

    public void setReadcount(String readcount) {
        this.readcount = readcount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "StoryInfo{" +
                "id='" + id + '\'' +
                ", storyTime='" + storyTime + '\'' +
                ", pics=" + pics +
                ", storyInfo='" + storyInfo + '\'' +
                ", uid='" + uid + '\'' +
                ", lng='" + lng + '\'' +
                ", lat='" + lat + '\'' +
                ", city='" + city + '\'' +
                ", readcount='" + readcount + '\'' +
                ", comment='" + comment + '\'' +
                ", user=" + user +
                '}';
    }
}
