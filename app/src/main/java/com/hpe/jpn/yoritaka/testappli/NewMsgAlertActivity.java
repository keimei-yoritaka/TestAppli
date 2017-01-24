package com.hpe.jpn.yoritaka.testappli;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hpe.jpn.yoritaka.testappli.util.Logger;

public class NewMsgAlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_new_msg_alert);

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //String message = intent.getStringExtra("message");
        Logger.i("message will be gotten from intent.");
        String message = intent.getExtras().getString("message");
        Logger.i("message will be gotten from intent. " + message);
        Bundle bundle = new Bundle();
        Logger.i("=== 0 ====");
        bundle.putString("AlertMessage", message);
        Logger.i(message + " will be transfered to AlertFragment.");

        NewMsgAlertFragment fragment = new NewMsgAlertFragment();
        fragment.setArguments(bundle);
        //fragment.show(getSupportFragmentManager(), "alert_dialog");
        FragmentManager mgr = getFragmentManager();
        fragment.show(mgr, message);
        //fragment.show();
    }
}
