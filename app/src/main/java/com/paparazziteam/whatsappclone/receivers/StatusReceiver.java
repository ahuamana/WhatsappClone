package com.paparazziteam.whatsappclone.receivers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
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

public class StatusReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

       //getMyImage(context,intent);
        updateStatus(context,intent);
    }

    private void updateStatus(Context context, Intent intent) {


        int id = intent.getExtras().getInt("idNotification");
        String messagesJSON = intent.getExtras().getString("messages");

        MessageProvider messageProvider = new MessageProvider();

        Gson gson = new Gson();
        Message[] messages = gson.fromJson(messagesJSON, Message[].class);


        for(Message m: messages)
        {
            messageProvider.updateStatus(m.getId(),"VISTO");
        }

        //Delete notification
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(id);

        /*
        NotificationCompat.Builder builder = helper.getNotificationMessage(messages,message, usernameSender, null, myBitmap,actionResponse);
        helper.getManager().notify(id,builder.build());

        */

    }


}
