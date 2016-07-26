package com.example.jeongsubin.myapplication;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import com.example.jeongsubin.myapplication.TrackData.LocalBinder;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
    private GoogleMap googleMap;
    Marker current_marker;
    double current_marker_lat;
    double current_marker_long;
    private GestureDetectorCompat gestureDetectorCompat;
    SQLiteDatabase db;
    String current_date = "";
    String[] commList={};
    TextView textview;
    ListView listview;
    TextView text_date;
    String snippet;
    ArrayAdapter<String> adapter;
    TrackData mService;
    boolean mBound = false;



    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        final Animation edit_up = AnimationUtils.loadAnimation(MainActivity.this, R.anim.drop_down);
        final Animation edit_down = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rise_up);
        final LinearLayout pin_comment = (LinearLayout) findViewById(R.id.pin_comment);
        final ImageButton commit = new ImageButton(MainActivity.this);
        final ImageButton marker_remover = new ImageButton(MainActivity.this);
        final EditText edit_text = new EditText(MainActivity.this);

        db = openOrCreateDatabase("Haru", MODE_PRIVATE, null);

        current_marker = null;
        try{
            db.execSQL("drop table Haru_marker"); //always delete existed Haru_comment table
        }
        catch (Exception e){
            System.out.println("Hello! There is no Haru_marker table.");
        }

        try {
            db.execSQL("create table Haru_marker(date TEXT, id integer, lat double, long double, comment TEXT);");//always create a new table named Haru_commnet

        } catch (Exception e) {
            System.out.println("Hello! Already Haru_marker table exists.");
        }

        try{
            db.execSQL("drop table Haru_track"); //always delete existed Haru_comment table
        }
        catch (Exception e){
            System.out.println("Hello! There is no Haru_track table.");
        }

        try {
            db.execSQL("create table Haru_track(date TEXT, id integer, lat double, long double, color integer);");

        } catch (Exception e) {
            System.out.println("Hello! Already Haru_track table exists.");
        }
        //testInsert();

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, false);

        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            System.out.println("Location is null");
        }


        LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }


            @Override
            public View getInfoContents(final Marker marker) {
                Toast.makeText(MainActivity.this, "comment", Toast.LENGTH_SHORT).show();
                edit_text.setId(R.id.edit_text);
                if (marker != current_marker && current_marker != null) {
                    edit_down.setAnimationListener(new AnimationListener());
                    pin_comment.removeView(edit_text);
                    pin_comment.removeView(commit);
                    pin_comment.removeView(marker_remover);
                    pin_comment.startAnimation(edit_down);
                }
                if (pin_comment.findViewById(R.id.edit_text) == null) {
                    current_marker = marker;
                    edit_text.setLayoutParams(new ViewGroup.LayoutParams(520, ViewGroup.LayoutParams.WRAP_CONTENT));
                    edit_text.setHint("your comment");
                    edit_text.setText(marker.getSnippet());
                    commit.setBackgroundResource(R.drawable.commit);
                    commit.setLayoutParams(new ViewGroup.LayoutParams(50, 50));
                    marker_remover.setBackgroundResource(R.drawable.remove);
                    marker_remover.setLayoutParams(new ViewGroup.LayoutParams(50, 50));

                    pin_comment.addView(edit_text);
                    pin_comment.addView(commit);
                    pin_comment.addView(marker_remover);

                    edit_up.setAnimationListener(new AnimationListener());
                    pin_comment.startAnimation(edit_up);
                    commit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "You click the pin", Toast.LENGTH_LONG).show();
                            snippet = String.valueOf(edit_text.getText());
                            marker.setSnippet(snippet);
                            String id_str = marker.getTitle().split(" ")[3];
                            int id = Integer.parseInt(id_str);
                            db_check(id, current_date);
                            edit_down.setAnimationListener(new AnimationListener());
                            pin_comment.removeView(edit_text);
                            pin_comment.removeView(commit);
                            pin_comment.removeView(marker_remover);
                            pin_comment.startAnimation(edit_down);
                            current_marker = null;
                        }
                    });
                    marker_remover.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(getApplicationContext(), "You remove the pin", Toast.LENGTH_LONG).show();
                            marker.remove();
                            String id_str = marker.getTitle().split(" ")[3];
                            int id = Integer.parseInt(id_str);
                            db_marker_rm(id, current_date);
                            pin_comment.removeView(edit_text);
                            pin_comment.removeView(commit);
                            pin_comment.removeView(marker_remover);
                            pin_comment.startAnimation(edit_down);
                            current_marker = null;
                        }
                    });
                }
                return null;
            }
        });
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMarkerClickListener(this);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        current_marker_lat = marker.getPosition().latitude;
        current_marker_long = marker.getPosition().longitude;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        current_marker_lat = marker.getPosition().latitude;
        current_marker_long = marker.getPosition().longitude;
        return false;
    }

    private final class AnimationListener implements
            Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SlidingPaneLayout slidingPaneLayout = (SlidingPaneLayout) findViewById(R.id.SlidingPanel);
        slidingPaneLayout.setSliderFadeColor(ContextCompat.getColor(this, android.R.color.transparent));

        commList= new String[50];


        long now = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date date = new Date(now);
        current_date = dateFormat.format(date);

        text_date = (TextView) findViewById(R.id.date_id);
        text_date.setText(current_date);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, commList);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @TargetApi(Build.VERSION_CODES.N)
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.make_pin:
                LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String provider = service.getBestProvider(criteria, false);
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    return;
                }
                Location location = service.getLastKnownLocation(provider);
                LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());
                Random r = new Random();
                googleMap.addMarker(new MarkerOptions()
                        .position(mylocation)
                        .draggable(true)
                        .title("marker id : "+ setmarkerID(current_date))
                        .snippet("What did you do??")
                        .icon(BitmapDescriptorFactory.defaultMarker(r.nextInt(250))));
                break;
            case R.id.left_btn:
                googleMap.clear();
                current_date = date_setting(current_date, -1);
                text_date.setText(current_date);
                Toast.makeText(this, "The day before", Toast.LENGTH_SHORT).show();
                mk_marker(current_date);
                mk_track(current_date);
                break;

            case R.id.right_btn:
                googleMap.clear();
                current_date = date_setting(current_date, 1);
                text_date.setText(current_date);
                Toast.makeText(this, "The day after", Toast.LENGTH_SHORT).show();
                mk_marker(current_date);
                mk_track(current_date);
                break;

            case R.id.today:
                googleMap.clear();
                long now = System.currentTimeMillis();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                Date date = new Date(now);
                current_date = dateFormat.format(date);
                text_date.setText(current_date);
                Toast.makeText(this, "Today", Toast.LENGTH_SHORT).show();
                mk_marker(current_date);
                mk_track(current_date);
                break;

        }

    }

    public String date_setting (String date, int day_ofs){
        String[] date_parse;
        date_parse = date.split("-");
        int day = Integer.parseInt(date_parse[2]) +day_ofs;
        int month = Integer.parseInt(date_parse[1]);
        int year = Integer.parseInt(date_parse[0]);
        if (day== 0){
            day=31;
            month--;
        }
        if(day>= 31){
            month++;
            day=1;
        }
        if(month > 12){
            year++;
            month=1;
        }
        if (month >9){
            if (day<10){
                return year+"-"+month+"-0"+day;
            }
            return year+"-"+month+"-"+day;
        }
        else {
            if (day < 10) {
                return year + "-0" + month + "-0" + day;
            }
            return year + "-0" + month + "-" + day;
        }
    }

    public void mk_track(String date){
        System.out.println(date);
        int i =0;
        //String sql = "select lat, long, color from Haru_track where date = \'" + date + "\' order by id ASC;";
        String sql = "select * from Haru_track where date = '" + date + "';";

        Cursor result =  db.rawQuery(sql, null);
        result.moveToFirst();

        double tlat = 0, tlong = 0, slat = 0, slong = 0;
        int tcolor = 0, scolor = 0;
        if (result.getCount() != 0) {
            tlat = result.getDouble(2);
            tlong = result.getDouble(3);
            tcolor = result.getInt(4);
            result.moveToNext();
        }

        while (!result.isAfterLast()) {

            //date TEXT, id integer, lat double, long double, color integer
            PolylineOptions line = new PolylineOptions();
            line.width(10);
            slat = result.getDouble(2);
            slong = result.getDouble(3);
            scolor = result.getInt(4);

            line.add(new LatLng(tlat, tlong), new LatLng(slat, slong));
            line.color(tcolor);
            googleMap.addPolyline(line);

            tlat = slat;
            tlong = slong;
            tcolor = scolor;
            result.moveToNext();
        }
        result.close();

    }


/*
    public void testInsert() {
        ContentValues insertValues = new ContentValues();
        String DATE = "date";
        String TIME = "id";
        String LONG = "long";
        String LAT = "lat";
        String COLOR = "color";

        insertValues.put(DATE, "2016-07-22");
        insertValues.put(TIME,003000);
        insertValues.put(LONG, -122.053248);
        insertValues.put(LAT, 37.00000);
        insertValues.put(COLOR,0x810038bd);
        db.insert("Haru_track", null, insertValues);

        insertValues.put(DATE, "2016-07-22");
        insertValues.put(TIME,004000);
        insertValues.put(LONG, -122.054234);
        insertValues.put(LAT, 36.99543);
        insertValues.put(COLOR,0xbabf3620);//5
        db.insert("Haru_track", null, insertValues);

        insertValues.put(DATE, "2016-07-22");
        insertValues.put(TIME,005000);
        insertValues.put(LONG, -122.054889);
        insertValues.put(LAT, 36.998789);
        insertValues.put(COLOR,0xc8842cbf);//6
        db.insert("Haru_track", null, insertValues);

        insertValues.put(DATE, "2016-07-22");
        insertValues.put(TIME,010000);
        insertValues.put(LONG, -122.055207);
        insertValues.put(LAT, 36.99822);
        insertValues.put(COLOR,0x601079be);//7
        db.insert("Haru_track", null, insertValues);

        insertValues.put(DATE, "2016-07-22");
        insertValues.put(TIME,011000);
        insertValues.put(LONG, -122.055291);
        insertValues.put(LAT, 36.997243);
        insertValues.put(COLOR,0x4678846b);//8
        db.insert("Haru_track", null, insertValues);

        insertValues.put(DATE, "2016-07-22");
        insertValues.put(TIME,012000);
        insertValues.put(LONG, -122.055239);
        insertValues.put(LAT, 36.996184);
        insertValues.put(COLOR,0x80f202a1);//9
        db.insert("Haru_track", null, insertValues);

    }

*/
    public void mk_marker(String date){
        for (int j=0;j<50;j++) {
            commList[j] = "";
        }
        listview = (ListView) findViewById(R.id.comment_list);
        int i =0;
        String sql = "select * from Haru_marker where date="+"'"+date+"'";
        Cursor result =  db.rawQuery(sql, null);
        result.moveToFirst();
        while (!result.isAfterLast()) {
            String s = result.getString(0) + '/' + result.getString(1)+'/' + result.getString(2)+'/' + result.getString(3)+ '/' + result.getString(4);
            Random r = new Random();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(result.getDouble(2), result.getDouble(3)))
                    .draggable(true)
                    .title("marker id : "+ result.getString(1))
                    .snippet(result.getString(4))
                    .icon(BitmapDescriptorFactory.defaultMarker(r.nextInt(250))));
            System.out.println(s);
            listview.setAdapter(adapter);
            commList[i] = result.getString(4);
            i++;
            result.moveToNext();
        }
        result.close();

    }
    public int setmarkerID(String date){
        String sql = "select * from Haru_marker where date="+"'"+date+"'";
        Cursor result =  db.rawQuery(sql, null);
        result.moveToFirst();
        int i =0;
        while (!result.isAfterLast()) {
            i++;
            result.moveToNext();
        }
        result.close();
        return i+1;
    }
    public void db_check(int id, String date){
        String sql = "select * from Haru_marker where id="+id+" and"+" date = '"+date+"'";
        Cursor result =  db.rawQuery(sql, null);
        result.moveToFirst();
        boolean new_one = true;
        while (!result.isAfterLast()) {
            String s = result.getString(0) + '/' + result.getString(1)+'/' + result.getString(2)+'/' + result.getString(3)+ '/' + result.getString(4);
            String exist_sql = "update Haru_marker set lat="+current_marker_lat+", long="+ current_marker_long + ", comment='" +snippet +"' where id="+id+" and"+" date = '"+date+"'";
            System.out.println("exist sql: "+ exist_sql);
            new_one = false;
            db.execSQL(exist_sql);
            result.moveToNext();
        }
        if(new_one == true){
            String new_sql = "insert into Haru_marker values( '" + current_date + "' ,"+ setmarkerID(current_date)+","+ current_marker_lat  +","+ current_marker_long + ","+ "'"+snippet+"' );";
            System.out.println("new sql : "+ new_sql);
            db.execSQL(new_sql);
        }
        result.close();
    }
    public void db_marker_rm(int id, String date){
        String sql = "delete from Haru_marker where id="+id+" and"+" date = '"+date+"'";
        db.execSQL(sql);
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
    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("database-update"));
    }

    // handler for received Intents for the "database-update" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String lat = intent.getStringExtra("lat");
            String lon = intent.getStringExtra("lon");
            String timefordata = intent.getStringExtra("time");

            int color = 0x810038bd;
            //String lon = extras.getString("lon");
            //String timefordata = extras.getString("time");
            insertData(current_date,timefordata,lon,lat,color);
            //Log.d("receiver", "Got message: " + message);
            Log.d("receiver", "Got message: " +lat);
            Log.d("reciever", "Got message: lat="+lat+"   lon="+lon+"  time="+timefordata);
        }
    };


    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public void insertData(String date, String time, String lon, String lat, int color){
        ContentValues insertValues = new ContentValues();
        String DATE = "date";
        String TIME = "id";
        String LONG = "long";
        String LAT = "lat";
        String COLOR = "color";

        double dlat = Double.parseDouble(lat);
        double dlon = Double.parseDouble(lon);
        Long dtime = Long.parseLong(time);



        insertValues.put(DATE, date);
        insertValues.put(TIME, dtime);
        insertValues.put(LONG, dlon);
        insertValues.put(LAT, dlat);
        insertValues.put(COLOR, color);
        db.insert("Haru_track", null, insertValues);
    }
}


