package com.hpe.jpn.yoritaka.testappli;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.hpe.jpn.yoritaka.testappli.util.Logger;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by YoritakaK on 1/11/2017.
 */

public class RegistrationIntentService extends IntentService {

    public RegistrationIntentService() {
        super("RegistrationIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Logger.i("GCM Registration token: " + token);
            sendRegistrationToServer(token);
            sharedPreferences.edit().putBoolean(MyPreferences.SENT_REG_TOKEN_TO_SERVER, true).apply();
            sharedPreferences.edit().putString(MyPreferences.REGISTRATION_ID, token).apply();
        } catch (Exception e) {
            Logger.e("Failed to complete token refresh", e);
            sharedPreferences.edit().putBoolean(MyPreferences.SENT_REG_TOKEN_TO_SERVER, false).apply();
        }

        Intent registrationComplete = new Intent(MyPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private static final String REGISTRAR_URL = "http://180.42.35.71:14080/register/?REG_TOKEN=";
    private void sendRegistrationToServer(String token) {
        //TODO
        try {
            HttpURLConnection connection;
            URL url = new URL(REGISTRAR_URL + token);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setInstanceFollowRedirects(false);
            connection.connect();

            InputStream is = connection.getInputStream();
            //String pageData = is.
            is.close();
        } catch (Exception e) {
            Logger.e("HTTPClient error", e);
        }
    }
}
