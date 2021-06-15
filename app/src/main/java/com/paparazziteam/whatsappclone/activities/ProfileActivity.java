package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.utils.Mytoolbar;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Mytoolbar.show(this,"Perfil",true); //mostrar toolbar

    }
}