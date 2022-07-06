package com.rishiksahu.stockalertsmain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.rishiksahu.stockalertsmain.R;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class OrderConfirmActivity extends AppCompatActivity implements PaymentResultListener {
    
    private Button paymentBtn;
    private String description, amount, email, phone, subsTitle, amt, perks;
    private int intAmount, validity, planType;
    private ConstraintLayout paymentSuccessLayout;
    private TextView interestWarningTv;

    private TextView titleTv, validTv, perksTv, priceTv;
    public static int COMBO_PLAN = 2, CHART_ONLY_PLAN =1;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirm);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        paymentBtn = findViewById(R.id.continueBtnn);
        titleTv = findViewById(R.id.plan_title);
        validTv = findViewById(R.id.plan_validity);
        perksTv = findViewById(R.id.plan_features);
        priceTv = findViewById(R.id.plan_price);
        interestWarningTv = findViewById(R.id.interestWarningTV);
        paymentSuccessLayout = findViewById(R.id.payment_success_layout);
        firebaseFirestore = FirebaseFirestore.getInstance();

        final Checkout checkout = new Checkout();
        checkout.setKeyID(getString(R.string.production_rzp_apikey));
        checkout.setImage(R.drawable.logo_512);

        checkout.preload(getApplicationContext());

        if (getIntent() != null){
            subsTitle = getIntent().getStringExtra("subs_title");
            intAmount = getIntent().getIntExtra("subs_price", 50000);
            perks = getIntent().getStringExtra("subs_perks");
            validity = getIntent().getIntExtra("subs_validity", 30);
            planType = getIntent().getIntExtra("subs_type", COMBO_PLAN);
        }else {
            intAmount = 0;
        }
        amt = String.valueOf(intAmount) + "00";


        try{
            titleTv.setText(subsTitle);
            priceTv.setText("Rs. " + intAmount + "/-");
            validTv.setText("" + validity + " Days");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                perksTv.setText(Html.fromHtml(perks, Html.FROM_HTML_MODE_COMPACT));
            }else {
                perksTv.setText(Html.fromHtml(perks));
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        SharedPreferences prefs = getSharedPreferences("com.wealthcreatorpro.stockalerts.user", MODE_PRIVATE);
        final String phone = prefs.getString("user_email", "");
        final String email = prefs.getString("user_phone", "");


        paymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPayment(checkout, subsTitle,phone,email,amt);
            }
        });
    }

    private void startPayment(Checkout checkout, String subscriptionTitle, String phone, String email, String amount) {

        final Activity activity = this;

        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "Wealth Creator Pro");
            options.put("description", subscriptionTitle);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//            options.put("order_id", "order_DBJOWzybf0sJbb");//from response of step 3.
            options.put("theme.color", "#FFC107");
            options.put("currency", "INR");
            options.put("amount", amount);//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact",phone);
            checkout.open(activity, options);
        } catch(Exception e) {
            Log.e("payment Error: ", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {

        paymentSuccessLayout.setVisibility(View.VISIBLE);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR,validity);
        Timestamp validTime = new Timestamp(c.getTime());


        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("active_plan_name", subsTitle);
        if (planType == COMBO_PLAN){
            updateMap.put("notific_with_chart_subscription_validity", validTime);

        }else if (planType == CHART_ONLY_PLAN){
            updateMap.put("chart_only_subscription_validity", validTime);
        }
        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .update(updateMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    //subscription updated successfully
                    //user will automatically allowed to access materials on stockalerts restart

                    if (planType == COMBO_PLAN){
                        //Allow notifications and allow to see charts
                        //Show the interest Wrning

                        interestWarningTv.setVisibility(View.VISIBLE);

                    }else if (planType == CHART_ONLY_PLAN){
                        //Only allow to see charts.
                    }

                }else {
                    //payment deducted but subscription not updated, ask to contact support

                    Toast.makeText(OrderConfirmActivity.this, "Payment Deducted but service unavailable", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Failed! : " + s, Toast.LENGTH_SHORT).show();
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