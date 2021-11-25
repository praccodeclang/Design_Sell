package com.taewon.shoppingmall;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String username;
    public String email;
    public int coin;
    public boolean isDesigner;
    public String phone;
    public String photoUrl;
    public String uid;
    public User(){

    }
    public User(String uid, String username, String email, String phone, String photoUrl, boolean isDesigner){
        this.uid = uid;
        this.username = username;
        this.email = email;
        this.coin = 0;
        this.phone = phone;
        this.isDesigner = isDesigner;
        this.photoUrl = photoUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public boolean isDesigner() {
        return isDesigner;
    }

    public void setDesigner(boolean designer) {
        isDesigner = designer;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
