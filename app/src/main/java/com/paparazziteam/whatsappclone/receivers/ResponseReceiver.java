package com.paparazziteam.whatsappclone.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import static com.paparazziteam.whatsappclone.services.MyFirebaseMessagingClient.NOTIFICATION_REPLY;

public class ResponseReceiver  extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        String message = getMessageText(intent).toString();
        android.util.Log.e("NOTIFICATION: ","Mensaje input: "+message);
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
