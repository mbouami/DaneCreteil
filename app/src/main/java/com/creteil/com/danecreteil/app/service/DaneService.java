package com.creteil.com.danecreteil.app.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.creteil.com.danecreteil.app.AccueilActivity;
import com.creteil.com.danecreteil.app.VillesFragment;

import static android.content.ContentValues.TAG;

/**
 * Created by Mohammed on 16/12/2016.
 */

public class DaneService extends IntentService  {
    private final String LOG_TAG = DaneService.class.getSimpleName();
    private static final Object sSyncAdapterLock = new Object();
    private static DaneServiceAdapter sDaneServiceAdapter = null;

    public DaneService(String name) {
        super(name);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sDaneServiceAdapter.getSyncAdapterBinder();
    }

    public DaneService() {
        super("DaneService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


//    @Override
//    public void onCreate() {
//        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
//        synchronized (sSyncAdapterLock) {
//            if (sDaneServiceAdapter == null) {
//                sDaneServiceAdapter = new DaneServiceAdapter(getApplicationContext(), true);
//            }
//        }
//    }
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return sDaneServiceAdapter.getSyncAdapterBinder();
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(LOG_TAG, "onHandleIntent - DaneService : "+ intent.getStringExtra(AccueilActivity.ETAT_BASE));
        synchronized (sSyncAdapterLock) {
            if (sDaneServiceAdapter == null) {
                sDaneServiceAdapter = new DaneServiceAdapter(getApplicationContext(), true);
            }
        }
    }
}
