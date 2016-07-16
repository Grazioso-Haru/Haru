package com.example.jeongsubin.myapplication;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //LatLng mylocation = new LatLng();
    private GoogleMap googleMap;


    public void onMapReady(final GoogleMap map) {
        googleMap = map;

        final Animation edit_up = AnimationUtils.loadAnimation(MainActivity.this, R.anim.drop_down);
        final Animation edit_down = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rise_up);
        final LinearLayout pin_comment = (LinearLayout) findViewById(R.id.pin_comment);
        final ImageButton commit = new ImageButton(MainActivity.this);
        final EditText edit_text = new EditText(MainActivity.this);

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

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        Location location= locationManager.getLastKnownLocation(locationProvider);
        if(location == null){
            System.out.println("Subin 12  NULL!!!!!!!!");
        }

        LatLng mylocation = new LatLng(location.getLatitude(), location.getLongitude());

        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                                           @Override
                                           public View getInfoWindow(Marker marker) {
                                               //Toast.makeText(MainActivity.this, "getinfo", Toast.LENGTH_SHORT).show();
                                               return null;
                                           }

                                           @Override
                                           public View getInfoContents(final Marker marker) {
                                               Toast.makeText(MainActivity.this, "comment", Toast.LENGTH_SHORT).show();
                                               edit_text.setId(R.id.edit_text);
                                               if (pin_comment.findViewById(R.id.edit_text) ==null) {
                                                   edit_text.setLayoutParams(new ViewGroup.LayoutParams(600, ViewGroup.LayoutParams.WRAP_CONTENT));
                                                   edit_text.setHint("hide");
                                                   edit_text.setText(marker.getSnippet());
                                                   //commit.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                                   commit.setBackgroundResource(R.drawable.right);
                                                   commit.setLayoutParams(new ViewGroup.LayoutParams(50, 50));

                                                   pin_comment.addView(edit_text);
                                                   pin_comment.addView(commit);

                                                   edit_up.setAnimationListener(new AnimationListener());
                                                   pin_comment.startAnimation(edit_up);
                                                   commit.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View view) {
                                                           Toast.makeText(getApplicationContext(),"You click pin",Toast.LENGTH_LONG).show();
                                                           marker.setSnippet(String.valueOf(edit_text.getText()));

                                                           edit_down.setAnimationListener(new AnimationListener());
                                                           pin_comment.removeView(edit_text);
                                                           pin_comment.removeView(commit);
                                                           pin_comment.startAnimation(edit_down);
                                                       }
                                                   });
                                                   return null;
                                               }
                                               googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                                   @Override
                                                   public void onMapClick(LatLng latLng) {
                                                       edit_down.setAnimationListener(new AnimationListener());
                                                       pin_comment.removeView(edit_text);
                                                       pin_comment.removeView(commit);
                                                       pin_comment.startAnimation(edit_down);
                                                   }
                                               });
                                               return null;
                                           }
                                       });

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
        // ------- //
        //googleMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener)this);
    }
    private final class AnimationListener implements
            Animation.AnimationListener{

        @Override
        public void onAnimationStart(Animation animation) {
            //Toast.makeText(getApplicationContext(),"You click pin",Toast.LENGTH_LONG).show();
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
            /*case R.id.btn:
                EditText txt = (EditText) findViewById(R.id.text_id);
                String text = txt.getText().toString();
                marker1.setSnippet(text);
                Toast.makeText(this, "1st pushed", Toast.LENGTH_SHORT).show();
                break;*/
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
}
