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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.collect.BiMap;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.channel.NotificationHelper;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.providers.MessageProvider;
import com.paparazziteam.whatsappclone.receivers.ResponseReceiver;
import com.paparazziteam.whatsappclone.receivers.StatusReceiver;

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
                getImageSender(data);
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

    private void getImageSender(Map<String, String> data)
    {
        String imageSender = data.get("imageSender");

        Log.e("NOTIFICATION","imageSender:" + imageSender);

        if(imageSender == null)
        {
            showNotificationMessage(data,null);
            return;
        }

        if(imageSender.equals(""))
        {
            showNotificationMessage(data,null);
            return;
        }

        //download image into bitmap
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(imageSender)
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


        String tokenSender = data.get("tokenSender");
        String tokenReceiver = data.get("tokenReceiver");

        String body = data.get("body");
        String idNotification = data.get("idNotification");
        String usernameSender = data.get("usernameSender");
        String usernameReceiver = data.get("usernameReceiver");
        String messagesJSON = data.get("messagesJSON");
        String imageSender = data.get("imageSender");
        String imageReceiver = data.get("imageReceiver");
        int id = Integer.parseInt(idNotification);

        String idChat = data.get("idChat");
        String idSender = data.get("idSender");
        String idReceiver = data.get("idReceiver");


        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        updateStatus(messages);

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        //Action on Notifications
        Intent intentResponse = new Intent(this, ResponseReceiver.class);
        intentResponse.putExtra("idNotification", id);
        intentResponse.putExtra("messages", messagesJSON);
        intentResponse.putExtra("usernameSender", usernameSender);
        intentResponse.putExtra("usernameReceiver", usernameReceiver);
        intentResponse.putExtra("imageSender", imageSender);
        intentResponse.putExtra("imageReceiver", imageReceiver);
        intentResponse.putExtra("idChat", idChat);
        intentResponse.putExtra("idSender", idSender);
        intentResponse.putExtra("idReceiver", idReceiver);
        intentResponse.putExtra("tokenSender", tokenSender);
        intentResponse.putExtra("tokenReceiver", tokenReceiver);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,id,intentResponse,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        ////Data for action on notifications
        NotificationCompat.Action actionResponse = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();


        //Action on Notifications status
        Intent intentStatus = new Intent(this, StatusReceiver.class);
        intentStatus.putExtra("messages",messagesJSON);


        PendingIntent pendingIntentStatus = PendingIntent.getBroadcast(this,id,intentStatus,PendingIntent.FLAG_UPDATE_CURRENT);

        ////Data for action on Notifications status
        NotificationCompat.Action actionStatus = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Marcar como leido",
                pendingIntentStatus).build();

        Intent chatIntent = new Intent( getApplicationContext(), ChatActivity.class);
        chatIntent.putExtra("idUser", idSender);
        chatIntent.putExtra("idChat", idChat);

        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(),id,chatIntent,PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = helper.getNotificationMessage(
                messages,
                "",
                usernameSender,
                bitmapReceiver,
                null,
                actionResponse,
                actionStatus,
                contentIntent);

        //Random random = new Random();
        //int numeroRam = random.nextInt(10000);



        Log.e("NOTIFICATION","ID:" + id);
        Log.e("NOTIFICATION","usernameSender:" + usernameSender);
        Log.e("NOTIFICATION","usernameReceiver:" + usernameReceiver);
        //the id is the position of notifications on the smarthphone
        helper.getManager().notify(id,builder.build());
    }

    private void updateStatus(Message[] messages)
    {
        MessageProvider messageProvider = new MessageProvider();

        for (Message m: messages)
        {
            messageProvider.getMessagesById(m.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    if(documentSnapshot.exists())
                    {
                        Message myMessage = documentSnapshot.toObject(Message.class);

                        if(!myMessage.getStatus().equals("VISTO"))
                        {
                            messageProvider.updateStatus(myMessage.getId(),"RECIBIDO");
                        }
                    }

                }
            });



        }
    }
}
