package com.hpe.jpn.yoritaka.testappli;

import android.*;
import android.Manifest;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.hpe.jpn.yoritaka.testappli.util.Logger;

public class MainActivity extends AppCompatActivity {
    private Button btn_ok; // = (Button) findViewById(R.id.btn_ok);
    private Button btn_register;
    private TextView textView;
    private boolean isReceiverRegistered;
    private BroadcastReceiver regBroadcastReceiver;
    private BroadcastReceiver msgBroadcastReceiver;

    private MyAudioRecord myAudioRecord;
    private MyMediaRecorder myMediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Logger.i("******************* Start apps!! **************************");

        Logger.i(" Check permission.");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Logger.i("**** RecordAudio is denied.");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Logger.i("**** ActivityCompat.shouldShowRequestPermissionRationale Yes.");
            } else {
                Logger.i("**** ActivityCompat.shouldShowRequestPermissionRationale No.");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }
        }else {
            Logger.i("**** RecordAudio is granted.");
        }

        textView = (TextView) findViewById(R.id.textView);

        Logger.i("My working directory is " + getFilesDir());
        myAudioRecord = new MyAudioRecord();
        myMediaRecorder = new MyMediaRecorder();


        regBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(MyPreferences.SENT_REG_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    String regId = sharedPreferences.getString(MyPreferences.REGISTRATION_ID, "NOT_FOUND");
                    textView.setText("Registration (" + regId + ")was completed successfully");
                } else {
                    textView.setText("Registration was failed");
                }
            }
        };
        msgBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                Intent newMsgIntent = new Intent(context, NewMsgAlertActivity.class);
                String message = intent.getStringExtra("message");
                Logger.i("msgBroadCastReceiver::onReceive(), new message=" + message);
                newMsgIntent.putExtra("message", message);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newMsgIntent, 0);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            }
        };

        /** 着信通知 **/
        TelephonyManager tManager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);

        tManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        // 着信 //
                        Logger.i("call state is ringing");
                        /*
                        Intent newCall = new Intent(MyPreferences.NEW_MSG_COMING);
                        newCall.putExtra("message", "CALL : Ringing");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(newCall);
                        */
                        // 自動でOffHook
                        Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        btnDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK
                        ));
                        Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                        btnUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));


                        getApplicationContext().sendOrderedBroadcast(btnDown,"android.permission.CALL_PRIVILEGED");
                        getApplicationContext().sendOrderedBroadcast(btnUp,"android.permission.CALL_PRIVILEGED");
                        Logger.i("Now automatic hand off!");
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Logger.i("Call state is offhook");
                        Intent offhook = new Intent(MyPreferences.NEW_MSG_COMING);
                        offhook.putExtra("message", "CALL : Off hook");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(offhook);

                        //if (!myAudioRecord.isRecording()) {
                        //    Logger.i("MyAudioRecord is being prepared now.");
                        //    myAudioRecord.initAudioRecord();
                        //    Logger.i("Starting audio record");
                        //    myAudioRecord.startAudioRecord();
                        //}

                        if(!myMediaRecorder.isRecording()) {
                            Logger.i("MyMediaRecorder start recording");
                            myMediaRecorder.startMediaRecord();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        Logger.i("Call state is idle.");
                        Intent callIdle = new Intent(MyPreferences.NEW_MSG_COMING);
                        callIdle.putExtra("message", "CALL : Idle");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(callIdle);
                        //if (myAudioRecord.isRecording()) {
                        //    myAudioRecord.stopAudioRecord();
                        //}
                        if (myMediaRecorder.isRecording()) {
                            myMediaRecorder.stopMediaRecord();
                        }
                        break;
                    default:
                        Logger.i("Call state was changed to other status=" + state);


                }
            }

        }, PhoneStateListener.LISTEN_CALL_STATE);

        registerReceiver();

        btn_ok = (Button) findViewById(R.id.btn_ok);

        btn_ok.setOnClickListener(new OkButtonClickListener());

        if (checkPlayServices()) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(regBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void registerReceiver() {
        if(!isReceiverRegistered) {
            Logger.i("Register two broadcast receiver!");
            LocalBroadcastManager.getInstance(this).registerReceiver(regBroadcastReceiver, new IntentFilter(MyPreferences.REGISTRATION_COMPLETE));
            LocalBroadcastManager.getInstance(this).registerReceiver(msgBroadcastReceiver, new IntentFilter(MyPreferences.NEW_MSG_COMING));
            isReceiverRegistered = true;
        }
    }
    /**
     * Google Play Serviceの利用可否判定
     * @return
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000).show();
            } else {
                Logger.i("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    class OkButtonClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            EditText et = (EditText) findViewById(R.id.et_name);
            TextView textView = (TextView) findViewById(R.id.textView);

            textView.setText(et.getText());

            if (!myAudioRecord.isRecording()) {
                Logger.i("MyAudioRecord is being prepared now.");
                myAudioRecord.initAudioRecord();
                Logger.i("Starting audio record");
                myAudioRecord.startAudioRecord();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 0 :
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Logger.i("Permission is granted!!");
                } else {
                    Logger.i("User denied your permission request.");
                }
                break;
        }

    }
}
