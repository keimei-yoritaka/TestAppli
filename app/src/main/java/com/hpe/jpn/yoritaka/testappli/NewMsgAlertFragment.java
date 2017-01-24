package com.hpe.jpn.yoritaka.testappli;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.hpe.jpn.yoritaka.testappli.util.Logger;

/**
 * Created by YoritakaK on 1/13/2017.
 */

public class NewMsgAlertFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        //bundle.putString("AlertMessage", message);
        Logger.i("Get alermessage from bundle.");
        String message = bundle.getString("AlertMessage");
        Logger.i("received message is " + message);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Dialog dialog = builder.setTitle("通知").setMessage(message).create();
        //savedInstanceState.get
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().finish();
    }
}
