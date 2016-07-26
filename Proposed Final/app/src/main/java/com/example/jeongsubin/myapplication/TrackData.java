package com.example.jeongsubin.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Timothy on 7/10/2016.
 **/
public class TrackData extends Service implements LocationListener {
    LocationManager mLocationManager;
    long startTime = 0;
    private IBinder mBinder = new LocalBinder();
    public static final String TAG = "lat";
    public static final String TAG2 = "lon";
    public static final String TAG3 = "time";
    public double curLat;
    public double curLon;
    public Criteria criteria;
    public String bestProvider;
    int[] colorArr;

    public TrackData() {
    }

    @Override
    public void onCreate() {
        getLocation();
        startTime = System.nanoTime();
        Gradient g = new Gradient();
        g.setGradient(0xff00ffff, 0xff000080, 144);
        colorArr = g.getColorArray();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public void getLocation() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(mLocationManager.getBestProvider(criteria, true)).toString();
        // Currently confirms location changed every 20 min / 13 meters (40ish feet)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 36000000, 3, this);
        //value is changed to debug 1check/10hour 3m

    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return false;
    }

    public class LocalBinder extends Binder {
        TrackData getService() {
            return TrackData.this;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Log time at location(and location)
        //start new log

        curLat = location.getLatitude();
        curLon = location.getLongitude();
        long timeAtLoc = System.nanoTime()-startTime;
        startTime = System.nanoTime();
        Log.d(TAG, String.valueOf(curLat));
        Log.d(TAG2, String.valueOf(curLon));
        Log.d(TAG3, String.valueOf(timeAtLoc));
        sendMessage(String.valueOf(curLat),String.valueOf(curLon),String.valueOf(timeAtLoc));
        //log at database
        //syncData(timeAtLoc,mylocation);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public int timeToInt(String t){
        int hour = Integer.parseInt(t.substring(0, 2));
        int min = Integer.parseInt(t.substring(3, 5));
        int ampm;
        if (t.substring(6).equals("오후")) {
            ampm = 1;
        }
        else {
            ampm = 0;
        }
        return hour * 6 + min / 10 + ampm * 72;
    }
    private void sendMessage(String lat, String lon, String time) {
        Intent intent = new Intent("database-update");
        //Bundle extras = new Bundle();
        // add data
        //extras.putString("lat", "lat");
        //extras.putString("lon", lon);
        //extras.putString("time", time);
        intent.putExtra("lat",lat);
        intent.putExtra("lon",lon);
        intent.putExtra("time",time);

        SimpleDateFormat dateFormat = new SimpleDateFormat("KK mm a", java.util.Locale.getDefault());
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String d = dateFormat.format(date);
        int index=timeToInt(d);

        intent.putExtra("color", colorArr[index]);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
