package com.ygaps.travelapp.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ygaps.travelapp.FrontPage;
import com.ygaps.travelapp.LoginActivity;
import com.ygaps.travelapp.MyAPIClient;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.UserService;
import com.ygaps.travelapp.ViewInvitationActivity;
import com.ygaps.travelapp.pojo.FireBaseTokenRequest;
import com.ygaps.travelapp.pojo.Message;
import com.ygaps.travelapp.pojo.Tour;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseService";
    private boolean isInvitation=false;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        isInvitation= !remoteMessage.getData().isEmpty();
        // handle a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
        else {
            if(isInvitation){
                StringBuilder builder=new StringBuilder();
                Map<String,String> data = remoteMessage.getData();
                builder.append("You has been invited to tour ");
                try{
                    builder.append(data.get("name").toString().toUpperCase());
                }
                catch (Exception e){Log.d(TAG,"Null tour name");}
                builder.append(System.getProperty("line.separator"));


                sendNotification(builder.toString());
                Log.d(TAG, builder.toString());
            }
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        String firebaseToken = token;
        String deviceId = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Call<Message> call= MyAPIClient.buildHTTPClient().create(UserService.class).registerFirebaseToken(
                getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE).getString("token", ""),
                new FireBaseTokenRequest(firebaseToken,deviceId,1,"1.0"));
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {

            }
        });
        SharedPreferences.Editor edit=getSharedPreferences(LoginActivity.PREF_NAME, MODE_PRIVATE).edit();
        edit.putString("firebaseToken",firebaseToken);
        edit.apply();
    }

    private void sendNotification(String messageBody) {
        Intent intent;
        if(isInvitation)intent = new Intent(this, ViewInvitationActivity.class);
        else intent = new Intent(this, FrontPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.project_id);
        String title="Notification";
        if(isInvitation)title="Tour invitation";
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}

