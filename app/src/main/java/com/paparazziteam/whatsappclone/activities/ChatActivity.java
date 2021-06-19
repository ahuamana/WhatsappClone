package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.paparazziteam.whatsappclone.R;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        showChatToolbar(R.layout.chat_toolbar);
    }

    private void showChatToolbar(int resource)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true); // ocultar back buttom from toolbar
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource,null);
        actionBar.setCustomView(view);
    }
}