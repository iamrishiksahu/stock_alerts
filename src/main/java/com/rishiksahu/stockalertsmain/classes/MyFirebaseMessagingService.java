package com.rishiksahu.stockalertsmain.classes;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rishiksahu.stockalertsmain.R;
import com.rishiksahu.stockalertsmain.activities.NotificationDisplayActivity;
import com.rishiksahu.stockalertsmain.classes.notification.NotificationModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String body, title,  dataTitle, dataBody, matchInterest, stockName,  timeFrame, exchangeName;
    private List<String> list = new ArrayList<>();

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        if(remoteMessage.getNotification() != null){

            // data from normal notification
            body = remoteMessage.getNotification().getBody();
            title = remoteMessage.getNotification().getTitle();

        }

        if (remoteMessage.getData() != null){
            //data from data payload of notification

            if (remoteMessage.getData().containsKey("interestValue")){
                matchInterest = remoteMessage.getData().get("interestValue");
            }
            if (remoteMessage.getData().containsKey("title")){
                dataTitle = remoteMessage.getData().get("title");
            }
            if (remoteMessage.getData().containsKey("body")){
                dataBody = remoteMessage.getData().get("body");
            }

        }


        //Check for the condition then decide whether to trigger notification or not.

        FirebaseFirestore.getInstance().collection("USERS").document(FirebaseAuth.getInstance().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    list = (List<String>) task.getResult().get("interests");

                    Timestamp subsValidity = (Timestamp) task.getResult().get("notific_with_chart_subscription_validity");
                    Timestamp currentTime = new Timestamp(new Date());

                    Long curr = currentTime.getSeconds();
                    Long vali = subsValidity.getSeconds();

                    if (vali > curr){
                        if (list.contains(matchInterest)){
                            triggerNotification(MyFirebaseMessagingService.this, dataTitle,  dataBody);
                            captureNotification(dataTitle, dataBody);
                        }else {
                            //Dont Trigger Notification

                        }
                    }else {
                        //Subscription Expired Dont Trigger Notification

                    }

                }

                else {

                }
            }
        });


    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("MyToken :", "recieved :" + s);
        System.out.println("token :" + s);
    }

    private void captureNotification(String title, String body){
        LocalDatabase db = LocalDatabase.getDbInstance(getApplicationContext());
        NotificationModel model = new NotificationModel();
        model.title = title;
        model.body = body;
        db.notificationDao().insertNotification(model);
    }

    private void triggerNotification(Context context, String title, String body){

        Random rand = new Random();
        int id = rand.nextInt(9999);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent notificationIntent = new Intent(this, NotificationDisplayActivity.class );
        notificationIntent.putExtra( "msg_title" , title);
        notificationIntent.putExtra( "msg_body" , body );
        notificationIntent.addCategory(Intent. CATEGORY_LAUNCHER ) ;
        notificationIntent.setAction(Intent. ACTION_MAIN ) ;
        notificationIntent.setFlags(Intent. FLAG_ACTIVITY_CLEAR_TOP | Intent. FLAG_ACTIVITY_SINGLE_TOP ) ;

        PendingIntent resultIntent = PendingIntent. getActivity (this, id, notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT) ;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID");
        builder.setSmallIcon(R.drawable.logo_512);
        builder.setContentTitle(title);
        builder.setContentText(body);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);
        builder.setChannelId("CHANNEL_ID");
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(defaultSoundUri);
        builder.setContentIntent(resultIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id , builder.build());
    }

}
