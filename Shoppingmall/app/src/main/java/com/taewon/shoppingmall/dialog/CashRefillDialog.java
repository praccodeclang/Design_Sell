package com.taewon.shoppingmall.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.taewon.shoppingmall.R;

public class CashRefillDialog extends Dialog {
    public CashRefillDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cash_fill);
    }
}
