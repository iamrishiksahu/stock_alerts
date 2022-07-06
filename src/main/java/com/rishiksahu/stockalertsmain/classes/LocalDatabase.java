package com.rishiksahu.stockalertsmain.classes;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.rishiksahu.stockalertsmain.classes.notification.NotificationDao;
import com.rishiksahu.stockalertsmain.classes.notification.NotificationModel;

@Database(entities = {NotificationModel.class}, version =  1)
public abstract class LocalDatabase extends RoomDatabase {

    public abstract NotificationDao notificationDao();

    private static LocalDatabase INSTANCE;

    public static LocalDatabase getDbInstance(Context context){
        if (INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), LocalDatabase.class, "LOCAL_DATABASE")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
