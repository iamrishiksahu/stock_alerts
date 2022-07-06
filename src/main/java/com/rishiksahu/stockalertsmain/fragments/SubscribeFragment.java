package com.rishiksahu.stockalertsmain.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.rishiksahu.stockalertsmain.R;
import com.rishiksahu.stockalertsmain.activities.OrderConfirmActivity;

public class SubscribeFragment extends Fragment {


    public SubscribeFragment() {
        // Required empty public constructor
    }

    private TextView descOneM, descThreeM, priceThreeM, priceOneM;
    private int oneMPrice = 999, threeMPrice = 1499, chartsThreeMPrice=49, validity1= 30, validity2=90, validity3=90;
    private Button subs3M, subs1M, subsCharts3M, goToYtBtn;
    private String title1= "ONE MONTH PLAN", title2 = "THREE MONTH PLAN", title3 = "CHARTS PLAN (3 MONTHS)";
    private int chartOnlyPlan = 1, comboPlan = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subscribe, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        descOneM = view.findViewById(R.id.subs_offer_one_month);
        descThreeM = view.findViewById(R.id.subs_offer_three_month);
        priceOneM = view.findViewById(R.id.price_oneM);
        priceThreeM = view.findViewById(R.id.price_threeM);

        subs1M = view.findViewById(R.id.one_subs_button);
        subs3M = view.findViewById(R.id.three_subs_button);
        goToYtBtn = view.findViewById(R.id.ytClickBtn);

        try {

            priceOneM.setText("Rs. " + oneMPrice + "/-");
            priceThreeM.setText("Rs. " + threeMPrice + "/-");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                descOneM.setText(Html.fromHtml(getString(R.string.one_month_offer), Html.FROM_HTML_MODE_COMPACT));
                descThreeM.setText(Html.fromHtml(getString(R.string.three_month_offer), Html.FROM_HTML_MODE_COMPACT));
            } else {
                descOneM.setText(Html.fromHtml(getString(R.string.one_month_offer)));
                descThreeM.setText(Html.fromHtml(getString(R.string.three_month_offer)));

            }
        }catch (Exception e){
            e.printStackTrace();
        }


        subs1M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callOrderConfirmActivity(title1, oneMPrice, getString(R.string.one_month_offer), validity1, comboPlan);

            }
        });

        subs3M.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callOrderConfirmActivity(title2, threeMPrice, getString(R.string.three_month_offer), validity2, comboPlan);
            }
        });


//        goToYtBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent youtubeIntent = new Intent(Intent.ACTION_VIEW);
//                youtubeIntent.setData(Uri.parse(getString(R.string.yt_channel_url)));
//                youtubeIntent.setPackage("com.google.android.youtube");
//                startActivity(youtubeIntent);
//            }
//        });

    }

    private void callOrderConfirmActivity(String sendTitle, int sendPRice, String perks, int validity, int type) {
        Intent cnfIntent = new Intent(getContext(), OrderConfirmActivity.class);
        cnfIntent.putExtra("subs_title",sendTitle);
        cnfIntent.putExtra("subs_price",sendPRice);
        cnfIntent.putExtra("subs_perks", perks);
        cnfIntent.putExtra("subs_validity", validity);
        cnfIntent.putExtra("subs_type", type);
        getActivity().startActivity(cnfIntent);
    }
}