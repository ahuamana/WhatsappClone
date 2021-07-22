package com.paparazziteam.whatsappclone.services;

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

        if(title != null)
        {
            showNotification(title, body);
        }

    }

    private void showNotification(String title, String body) {

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        NotificationCompat.Builder builder = helper.getNotification(title,body);

        Random random = new Random();
        int numeroRam = random.nextInt(10000);

        //the id is the position of notifications on the smarthphone
        helper.getManager().notify(numeroRam,builder.build());
    }
}
