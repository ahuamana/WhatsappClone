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

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Map;

import static com.paparazziteam.whatsappclone.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class ResponseReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

       getMyImage(context,intent);
    }

    private void showNotificationMessage(Context context, Intent intent, Bitmap myBitmap) {

        String message = getMessageText(intent).toString();

        int id = intent.getExtras().getInt("idNotification");
        String messagesJSON = intent.getExtras().getString("messages");
        String usernameSender = intent.getExtras().getString("usernameSender");
        String imageSender = intent.getExtras().getString("imageSender");
        String imageReceiver = intent.getExtras().getString("imageReceiver");
        String idChat = intent.getExtras().getString("idChat");
        String idSender = intent.getExtras().getString("idSender");
        String idReceiver = intent.getExtras().getString("idReceiver");


        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);

        NotificationHelper helper = new NotificationHelper(context);

        //Action on Notifications
        Intent intentResponse = new Intent(context, ResponseReceiver.class);
        intentResponse.putExtra("idNotification", id);
        intentResponse.putExtra("messages", messagesJSON);
        intentResponse.putExtra("usernameSender", usernameSender);
        intentResponse.putExtra("imageSender", imageSender);
        intentResponse.putExtra("imageReceiver", id);
        intentResponse.putExtra("idChat", idChat);
        intentResponse.putExtra("idSender", idSender);
        intentResponse.putExtra("idReceiver", idReceiver);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,id,intentResponse,PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteInput remoteInput = new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        ////Data for action on notifications
        NotificationCompat.Action actionResponse = new NotificationCompat.Action.Builder(
                R.mipmap.ic_launcher,
                "Responder",
                pendingIntent)
                .addRemoteInput(remoteInput)
                .build();

        NotificationCompat.Builder builder = helper.getNotificationMessage(messages,message, usernameSender, "", null, myBitmap,actionResponse);

        //Random random = new Random();
        //int numeroRam = random.nextInt(10000);

        createMessage(message, idChat,idReceiver,idSender);

        Log.e("NOTIFICATION RESPONSE","ID:" + id);
        Log.e("NOTIFICATION RESPONSE","usernameSender:" + usernameSender);
        //the id is the position of notifications on the smarthphone
        helper.getManager().notify(id,builder.build());


        android.util.Log.e("NOTIFICATION: ","Mensaje input: "+message);
    }

    private void createMessage(String messageText, String idChat,String idReceiver, String idSender) {


        if(!messageText.equals(""))
        {
            Message message = new Message();
            message.setIdChat(idChat);
            message.setIdSender(idReceiver);
            message.setIdReceiver(idSender);
            message.setMessage(messageText);
            message.setStatus("ENVIADO");
            message.setType("texto");
            message.setTimestamp(new Date().getTime());

            MessageProvider mMessageProvider = new MessageProvider();

            mMessageProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {

                    Log.e("NOTIFICATION RESPONSE","Mensaje creado");
                }
            });
        }
    }

    private void getMyImage(Context context, Intent intent)
    {
        String myImage = intent.getExtras().getString("imageReceiver");

        Log.e("NOTIFICATION RECEIVER","imageSender:" + myImage);

        if(myImage == null)
        {
            showNotificationMessage(context,intent, null);
            return;
        }

        if(myImage.equals(""))
        {
            showNotificationMessage(context,intent,null);
            return;
        }

        //download image into bitmap
        Glide.with(context)
                .asBitmap()
                .load(myImage)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull @NotNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {

                        showNotificationMessage(context, intent, resource);

                    }

                    @Override
                    public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable @org.jetbrains.annotations.Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);

                        showNotificationMessage(context, intent,null);

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
