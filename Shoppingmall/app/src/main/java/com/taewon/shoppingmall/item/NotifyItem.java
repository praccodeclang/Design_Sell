package com.taewon.shoppingmall.item;

public class NotifyItem {
    private String uid, notifyText, notifyTitle, dateString, kind, boardID;
    private boolean isRead;

    public NotifyItem(){

    }

    public NotifyItem(String uid, String notifyTitle, String notifyText, String dateString, String kind,boolean isRead){
        this.uid = uid;
        this.notifyTitle = notifyTitle;
        this.notifyText = notifyText;
        this.dateString = dateString;
        this.isRead = isRead;
        this.kind = kind;
        this.boardID = "";
    }

    public NotifyItem(String uid, String boardID, String notifyTitle, String notifyText, String dateString, String kind, boolean isRead){
        this.uid = uid;
        this.notifyTitle = notifyTitle;
        this.notifyText = notifyText;
        this.dateString = dateString;
        this.isRead = isRead;
        this.kind = kind;
        this.boardID = boardID;
    }

    public String getBoardID() {
        return boardID;
    }

    public void setBoardID(String boardID) {
        this.boardID = boardID;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public String getNotifyText() {
        return notifyText;
    }

    public void setNotifyText(String notifyText) {
        this.notifyText = notifyText;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public boolean getIsRead() {
        return this.isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }
}
