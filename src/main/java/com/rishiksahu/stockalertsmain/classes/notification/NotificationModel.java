package com.rishiksahu.stockalertsmain.classes.notification;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Entity
public class NotificationModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "body")
    public String body;

    @ColumnInfo(name = "timestamp")
    public String time = getTime();

    private String getTime(){
        String timeStamp = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a").format(new Date());
        return timeStamp;
    }

    public NotificationModel(String title, String body, String time) {
        Random rand = new Random();
        int id = rand.nextInt(999999);
        this.id = id;
        this.title = title;
        this.body = body;
        this.time = time;
    }

    public NotificationModel(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
