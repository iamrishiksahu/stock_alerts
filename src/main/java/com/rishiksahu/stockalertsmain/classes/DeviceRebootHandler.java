package com.rishiksahu.stockalertsmain.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DeviceRebootHandler extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MyFirebaseMessagingService.class));
    }
}
