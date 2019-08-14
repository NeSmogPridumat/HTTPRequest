package com.dreamteam.httprequest.Service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.dreamteam.httprequest.Data.ConstantConfig;
import com.dreamteam.httprequest.Event.Entity.ChangeEvent.EventChoice;
import com.dreamteam.httprequest.Data.HTTPConfig;
import com.dreamteam.httprequest.Group.Entity.GroupData.Group;
import com.dreamteam.httprequest.HTTPManager.HTTPManager;
import com.dreamteam.httprequest.Interfaces.OutputHTTPManagerInterface;
import com.dreamteam.httprequest.Invation.InvitationInteractor.InvitationInteractor;
import com.dreamteam.httprequest.QueryPreferences;
import com.dreamteam.httprequest.R;
import com.dreamteam.httprequest.Service.Data.NotificationHelper.NotificationHelper;
import com.dreamteam.httprequest.Service.Data.SocketEvent.DataEvent;
import com.dreamteam.httprequest.Service.Data.SocketEvent.SocketEvent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONArray;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EventService extends Service implements ForServiceInterface {

    private HTTPManager httpManager = HTTPManager.get();
    private HTTPConfig httpConfig = new HTTPConfig();
    private ConstantConfig constantConfig = new ConstantConfig();
    private InvitationInteractor invitationInteractor = new InvitationInteractor(this, this);
    NotificationHelper helper;

    String CHANNEL_ID = "com.dreamteam.httprequest";

    WebSocket ws;

    //Ключ, по кторому приложение будет ловить широковещательный интент передаваемый сервисом
    public final String BROADCAST_ACTION = "com.dreamteam.httprequest";

    public EventService(){
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
                final String path = httpConfig.serverURL + httpConfig.SERVER_GETTER + httpConfig.INVITATION;
//                        + httpConfig.EVENT + httpConfig.USER
//                        + httpConfig.USER_ID_PARAM
//                        + QueryPreferences.getUserIdPreferences(this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            helper = new NotificationHelper(EventService.this);
                        }
                        searchNetwork();
//                        boolean check = socket.isConnected();
//                        Log.i("Connection", String.valueOf(check));
//                        httpManager.getRequest(path, constantConfig.GET_EVENT_TYPE,
//                                EventService.this);
                    }
                }).start();
            }
        }
        //возращаемый int дает указания, что делать если система убила сервис (в данном случае попытаться восстановить работу)
        return START_STICKY;
    }

//    @Override
//    public void response(byte[] byteArray, String type) {
//        if (type.equals(constantConfig.GET_EVENT_TYPE)) {
//            prepareGetEventsResponse(byteArray);
//        }
//    }

    private void prepareGetEventsResponse(byte[] byteArray) {
        Log.e("AAAAAJKF:F:KA:KJFAAAAA", String.valueOf(byteArray));
        if (byteArray != null) {
            final ArrayList<EventChoice> eventArrayList;
            try {
                eventArrayList = createEventsOfBytes(byteArray);
                int newEventCount = eventArrayList.size();
                int count = 0;

                //получаем последний результат запроса
                String lastResultString = QueryPreferences.getLastResultId(this);
                Type type = new TypeToken<ArrayList<EventChoice>>() {
                }.getType();
                ArrayList<EventChoice> lastResultArray = new Gson().fromJson(lastResultString, type);

                //проверяем наличие новых эвентов
                for (int i = 0; i < eventArrayList.size(); i++) {
                    for (int j = 0; j < lastResultArray.size(); j++) {
                        if (eventArrayList.get(i).id.equals(lastResultArray.get(j).id)) {
                            count++;
                            newEventCount--;
                        }
                    }
                }

                //если есть новые эвенты выкидываем уведомление
                if (count != eventArrayList.size()) {
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

    private ArrayList<EventChoice> createEventsOfBytes(byte[] byteArray) throws Exception {
        Gson gson = new Gson();
        String jsonString = new String(byteArray);
        JSONArray jsonArray = new JSONArray(jsonString);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            list.add(jsonArray.getString(i));
        }

        ArrayList<EventChoice> events = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (!(jsonArray.get(i).equals("null"))) {
                EventChoice event = gson.fromJson((list.get(i)), new TypeToken<EventChoice>() {
                }.getType());
                if (event.active) {
                    events.add(event);
                }
            }
        }
        return events;
    }

//    @Override
//    public void error(Throwable t) {
//    }
//
//    @Override
//    public void errorHanding(int responseCode, String type) {
//    }

    @Override
    public void onDestroy() {
        //при вызове stopService() вызывается onDestroy() сервиса, но вызовы продолжают идти из-за того, что
        // "поток продолжает работать". Чтобы закрыть поток и прекратить запросы на сервер, надо вызвать у handler
//        handler.removeCallbacksAndMessages(null);

        super.onDestroy();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, EventService.class);
    }

    public static void setServiceAlarm(Context context) {
        Intent i = EventService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

//        TODO книга
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 0, pi);
    }

    private String DeviceName = "Device";

    private boolean searchNetwork() {

        try {
            ws = new WebSocketFactory().createSocket("ws://192.168.1.177:"  + httpConfig.SERVER_SETTER + "/ws?token="+ QueryPreferences.getToken(getApplicationContext()));
            ws.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) {
                    // Received a text message.
                    Log.i("EventService", message);
                    Gson gson = new Gson();
                    SocketEvent socketEvent =  gson.fromJson(message, new TypeToken<SocketEvent>(){}.getType());
                    if((socketEvent.data.eventType).equals("groupInvite")){
                        invitationInteractor.getInvation(socketEvent.data.data.objectID);
                    } else if ((socketEvent.data.eventType).equals("subGroupCreatingRequest")){
                        invitationInteractor.activationSubGroup(socketEvent.data.data.objectID);
                    } else if ((socketEvent.data.eventType).equals("groupRatingEvent")){
                        invitationInteractor.ratingGroup(socketEvent.data.data.objectID);
                    } else if ((socketEvent.data.eventType).equals("groupDiscussionEvent")){
                        invitationInteractor.discussionGroup(socketEvent.data.data.objectID);
                    }

                    //TODO:присылать id последнего уведомления
                }

                @Override
                public void onError(WebSocket websocket, WebSocketException cause) {
                    Log.i("EventService", "onerror");
                }

                @Override
                public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                    Log.i("EventService", "onConError");
                }

                @Override
                public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                    Log.i("EventService", "Connected");
                    createNotificationChannel(DeviceName, BROADCAST_ACTION);
                }

                @Override
                public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {
                    Log.i("EventService", "onSendError");
                }

                @Override
                public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                    Log.i("EventService", "onDisconnected");
                    searchNetwork();
                }

                @Override
                public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {
                    Log.i("EventService", "onMessageError");

                }

                @Override
                public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
                    super.onPongFrame(websocket, frame);
                    websocket.sendPing();
                }
            });
            ws.connect();
            Log.i("EventService", "connect");

        } catch (Exception e) {
            Log.i("EventService", e.getMessage());
        }

        return false;

        }

    private void createNotificationChannel(String title, String discription) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = title;
            String description = discription;
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void answerSubGroupCreatingRequest(Group group) {
//        Notification notification =
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Создание подгруппы");
        builder.setContentText("В группе " + group.personal.descriptive.title + " появились новые подгруппы");
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }

    @Override
    public void answerInvitation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = helper.getChannelNotification("Приглашение", "Вас приглашают в группу");
            helper.getManager().notify(new Random().nextInt(), builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Приглашение");
            builder.setContentText("Вас приглашают в группу");
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            Notification notification = builder.build();

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(1, notification);
        }

    }

    @Override
    public void answerRatingEvent(Group group) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = helper.getChannelNotification("Голосование", "В группе " + group.personal.descriptive.title + " началось голосование");
            helper.getManager().notify(new Random().nextInt(), builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Голосование")
                    .setContentText("В группе " + group.personal.descriptive.title + " новое объявление")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.mipmap.ic_launcher);
            builder.setSound(Settings.System.DEFAULT_RINGTONE_URI);

            Notification notification = builder.build();

            NotificationManagerCompat mNotificationManager =
                    NotificationManagerCompat.from(this);

            mNotificationManager.notify(1, notification);
        }
        }

    @Override
    public void answerDiscussionEvent(Group group) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder builder = helper.getChannelNotification("Объявление", "В группе " + group.personal.descriptive.title + " новое объявление");
            helper.getManager().notify(new Random().nextInt(), builder.build());
        } else {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Голосование")
                    .setContentText("В группе " + group.personal.descriptive.title + " новое объявление")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.mipmap.ic_launcher);
            builder.setSound(Settings.System.DEFAULT_RINGTONE_URI);

            Notification notification = builder.build();

            NotificationManagerCompat mNotificationManager =
                    NotificationManagerCompat.from(this);

            mNotificationManager.notify(1, notification);
        }
    }

}
