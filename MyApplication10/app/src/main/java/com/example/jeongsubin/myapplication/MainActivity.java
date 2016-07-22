package com.example.jeongsubin.myapplication;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
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

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            return;
        }
        googleMap.setMyLocationEnabled(true);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location location = locationManager.getLastKnownLocation(locationProvider);
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
                break;

            case R.id.right_btn:
                googleMap.clear();
                current_date = date_setting(current_date, 1);
                text_date.setText(current_date);
                Toast.makeText(this, "The day after", Toast.LENGTH_SHORT).show();
                mk_marker(current_date);
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
}


