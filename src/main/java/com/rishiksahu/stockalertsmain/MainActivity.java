package com.rishiksahu.stockalertsmain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rishiksahu.stockalertsmain.activities.LoginRegActivity;
import com.rishiksahu.stockalertsmain.activities.NotificationsActivity;
import com.rishiksahu.stockalertsmain.activities.TnCActivity;
import com.rishiksahu.stockalertsmain.classes.TabbedViewPagerAdapter;
import com.rishiksahu.stockalertsmain.fragments.AccountFragment;
import com.rishiksahu.stockalertsmain.fragments.PremiumHomeFragment;
import com.rishiksahu.stockalertsmain.fragments.SingupFragment;
import com.rishiksahu.stockalertsmain.fragments.SubscribeFragment;

public class MainActivity extends AppCompatActivity {

    private TabbedViewPagerAdapter viewPagerAdapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView notificationBtn;
    private ActionBarDrawerToggle toggle;
    public static DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Dialog supportDialog;
    private FirebaseUser currentUser;

    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        notificationBtn = findViewById(R.id.notificationBtn);

        firebaseFirestore = FirebaseFirestore.getInstance();
//        navigationView.getMenu().getItem(0).setChecked(true);

        Toolbar toolbar = findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        tabLayout = findViewById(R.id.mainTabLayout);
        viewPager = findViewById(R.id.mainViewPager);

        createNotificationChannel();

        subscribeToTopic();

        viewPagerAdapter = new TabbedViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new PremiumHomeFragment(), "Analyse");
        viewPagerAdapter.addFragment(new SubscribeFragment(), "Subscribe");
        viewPagerAdapter.addFragment(new AccountFragment(), "Account");
        viewPager.setAdapter(viewPagerAdapter);

        //to stop reloading the fragments on swipe action (tell the view pager how many fragments do you have)
        //following is the code.
        viewPager.setOffscreenPageLimit(3);
        tabLayout.setupWithViewPager(viewPager);

        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.parseColor("#FF6F00")));
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            MenuItem menuItem;
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                menuItem = item;

                if(currentUser!=null){

                    if (supportDialog!=null){
                        supportDialog.dismiss();
                    }

                    switch (menuItem.getItemId()) {
                        case R.id.nav_homeFragment:
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

//                        case R.id.nav_how_to_use:
//                            Intent how = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/rIiha3YCRTg"));
//                            how.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            how.setPackage("com.google.android.youtube");
//                            startActivity(how);
//                            break;
//
//                        case R.id.nav_tut2:
//                            Intent aye = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/w-r9YVwY7ug"));
//                            aye.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            aye.setPackage("com.google.android.youtube");
//                            startActivity(aye);
//                            break;
//
//                        case R.id.nav_tut3:
//                            Intent bye = new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/UuOurrai2kY"));
//                            bye.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            bye.setPackage("com.google.android.youtube");
//                            startActivity(bye);
//                            break;


                        case R.id.nav_help:

                            supportDialog = new Dialog(MainActivity.this);
                            supportDialog.setContentView(R.layout.support_layout);
                            supportDialog.setCancelable(true);
                            supportDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.rounded_corner_button_layout));
                            supportDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                            drawerLayout.closeDrawer(GravityCompat.START);

                            supportDialog.show();



                            break;

                        case R.id.nav_tnc:

                            Intent tncIntent = new Intent(MainActivity.this, TnCActivity.class);
                            SingupFragment.fromSignUpFragment = false;

                            drawerLayout.closeDrawer(GravityCompat.START);
                            startActivity(tncIntent);
                            break;

                        case R.id.nav_share:
                            Intent myIntent = new Intent(Intent.ACTION_SEND);
                            myIntent.setType("text/plain");
                            String body = "https://play.google.com/store/apps/details?id=com.pratikwaghmare.stockalertsmain";
                            String sub = "";
                            myIntent.putExtra(Intent.EXTRA_SUBJECT,sub);
                            myIntent.putExtra(Intent.EXTRA_TEXT,body);
                            drawerLayout.closeDrawer(GravityCompat.START);
                            startActivity(Intent.createChooser(myIntent, "Share Using"));
                            break;

                        case R.id.nav_logout:


                            FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                                    .update("isLoggedIn", false).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Successfully Logged Out!", Toast.LENGTH_SHORT).show();
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(MainActivity.this, LoginRegActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Could Not Log Out! :" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            drawerLayout.closeDrawer(GravityCompat.START);
                            break;

                    }

                    return true;
                }else {
                    return false;
                }

            }
        });

        notificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent notiIntent = new Intent(MainActivity.this, NotificationsActivity.class);

                startActivity(notiIntent);

            }
        });


    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();

            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            CharSequence name = "General";
            String description = "General Notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name,
                    importance);
            channel.setDescription(description);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(defaultSoundUri, audioAttributes);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    //todo: change the subscribe to topic method to production

    private void subscribeToTopic(){
        FirebaseMessaging.getInstance().subscribeToTopic("allLoggedIn").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                }else {

                }
            }
        });
    }
//    private void subscribeToTopic(){
//        FirebaseMessaging.getInstance().subscribeToTopic("testing123").addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()){
//
//                }else {
//
//                }
//            }
//        });
//    }



    @Override
    protected void onStart() {
        super.onStart();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }
}