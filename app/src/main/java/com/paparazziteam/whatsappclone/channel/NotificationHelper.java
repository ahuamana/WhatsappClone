package com.paparazziteam.whatsappclone.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.models.Message;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {


    private static final String CHANNEL_ID = "com.paparazziteam.whatsappclone";
    private static final String CHANNEL_NAME = "WhatsappClone";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createChannels();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {

        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
        );

        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GRAY);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel); // canal de notificaciones a partir de la version 8 de android
    }

    public NotificationManager getManager()
    {
        if(manager == null)
        {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return manager;
    }

    //set up configuration from notification
    public NotificationCompat.Builder getNotification(String title, String body)
    {
        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setColor(Color.GRAY)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
    }

    //Notifications with style of message
    public NotificationCompat.Builder getNotificationMessage(
            Message[] messages,
            String myMessage,
            String usernameSender,
            Bitmap bitmapReceiver,
            Bitmap myBitmap,
            NotificationCompat.Action actionResponse)
    {
        //User who send the message
       Person myPerson = null;
       Person receiverPerson= null;

       if(bitmapReceiver == null)
       {
           //User who recieve the message
           receiverPerson = new Person.Builder()
                   .setName(usernameSender)
                   .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_person))
                   .build();
       } else
       {
           //User who recieve the message
           receiverPerson = new Person.Builder()
                   .setName(usernameSender)
                   .setIcon(IconCompat.createWithBitmap(bitmapReceiver))
                   .build();
       }

       if(myBitmap == null)
       {
           //User who recieve the message
           myPerson = new Person.Builder()
                   .setName("Tu")
                   .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_person))
                   .build();
       } else
       {
           //User who recieve the message
           myPerson = new Person.Builder()
                   .setName("Tu")
                   .setIcon(IconCompat.createWithBitmap(myBitmap))
                   .build();
       }



        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(receiverPerson);


        for(Message m: messages)
        {
            //this code is to add message to notification manager
            NotificationCompat.MessagingStyle.Message messageNotification = new NotificationCompat.MessagingStyle.Message(
                    m.getMessage(),
                    m.getTimestamp(),
                    receiverPerson //who will recieve the message
            );

            messagingStyle.addMessage(messageNotification);

        }

        if(!myMessage.equals(""))
        {
            //this code is to add message to notification manager
            NotificationCompat.MessagingStyle.Message myMessageNotification = new NotificationCompat.MessagingStyle.Message(
                    myMessage,
                    new Date().getTime(),
                    myPerson //who will recieve the message
            );

            messagingStyle.addMessage(myMessageNotification);

        }




        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(messagingStyle)
                .addAction(actionResponse);
    }

}

