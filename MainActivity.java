package timotongo.haru;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.database.sqlite.*;
import android.app.Service;
import timotongo.haru.TrackData.LocalBinder;

import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;



public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //LatLng mylocation = new LatLng();
    TrackData mService;
    boolean mBound = false;
    private GoogleMap googleMap;
    Marker marker1;
    Marker marker2;
    Marker marker3;

    public void onMapReady(final GoogleMap map) {
        googleMap = map;
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
        googleMap.setMyLocationEnabled(true);
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = service.getBestProvider(criteria, false);
        Location location = service.getLastKnownLocation(provider);

        LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng mylocation2 = new LatLng(location.getLatitude() + 0.001, location.getLongitude());
        LatLng mylocation3 = new LatLng(location.getLatitude() + 0.002, location.getLongitude());

        marker1 = googleMap.addMarker(new MarkerOptions()
                .position(mylocation)
                .draggable(true)
                .title("the first marker")
                .snippet("What did you do??")
                .icon(BitmapDescriptorFactory.defaultMarker(0)));

        marker2 = googleMap.addMarker(new MarkerOptions()
                .position(mylocation2)
                .draggable(true)
                .snippet("What did you do??")
                .title("the second marker")
                .icon(BitmapDescriptorFactory.defaultMarker(100)));
        marker3 = googleMap.addMarker(new MarkerOptions()
                .position(mylocation3)
                .draggable(true)
                .title("the third marker")
                .snippet("What did you do??")
                .icon(BitmapDescriptorFactory.defaultMarker(200)));


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        long now = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault());
        Date date = new Date(now);
        String strDate = dateFormat.format(date);
        TextView text_date = (TextView) findViewById(R.id.date_id);
        text_date.setText(strDate);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn:
                EditText txt = (EditText) findViewById(R.id.text_id);
                String text = txt.getText().toString();
                marker1.setSnippet(text);
                Toast.makeText(this, "1st pushed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn2:
                TextView txt2 = (TextView) findViewById(R.id.text_id2);
                String text2 = txt2.getText().toString();
                marker2.setSnippet(text2);
                Toast.makeText(this, "2nd pushed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn3:
                TextView txt3 = (TextView) findViewById(R.id.text_id3);
                String text3 = txt3.getText().toString();
                marker3.setSnippet(text3);
                Toast.makeText(this, "3rd pushed", Toast.LENGTH_SHORT).show();
                break;
            case R.id.make_pin:
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = service.getBestProvider(criteria, false);
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
                Location location = service.getLastKnownLocation(provider);
                LatLng mylocation = new LatLng(location.getLatitude(),location.getLongitude());
                Random r= new Random();
                googleMap.addMarker(new MarkerOptions()
                        .position(mylocation)
                        .draggable(true)
                        .title("You add a pin")
                        .snippet("What did you do??")
                        .icon(BitmapDescriptorFactory.defaultMarker(r.nextInt(250))));
                break;
            case R.id.left_btn:
                Toast.makeText(this, "Yesterday", Toast.LENGTH_SHORT).show();
                break;
            case R.id.right_btn:
                Toast.makeText(this, "Tomorrow", Toast.LENGTH_SHORT).show();
                break;

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Bind to TrackData
        Intent intent = new Intent(this, TrackData.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop(){
        super.onStop();
        if (mBound){
            unbindService(mConnection);
            mBound = false;
        }
    }
    public void onButtonClick(View v) {
        if (mBound){
            // TODO Call A method from the track data (Sync the data)
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBound = false;
        }
    };


    /**
    public final class FeedReaderContract {
        public FeedReaderContract() {}

        public static abstract class FeedEntry implements BaseColumns {
            public static final String COLUMN_NAME_ENTRY_ID = "entryid";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_SUBTITLE = "subtitle";
            public static final String COLUMN_NAME_ENTRY_ID = "entryid";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_SUBTITLE = "subtitle";
            public static final String COLUMN_NAME_ENTRY_ID = "entryid";
            public static final String COLUMN_NAME_TITLE = "title";
            public static final String COLUMN_NAME_SUBTITLE = "subtitle";
        }
    }

 */


}
