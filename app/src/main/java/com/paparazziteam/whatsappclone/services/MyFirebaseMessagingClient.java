package com.paparazziteam.whatsappclone.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.paparazziteam.whatsappclone.channel.NotificationHelper;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    //Servira para enviar token de notificaciones de dispositivo a dispositivo
    @Override
    public void onNewToken(@NonNull @NotNull String s) {
        super.onNewToken(s);
    }


    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData(); // in data will get all information when we send a notification

        String title = data.get("title");
        String body = data.get("body");
        String idNotification = data.get("idNotification");

        if(title != null)
        {
            if(title.equals("MENSAJE"))
            {
                showNotificationMessage(data);
            }else
            {
                showNotification(title, body, idNotification);
            }


        }

    }

    private void showNotification(String title, String body, String idNotification) {

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        NotificationCompat.Builder builder = helper.getNotification(title,body);

        //Random random = new Random();
        //int numeroRam = random.nextInt(10000);

        int id = Integer.parseInt(idNotification);

        Log.e("NOTIFICATION","ID:" + id);
        //the id is the position of notifications on the smarthphone
        helper.getManager().notify(id,builder.build());
    }

    private void showNotificationMessage(Map<String, String> data) {

        String body = data.get("body");
        String idNotification = data.get("idNotification");
        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");

        NotificationHelper helper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = helper.getNotificationMessage(usernameSender,usernameReceiver,body);

        //Random random = new Random();
        //int numeroRam = random.nextInt(10000);

        int id = Integer.parseInt(idNotification);

        Log.e("NOTIFICATION","ID:" + id);
        Log.e("NOTIFICATION","usernameSender:" + usernameSender);
        Log.e("NOTIFICATION","usernameReceiver:" + usernameReceiver);
        //the id is the position of notifications on the smarthphone
        helper.getManager().notify(id,builder.build());
    }
}
