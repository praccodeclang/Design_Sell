package com.taewon.shoppingmall.item;

public class AdsItem {
    private String url, imgRef;
    public AdsItem(String imgRef, String url){
        this.url = url;
        this.imgRef = imgRef;
    }

    public String getUrl() {
        return url;
    }

    public String getImgRef() {
        return imgRef;
    }
}
