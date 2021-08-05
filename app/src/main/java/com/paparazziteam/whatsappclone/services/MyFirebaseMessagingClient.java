package com.paparazziteam.whatsappclone.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.common.collect.BiMap;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.channel.NotificationHelper;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.receivers.ResponseReceiver;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {


    public static final String NOTIFICATION_REPLY = "NotificationReply";

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

        Log.e("NOTIFICATION","title:" + title);

        if(title != null)
        {


            if(title.equals("MENSAJE"))
            {
                Log.e("NOTIFICATION","Mensaje con imagen");
                getImageReceiver(data);
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

    private void getImageReceiver(Map<String, String> data)
    {
        String imageReceiver = data.get("imageReceiver");

        Log.e("NOTIFICATION","imageReceiver:" + imageReceiver);

        if(imageReceiver == null)
        {
            showNotificationMessage(data,null);
            return;
        }

        if(imageReceiver.equals(""))
        {
            showNotificationMessage(data,null);
            return;
        }

        //download image into bitmap
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageReceiver)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {

                        showNotificationMessage(data, resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);

                        showNotificationMessage(data, null);

                    }
                });
    }

    private void showNotificationMessage(Map<String, String> data, Bitmap bitmapReceiver) {

        String body = data.get("body");
        String idNotification = data.get("idNotification");
        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");
        String messagesJSON = data.get("messagesJSON");


        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        //Action on Notifications
        Intent intentResponse = new Intent(this, ResponseReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,1,intentResponse,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        ////Data for action on notifications
        NotificationCompat.Action actionResponse = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        NotificationCompat.Builder builder = helper.getNotificationMessage(messages, usernameSender, usernameReceiver, bitmapReceiver,actionResponse);

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
