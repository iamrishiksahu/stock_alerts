package com.rishiksahu.stockalertsmain.classes.notification;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface NotificationDao {

    @Query("SELECT * FROM notificationmodel")
    List<NotificationModel> getAllNotifications();

    @Insert
    void insertNotification(NotificationModel... notificationModels);

    @Delete
    void deleteNotification(NotificationModel notificationModel);


}
