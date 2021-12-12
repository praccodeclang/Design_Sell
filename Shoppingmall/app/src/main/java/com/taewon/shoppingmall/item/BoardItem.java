package com.taewon.shoppingmall.item;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class BoardItem implements Serializable {
    public String uid;
    public String title;
    public String body;
    public String username;
    public List<String> tags;
    public int starCount;
    public String dateString;
    public Map<String, Boolean> likeUsers;
    public String boardID;
    public int price;
    public boolean isAuction;
    public String buyerUid;
    public Map<String, Object> comments;

    public BoardItem(){

    }

    public BoardItem(String uid, String username, String title, String body, List<String> tags, int price, boolean isAuction, String dateString){
        this.uid = uid;
        this.title = title;
        this.body = body;
        this.tags = tags;
        this.username = username;
        this.dateString = dateString;
        this.likeUsers = new HashMap<>();
        likeUsers.put("temp", true);
        starCount = 0;
        this.price = price;
        this.isAuction = isAuction;
        this.buyerUid = "";
        this.comments = new HashMap<>();
    }

    public Map<String, Object> getComments() {
        return comments;
    }

    public void setComments(Map<String, Object> comments) {
        this.comments = comments;
    }

    public Map<String, Boolean> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(Map<String, Boolean> likeUsers) {
        this.likeUsers = likeUsers;
    }

    public String getBoardID() {
        return boardID;
    }

    public void setBoardID(String boardID) {
        this.boardID = boardID;
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

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public boolean getIsAuction() {
        return isAuction;
    }

    public void setIsAuction(boolean isAuction) {
        this.isAuction = isAuction;
    }

    public String getBuyerUid() {
        return buyerUid;
    }

    public void setBuyerUid(String buyerUid) {
        this.buyerUid = buyerUid;
    }
}
