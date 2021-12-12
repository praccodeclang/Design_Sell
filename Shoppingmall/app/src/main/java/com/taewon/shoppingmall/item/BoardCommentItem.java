package com.taewon.shoppingmall.item;

public class BoardCommentItem {
    String uid;
    String username;
    String comment;
    String dateString;
    String commentID;
    String boardID;

    public BoardCommentItem(String uid, String username, String comment, String dateString){
        this.uid = uid;
        this.username = username;
        this.comment = comment;
        this.dateString = dateString;
    }

    public BoardCommentItem(){

    }

    public String getBoardID() {
        return boardID;
    }

    public void setBoardID(String boardID) {
        this.boardID = boardID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
