package com.app.dwit;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class DWIT extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
    }
}
