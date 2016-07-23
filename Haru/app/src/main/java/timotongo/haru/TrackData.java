package timotongo.haru;

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
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Timothy on 7/10/2016.
 * data base pw : harsubhakjootimmit22
 */
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

    public TrackData() {
    }

    @Override
    public void onCreate() {
        getLocation();
        startTime = System.nanoTime();
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    public void getLocation(){
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        bestProvider = String.valueOf(mLocationManager.getBestProvider(criteria, true)).toString();
        // Currently confirms location changed every 20 min / 13 meters (40ish feet)
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1200000, 13, this);

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
    public void syncData(long number, LatLng loc){
        //store to database LatLng and time spent there

    }
}
