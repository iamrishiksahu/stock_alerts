package com.rishiksahu.stockalertsmain.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rishiksahu.stockalertsmain.R;
import com.rishiksahu.stockalertsmain.classes.LocalDatabase;
import com.rishiksahu.stockalertsmain.classes.notification.NotificationModel;
import com.rishiksahu.stockalertsmain.classes.notification.NotificatiosAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity  {

    private RecyclerView recyclerView;
    private NotificatiosAdapter adapter;
    private TextView pastNotificationBtn;
    private ImageView backBtn;
    private DatePickerDialog datePickerDialog;

    private Calendar c = Calendar.getInstance();
    private int year = c.get(Calendar.YEAR);
    private int month = c.get(Calendar.MONTH);
    private int day = c.get(Calendar.DAY_OF_MONTH);
    private List<NotificationModel> notificationList;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.notiRecyvlerView);
        pastNotificationBtn = findViewById(R.id.pastNotificationBtn);
        getSupportActionBar().hide();

        notificationList = new ArrayList<>();
        backBtn = findViewById(R.id.backBtn);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);

        LocalDatabase db = LocalDatabase.getDbInstance(this);
        List<NotificationModel> list = db.notificationDao().getAllNotifications();
        Collections.reverse(list);

        adapter = new NotificatiosAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                String documentName = "" + i2 +"-" + (i1 +1) +"-" +i;
                firestore.collection("NOTIFICATIONS")
                        .document(documentName).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()){
                            DocumentSnapshot shot = task.getResult();
                            if (shot.contains("total")) {
                                Long total = (Long) shot.get("total");

                                notificationList.clear();

                                for (int x = 1; x <= total.intValue(); x++) {

                                    notificationList.add(new NotificationModel((String) shot.get("title_" + x)
                                            , (String) shot.get("body_" + x)
                                            , (String) shot.get("time_" + x)));

                                }

                                NotificatiosAdapter adapter = new NotificatiosAdapter(notificationList);
                                recyclerView.setAdapter(adapter);
                                adapter.notifyDataSetChanged();


                            }else {
                                Toast.makeText(NotificationsActivity.this, "Sorry! No Data Found!", Toast.LENGTH_SHORT).show();

                            }
                        }else {
                            Toast.makeText(NotificationsActivity.this, "Sorry! No Data Found!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }, year , month, day);
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
        c.add(Calendar.DAY_OF_MONTH, -7);
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());


        pastNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                datePickerDialog.show();

            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        else {
            return false;
        }
    }


}