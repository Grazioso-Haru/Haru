package com.example.jeongsubin.myapplication;

import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by LimJoowon on 2016. 7. 17..
 */
public class CommentActivity extends ActionBarActivity {

    private GestureDetectorCompat gestureDetectorCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView text2ndActivity = new TextView(this);
        text2ndActivity.setText("Second Activity");
        setContentView(text2ndActivity);

        gestureDetectorCompat = new GestureDetectorCompat(this, new My2ndGestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.gestureDetectorCompat.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class My2ndGestureListener extends GestureDetector.SimpleOnGestureListener {
        //handle 'swipe right' action only

        @Override
        public boolean onFling(MotionEvent event1, MotionEvent event2,
                               float velocityX, float velocityY) {


            if(event2.getX() > event1.getX()){
                Toast.makeText(getBaseContext(),
                        "Map",
                        Toast.LENGTH_SHORT).show();

                finish();
            }

            return true;
        }
    }

}
