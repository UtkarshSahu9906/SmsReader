package com.example.smsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsReceiver";
    private static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    private static final String FIRST = "sms_1";
    private static final String SECOND = "sms_2";
    private static final String THIRD = "sms_3";

    private SharedPreferences sharedPreferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED_ACTION)) {
            sharedPreferences = context.getSharedPreferences("my_sms_shared_prefs", Context.MODE_PRIVATE);

            Log.d(TAG, "onReceive: ");

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String messageBody = smsMessage.getMessageBody();
                        String sender = smsMessage.getOriginatingAddress();
                        Toast.makeText(context, "" + sender + ": " + messageBody, Toast.LENGTH_SHORT).show();
                        addNewSMS(messageBody);
                    }

                }
            }
        }
    }

    private void addNewSMS(String sms){
        String first = sharedPreferences.getString(FIRST,null);
        String second = sharedPreferences.getString(SECOND,null);
        String third = sharedPreferences.getString(THIRD,null);

        if (first == null) sharedPreferences.edit().putString(FIRST, sms).apply();
        else if (second == null) sharedPreferences.edit().putString(SECOND, sms).apply();
        else if (third == null) sharedPreferences.edit().putString(THIRD, sms).apply();
        else {
            sharedPreferences.edit().putString(FIRST, sharedPreferences.getString(SECOND,"")).apply();
            sharedPreferences.edit().putString(SECOND, sharedPreferences.getString(THIRD,"")).apply();
            sharedPreferences.edit().putString(THIRD,sms).apply();
        }
    }
}
