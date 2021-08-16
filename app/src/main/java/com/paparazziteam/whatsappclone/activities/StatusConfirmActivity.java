package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.OptionsPagerAdapter;
import com.paparazziteam.whatsappclone.adapters.StatusPagerAdapter;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.Status;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ImageProvider;
import com.paparazziteam.whatsappclone.providers.NotificationProvider;
import com.paparazziteam.whatsappclone.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StatusConfirmActivity extends AppCompatActivity {

    ViewPager mViewPager;

    ArrayList<String> data;

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    ArrayList<Status> mStatus = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_confirm);
        setStatusBarColor(); // apply color of status bar

        mViewPager = findViewById(R.id.viewPager_Image_Confirm);

        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();


        data = getIntent().getStringArrayListExtra("data");

        if(data != null)
        {
            for (int i = 0 ;  i < data.size(); i++)
            {
                long now =  new Date().getTime();
                //long limit = now + (60 * 1000 * 3); // 3 minutos
                //long limit = now + (60 * 1000 * 20); // 20 minutos
                long limit = now + (60 * 1000 * 60 * 24); // 24 horas

                Status s = new Status();
                s.setIdUser(mAuthProvider.getID());
                s.setComment("");
                s.setTimestamp(now);
                s.setTimestampLimit(limit);
                s.setUrl(data.get(i));

                mStatus.add(s);
            }

        }


        //
        StatusPagerAdapter pagerAdapter = new StatusPagerAdapter(
                getApplicationContext(),getSupportFragmentManager(),
                dpToPixels( 2 , this),
                data);

        ShadowTransformer transformer = new ShadowTransformer(mViewPager,pagerAdapter);

        transformer.enableScaling(true);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(false, transformer);


    }



    public void send()
    {
        mImageProvider.uploadMultipleStatus(StatusConfirmActivity.this, mStatus);
        finish();
    }

    public void setComment(int position, String comment)
    {
        mStatus.get(position).setComment(comment);
    }


    public static float dpToPixels(int dp, Context context)
    {
        return dp * ( context.getResources().getDisplayMetrics().density);
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