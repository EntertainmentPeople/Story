package com.klj.story.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类
 */
public class Comment {

    private String id;
    private String comments;
    private String time;
    private String sid;
    private String uid;
    private String cid;
    private User user;
    private List<Comment> commentList=new ArrayList<>();

    public Comment() {
    }

    public Comment(String id, String comments, String time, String sid, String uid, String cid, User user) {
        this.id = id;
        this.comments = comments;
        this.time = time;
        this.sid = sid;
        this.uid = uid;
        this.cid = cid;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", comments='" + comments + '\'' +
                ", time='" + time + '\'' +
                ", sid='" + sid + '\'' +
                ", uid='" + uid + '\'' +
                ", cid='" + cid + '\'' +
                ", user=" + user +
                '}';
    }
}
