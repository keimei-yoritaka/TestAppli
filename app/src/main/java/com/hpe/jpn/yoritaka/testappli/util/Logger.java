package com.hpe.jpn.yoritaka.testappli.util;

import android.util.Log;

/**
 * Created by YoritakaK on 1/18/2017.
 */

public class Logger {
    public static final String TAG = "yoritaka";

    public static void v(String message) {
        Log.v(TAG, message);
    }
    public static void d(String message) {
        Log.d(TAG, message);
    }
    public static void i(String message) {
        Log.i(TAG, message);
    }
    public static void w(String message) {
        Log.w(TAG,message);
    }
    public static void w(String message, Throwable tr) {
        Log.w(TAG, message, tr);
    }
    public static void e(String message) {
        Log.e(TAG,message);
    }
    public static void e(String message, Throwable tr) {
        Log.e(TAG, message, tr);
    }

}
