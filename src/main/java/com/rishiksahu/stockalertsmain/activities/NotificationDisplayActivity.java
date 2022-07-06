package com.rishiksahu.stockalertsmain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.rishiksahu.stockalertsmain.R;

public class NotificationDisplayActivity extends AppCompatActivity {

    private String title, body;
    private TextView titleTv, bodyTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification_display);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Notification");

        titleTv = findViewById(R.id.titleTv);
        bodyTv = findViewById(R.id.bodyTv);

        if (getIntent() != null) {

            title = getIntent().getStringExtra("msg_title");
            body = getIntent().getStringExtra("msg_body");

            try {
                titleTv.setText(title);
                bodyTv.setText(body);
            }catch (Exception e){
                e.printStackTrace();
                Toast.makeText(this, "Sorry, An Error Occurred!", Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home){
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


}