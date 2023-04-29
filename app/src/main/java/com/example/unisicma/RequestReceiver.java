package com.example.unisicma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RequestReceiver extends BroadcastReceiver {
    public static String receivedText;
    @Override
    public void onReceive(Context context, Intent intent) {
        receivedText = intent.getStringExtra("com.example.unisicma.REQUEST_TEXT");
        Toast.makeText(context,receivedText,Toast.LENGTH_LONG).show();

    }
}
