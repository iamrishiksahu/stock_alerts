package com.rishiksahu.stockalertsmain.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rishiksahu.stockalertsmain.R;

public class DisclaimerActivity extends AppCompatActivity {

    private TextView disclaimerTxt;
    private Button agreeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        disclaimerTxt = findViewById(R.id.mainTv);
        agreeBtn = findViewById(R.id.proceedBtn);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            disclaimerTxt.setText(Html.fromHtml(getString(R.string.starting_disclaimer), Html.FROM_HTML_MODE_COMPACT));
        } else {
            disclaimerTxt.setText(Html.fromHtml(getString(R.string.starting_disclaimer)));
        }

        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerintent = new Intent(DisclaimerActivity.this, LoginRegActivity.class);
                startActivity(registerintent);
                finish();
            }
        });
    }
}