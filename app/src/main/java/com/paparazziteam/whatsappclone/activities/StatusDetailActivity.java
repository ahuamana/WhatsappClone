package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import com.paparazziteam.whatsappclone.R;

public class StatusDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);

        setStatusBarColor();
    }

    private void setStatusBarColor ()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullblack, this.getTheme()));
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullblack));
            }
        }
    }
}