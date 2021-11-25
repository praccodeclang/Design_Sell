package com.taewon.shoppingmall.util;

public class FirebaseUtil {
    public static FirebaseUtil _instance = null;
    private FirebaseUtil(){

    }
    public static FirebaseUtil getFirebaseUtil(){
        if(_instance == null){
            _instance = new FirebaseUtil();
        }
        return _instance;
    }
}
