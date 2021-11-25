package com.taewon.shoppingmall.item;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.List;

@IgnoreExtraProperties
public class BoardItem {
    public String uid;
    public String title;
    public String body;
    public String username;
    public List<String> imgs;
    public List<String> tags;
    public int starCount;
    public BoardItem(){

    }
    public BoardItem(String uid, String username,String title, String body, List<String> imgs, List<String> tags){
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.imgs = imgs;
        this.tags = tags;
        this.username = username;
        starCount = 0;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public List<String> getImgs() {
        return imgs;
    }

    public void setImgs(List<String> imgs) {
        this.imgs = imgs;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }
}
