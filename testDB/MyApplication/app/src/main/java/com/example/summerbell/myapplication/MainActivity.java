package com.example.summerbell.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.*;
import android.view.View;
import android.database.sqlite.*;
import android.database.Cursor;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TestDataBaseActivity";
    private Database Haru_db;
    private Cursor mCursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText editId = (EditText) findViewById(R.id.edit_id);
        editId.setText("change");
        Haru_db = new Database(this);
        Haru_db.open();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.edit_add:
                //Data insert
                EditText getInfo = (EditText) findViewById(R.id.edit_info);
                Haru_db.insert(getInfo.getText().toString().trim());
                break;
        }
    }

}