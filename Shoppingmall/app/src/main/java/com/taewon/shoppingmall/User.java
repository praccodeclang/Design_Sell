package com.taewon.shoppingmall;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String username;
    public String email;
    public int coin;
    public boolean isDesigner;
    public String phone;

    public User(String username, String email, String phone, boolean isDesigner){
        this.username = username;
        this.email = email;
        this.coin = 0;
        this.phone = phone;
        this.isDesigner = isDesigner;
    }
}
