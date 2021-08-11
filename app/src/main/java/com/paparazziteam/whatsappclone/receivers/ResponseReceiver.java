package com.paparazziteam.whatsappclone.receivers;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.channel.NotificationHelper;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.providers.MessageProvider;
import com.paparazziteam.whatsappclone.providers.NotificationProvider;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.paparazziteam.whatsappclone.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class ResponseReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

       getMyImage(context,intent);
    }

    private void showNotification(Context context, Intent intent, Bitmap myBitmap) {

        String message = getMessageText(intent).toString();

        int id = intent.getExtras().getInt("idNotification");
        String messagesJSON = intent.getExtras().getString("messages");
        String usernameSender = intent.getExtras().getString("usernameSender");
        String usernameReceiver = intent.getExtras().getString("usernameReceiver");

        String imageSender = intent.getExtras().getString("imageSender");
        String imageReceiver = intent.getExtras().getString("imageReceiver");

        String idChat = intent.getExtras().getString("idChat");
        String idSender = intent.getExtras().getString("idSender");
        String idReceiver = intent.getExtras().getString("idReceiver");
        String tokenSender = intent.getExtras().getString("tokenSender");
        String tokenReceiver = intent.getExtras().getString("tokenReceiver");


        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        NotificationHelper helper = new NotificationHelper(context);

        //Action on Notifications
        Intent intentResponse = new Intent(context, ResponseReceiver.class);
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

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intentResponse,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        ////Data for action on notifications
        NotificationCompat.Action actionResponse = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();




        NotificationCompat.Builder builder = helper.getNotificationMessage(
                messages,
                message,
                usernameSender,
                null,
                myBitmap,actionResponse,
                null);

        //Random random = new Random();
        //int numeroRam = random.nextInt(10000);



        Log.e("NOTIFICATION RESPONSE","ID:" + id);
        Log.e("NOTIFICATION RESPONSE","usernameSender:" + usernameSender);
        //the id is the position of notifications on the smarthphone
        helper.getManager().notify(id,builder.build());

        if(!message.equals(""))
        {
            Message myMessage = new Message();
            myMessage.setIdChat(idChat);
            myMessage.setIdSender(idReceiver);
            myMessage.setIdReceiver(idSender);
            myMessage.setMessage(message);
            myMessage.setStatus("ENVIADO");
            myMessage.setType("texto");
            myMessage.setTimestamp(new Date().getTime());

            createMessage(myMessage);

            ArrayList<Message> messageArrayList = new ArrayList<>();
            messageArrayList.add(myMessage);

            sendNotification(context,
                    messageArrayList,
                    String.valueOf(id),
                    usernameReceiver,
                    usernameSender,
                    imageReceiver,
                    imageSender,
                    idChat,
                    idSender,
                    idReceiver,
                    tokenSender,
                    tokenReceiver
                    );

        }


    }

    private void createMessage(Message message) {


            MessageProvider mMessageProvider = new MessageProvider();

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {

                    Log.e("NOTIFICATION RESPONSE","Mensaje creado");
                }
            });

    }

    private void sendNotification(Context context,
                                  ArrayList<Message> messages,
                                  String idNotification,
                                  String usernameReceiver,
                                  String usernameSender,
                                  String imageReceiver,
                                  String imageSender,
                                  String idChat,
                                  String idSender,
                                  String idReceiver,
                                  String tokenSender,
                                  String tokenReceiver) {

        Map<String, String> data = new HashMap<>();
        data.put("title","MENSAJE");
        data.put("body", "texto mensaje");
        data.put("idNotification", idNotification);
        data.put("usernameReceiver", usernameSender);
        data.put("usernameSender", usernameReceiver);
        data.put("imageSender",imageReceiver);
        data.put("imageReceiver",imageSender);
        data.put("idChat",idChat);
        data.put("idSender",idReceiver);
        data.put("idReceiver",idSender);
        data.put("tokenSender",tokenReceiver);
        data.put("tokenReceiver",tokenSender);

        Gson gson = new Gson();
        String messagesJSON = gson.toJson(messages); // Arralist to json

        data.put("messagesJSON", messagesJSON);

        NotificationProvider mNotificationProvider = new NotificationProvider();

        mNotificationProvider.send(context, tokenSender, data);

    }

    private void getMyImage(Context context, Intent intent)
    {
        String myImage = intent.getExtras().getString("imageReceiver");

        Log.e("NOTIFICATION RECEIVER","imageSender:" + myImage);

        if(myImage == null)
        {
            showNotification(context,intent, null);
            return;
        }

        if(myImage.equals(""))
        {
            showNotification(context,intent,null);
            return;
        }

        //download image into bitmap
        Glide.with(context)
                .asBitmap()
                .load(myImage)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {

                        showNotification(context, intent, resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);

                        showNotification(context, intent,null);

                    }
                });
    }

    private CharSequence getMessageText(Intent intent)
    {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        if(remoteInput != null)
        {
            return remoteInput.getCharSequence(NOTIFICATION_REPLY);
        }

        return null;
    }
}
