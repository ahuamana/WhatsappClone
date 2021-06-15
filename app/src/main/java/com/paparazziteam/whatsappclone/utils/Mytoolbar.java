package com.paparazziteam.whatsappclone.utils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.paparazziteam.whatsappclone.R;

public class Mytoolbar {

    public static void show(AppCompatActivity activity, String title, boolean upbutton)
    {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(title);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(upbutton);
    }

}
