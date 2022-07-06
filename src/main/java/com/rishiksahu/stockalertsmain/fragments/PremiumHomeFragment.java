package com.rishiksahu.stockalertsmain.fragments;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rishiksahu.stockalertsmain.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PremiumHomeFragment extends Fragment {

   public PremiumHomeFragment() {
        // Required empty public constructor
    }

    private WebView webView;
    private String loadURL;
    private ProgressBar loading, initialLoading;
    private ConstraintLayout unsubsConstLayout;

    private FirebaseFirestore firebaseFirestore;

    private boolean isChartAccessible = false;
    private String activePlanTitle = "";
    private Timestamp comboValidity, chartsValidity;
    private static List<String> interestListFirst = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_premium_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loading = view.findViewById(R.id.loadingBar);
        initialLoading = view.findViewById(R.id.initialLoadingBar);
        firebaseFirestore = FirebaseFirestore.getInstance();


        loadURL = getString(R.string.premium_home_web_url);

        initialLoading.setVisibility(View.VISIBLE);



        webView = view.findViewById(R.id.mainPremiumWebView);
        unsubsConstLayout = view.findViewById(R.id.unsubs_constraint_layout);

        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setInitialScale(90);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webView.canGoBack()) {
                    webView.goBack();
                    return true;
                }
                return false;
            }
        });

        firebaseFirestore.collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    comboValidity = (Timestamp) task.getResult().get("notific_with_chart_subscription_validity");
                    chartsValidity = (Timestamp) task.getResult().get("chart_only_subscription_validity");

                    Timestamp currentTime = new Timestamp(new Date());

                    Long today = currentTime.getSeconds();
                    Long combo = comboValidity.getSeconds();
                    Long charts = chartsValidity.getSeconds();

                    if (combo > today){
                        //subscribed for notifications + charts
                        isChartAccessible = true;
                    }else if (charts > today){
                        //subscribed for charts

                        isChartAccessible = true;
                    }else {
                        //subscription expired

                        isChartAccessible = false;
                    }

                    if (isChartAccessible){
                        unsubsConstLayout.setVisibility(View.GONE);
                        try {
                            webView.loadUrl(loadURL);
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Something Went Wrong! : " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }else {
                        //chart is not accessible to user since subscription has expired.
                        unsubsConstLayout.setVisibility(View.VISIBLE);

                    }


                }else {
                    Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });




    }



    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            initialLoading.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loading.setVisibility(View.GONE);


        }
    }
}