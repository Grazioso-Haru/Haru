package timotongo.haru;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
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
    public TrackData(){
    }

    @Override
    public void onCreate() {
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1, this);
        //start timer
        startTime = System.nanoTime();
    }
    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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
        Location curLoc = mLocationManager.getLastKnownLocation();
        LatLng mylocation = new LatLng(curLoc.getLatitude(), curLoc.getLongitude());
        long timeAtLoc = System.nanoTime()-startTime;
	   startTime = System.nanoTime();s
        //log at database
        syncData(timeAtLoc,mylocation);
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
