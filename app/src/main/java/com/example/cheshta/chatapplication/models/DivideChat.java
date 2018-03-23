package com.example.cheshta.chatapplication.models;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chesh on 2/8/2018.
 */

public class DivideChat extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
/*
        Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this, Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/
    }
}
