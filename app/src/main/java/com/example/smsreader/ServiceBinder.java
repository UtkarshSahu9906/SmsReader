package com.example.smsreader;

import android.os.Binder;

public class ServiceBinder extends Binder {
    ForegroundService serviceContext;

    public ServiceBinder(ForegroundService serviceContext) {
        this.serviceContext = serviceContext;
    }

    public ForegroundService getService() {
        return this.serviceContext;
    }
}
