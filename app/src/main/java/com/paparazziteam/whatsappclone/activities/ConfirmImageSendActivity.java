package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.OptionsPagerAdapter;
import com.paparazziteam.whatsappclone.utils.ShadowTransformer;

import java.util.ArrayList;

public class ConfirmImageSendActivity extends AppCompatActivity {

    ViewPager mViewPager;
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);
        setStatusBarColor(); // apply color of status bar

        mViewPager = findViewById(R.id.viewPager_Image_Confirm);


        data = getIntent().getStringArrayListExtra("data");


        //
        OptionsPagerAdapter pagerAdapter = new OptionsPagerAdapter(
                getApplicationContext(),getSupportFragmentManager(),
                dpToPixels( 2 , this),
                data);

        ShadowTransformer transformer = new ShadowTransformer(mViewPager,pagerAdapter);

        transformer.enableScaling(true);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(false, transformer);


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