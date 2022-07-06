package com.rishiksahu.stockalertsmain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TextView;

import com.rishiksahu.stockalertsmain.R;

public class TnCActivity extends AppCompatActivity {

    private TextView mainTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tn_c);

        getSupportActionBar().setTitle("Terms and Conditions");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainTv = findViewById(R.id.mainTv);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mainTv.setText(Html.fromHtml(getString(R.string.tnc), Html.FROM_HTML_MODE_COMPACT));
        } else {
            mainTv.setText(Html.fromHtml(getString(R.string.tnc)));
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