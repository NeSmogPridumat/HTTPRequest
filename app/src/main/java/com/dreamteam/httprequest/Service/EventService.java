package com.dreamteam.httprequest.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Event.Entity.ChangeEvent.EventChoice;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class EventService extends Service implements OutputHTTPManagerInterface {

    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();

    //Ключ, по кторому приложение будет ловить широковещательный интент передаваемый сервисом
    public final String BROADCAST_ACTION = "com.dreamteam.httprequest";

    public EventService() {
    }

    //метод для привязки приложения к сервису
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if (intent != null) {
            if (!(QueryPreferences.getUserIdPreferences(this).equals(""))) {
                final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER
                        + httpConfig.EVENT + httpConfig.USER
                        + httpConfig.USER_ID_PARAM
                        + QueryPreferences.getUserIdPreferences(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        httpManager.getRequest(path, constantConfig.GET_EVENT_TYPE,
                                EventService.this);
                    }
                }).start();
            }
        }
        //возращаемый int дает указания, что делать если система убила сервис (в данном случае попытаться восстановить работу)
        return START_STICKY;
    }

    @Override
    public void response(byte[] byteArray, String type) {
        if (type.equals(constantConfig.GET_EVENT_TYPE)){
            prepareGetEventsResponse(byteArray);
        }
    }

    private void prepareGetEventsResponse (byte[] byteArray){
        Log.e("AAAAAJKF:F:KA:KJFAAAAA", String.valueOf(byteArray));
        if (byteArray != null){
            final ArrayList<EventChoice> eventArrayList;
            try {
                eventArrayList = createEventsOfBytes(byteArray);
                int newEventCount = eventArrayList.size();
                int count = 0;

                //получаем последний результат запроса
                String lastResultString = QueryPreferences.getLastResultId(this);
                Type type = new TypeToken<ArrayList<EventChoice>>(){}.getType();
                ArrayList<EventChoice> lastResultArray = new Gson().fromJson(lastResultString, type);

                //проверяем наличие новых эвентов
                for (int i = 0; i < eventArrayList.size(); i++ ){
                    for(int j = 0; j < lastResultArray.size(); j++){
                        if (eventArrayList.get(i).id.equals(lastResultArray.get(j).id)){
                            count++;
                            newEventCount--;
                        }
                    }
                }

                //если есть новые эвенты выкидываем уведомление
                if(count != eventArrayList.size()){
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentTitle("Новых уведомлений " + newEventCount);
                    builder.setContentText("Опаньки!");
                    builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                    Notification notification = builder.build();

                    NotificationManager notificationManager =
                            (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);
                }

                //запускаем интент на поиски нашего Broadcast. Если приложение активное, то интент дойдет в MainActivity
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra("event", eventArrayList.size());
                sendBroadcast(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //сохраняем полученный результат для сравнения с последующими
            QueryPreferences.setLastResult(this, new String(byteArray));
        }
    }

    private ArrayList<EventChoice> createEventsOfBytes (byte[] byteArray)throws Exception{
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<String> list = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        ArrayList<EventChoice> events = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++){
            if(!(jsonArray.get(i).equals("null"))) {
                EventChoice event = gson.fromJson((list.get(i)), new TypeToken<EventChoice>() {}.getType());
                if (event.active) {
                    events.add(event);
                }
            }
        }
        return events;
    }

    @Override
    public void error(Throwable t) {
    }

    @Override
    public void errorHanding(int resposeCode, String type) {
    }

    @Override
    public void onDestroy() {
        //при вызове stopService() вызывается onDestroy() сервиса, но вызовы продолжают идти из-за того, что
        // "поток продолжает работать". Чтобы закрыть поток и прекратить запросы на сервер, надо вызвать у handler
//        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    public static Intent newIntent(Context context){
        return new Intent(context, EventService.class);
    }

    public static void setServiceAlarm(Context context){
        Intent i = EventService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //TODO книга
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 6000, pi);
    }
}
