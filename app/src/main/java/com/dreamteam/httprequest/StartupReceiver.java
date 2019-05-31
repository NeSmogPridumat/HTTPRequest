package com.dreamteam.httprequest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.dreamteam.httprequest.Service.EventService;

//запускается при запуске устройства, с помощью него запускаем сервис
public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartupReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            EventService.setServiceAlarm(context);
            Log.i(TAG, "Received broadcast intent");
        }

//        intent = new Intent(context, EventService.class);
//        context.startService(intent);
    }
}
