package com.taewon.shoppingmall.util;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.taewon.shoppingmall.item.BoardItem;

import java.util.ArrayList;

public class FirebaseUtil {
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;

    private FirebaseUtil(){
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
    }
    private static FirebaseUtil _instance = null;
    public static FirebaseUtil getFirebaseUtil(){
        if(_instance == null){
            _instance = new FirebaseUtil();
        }
        return _instance;
    }
}
