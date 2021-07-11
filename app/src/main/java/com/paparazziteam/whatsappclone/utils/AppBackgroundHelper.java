package com.paparazziteam.whatsappclone.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

import java.util.List;

public class AppBackgroundHelper {


    public static void online(Context context, boolean status)
    {
        UsersProvider usersProvider = new UsersProvider();
        AuthProvider authProvider = new AuthProvider();



        usersProvider.updateOnline(authProvider.getID(), status);
    }

    //with this class will know if user minimize the app
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
