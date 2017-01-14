package com.creteil.com.danecreteil.app.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.creteil.com.danecreteil.app.AccueilActivity;
import com.creteil.com.danecreteil.app.R;
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
        synchronized (sSyncAdapterLock) {
            Toast.makeText(this, "sSyncAdapterLock ", Toast.LENGTH_LONG).show();
            if (sDaneServiceAdapter == null) {
                Toast.makeText(this, "sDaneServiceAdapter ", Toast.LENGTH_LONG).show();
                sDaneServiceAdapter = new DaneServiceAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting "+intent.getStringExtra(AccueilActivity.ETAT_BASE)+"---"+flags+"----"+startId, Toast.LENGTH_LONG).show();
//        synchronized (sSyncAdapterLock) {
//            Toast.makeText(this, "onStartCommand ", Toast.LENGTH_LONG).show();
//            if (sDaneServiceAdapter == null) {
//                Toast.makeText(this, "sDaneServiceAdapter ", Toast.LENGTH_LONG).show();
//                sDaneServiceAdapter = new DaneServiceAdapter(getApplicationContext(), true);
//            }
//        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        Notification notification = new Notification.Builder(this)
//                .setContentTitle("Truiton Music Player")
//                .setTicker("Truiton Music Player")
//                .setContentText("My Music")
//                .setSmallIcon(android.R.drawable.ic_popup_sync)
//                .setContentIntent(pendingIntent)
//                .setTicker(getText(R.string.action_mail))
//                .build();
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                android.R.drawable.ic_dialog_dialer);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(getText(R.string.action_departement))
                .setContentText(getText(R.string.message_version))
//                    .setSmallIcon(R.drawable.icon)
                .setSmallIcon(android.R.drawable.ic_popup_sync)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentIntent(pendingIntent)
                .setTicker(getText(R.string.action_mail))
                .build();

//        Notification notification = new Notification.Builder(this)
//                .setContentTitle(getText(R.string.action_departement))
//                .setTicker("Truiton Music Player")
//                .setContentText(getText(R.string.message_version))
//                .setSmallIcon(android.R.drawable.ic_popup_sync)
//                .setLargeIcon(
//                        Bitmap.createScaledBitmap(icon, 128, 128, false))
//                .setContentIntent(pendingIntent)
//                .setOngoing(true)
//                .addAction(android.R.drawable.ic_media_previous,
//                        "Previous", pendingIntent)
//                .addAction(android.R.drawable.ic_media_play, "Play",
//                        pendingIntent)
//                .addAction(android.R.drawable.ic_media_next, "Next",
//                        pendingIntent).build();
        startForeground(13568, notification);
        return super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
        super.onDestroy();
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
        Toast.makeText(this, "onHandleIntent - DaneService : "+intent.getStringExtra(AccueilActivity.ETAT_BASE), Toast.LENGTH_SHORT).show();
        try {
            Toast.makeText(this, "service sleep 0 "+intent.getStringExtra(AccueilActivity.ETAT_BASE), Toast.LENGTH_SHORT).show();
            Thread.sleep(5000);
            Toast.makeText(this, "service sleep 1 "+intent.getStringExtra(AccueilActivity.ETAT_BASE), Toast.LENGTH_SHORT).show();
        synchronized (sSyncAdapterLock) {
            Toast.makeText(this, "onHandleIntent ", Toast.LENGTH_LONG).show();
            if (sDaneServiceAdapter == null) {
                sDaneServiceAdapter = new DaneServiceAdapter(getApplicationContext(), true);
            }
        }
        } catch (InterruptedException e) {
            // Restore interrupt status.
            Thread.currentThread().interrupt();
        }
    }
}
