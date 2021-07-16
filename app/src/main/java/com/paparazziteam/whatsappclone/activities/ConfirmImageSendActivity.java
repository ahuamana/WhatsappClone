package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.OptionsPagerAdapter;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ImageProvider;
import com.paparazziteam.whatsappclone.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.Date;

public class ConfirmImageSendActivity extends AppCompatActivity {

    ViewPager mViewPager;
    String mExtraIdChat;
    String mExtraIdReceiver;
    ArrayList<String> data;
    ArrayList<Message> messages = new ArrayList<>();

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);
        setStatusBarColor(); // apply color of status bar

        mViewPager = findViewById(R.id.viewPager_Image_Confirm);

        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();


        data = getIntent().getStringArrayListExtra("data");
        mExtraIdChat = getIntent().getStringExtra("idChat");
        mExtraIdReceiver = getIntent().getStringExtra("idReceiver");

        if(data != null)
        {
            for (int i = 0 ;  i < data.size(); i++)
            {
                Message m = new Message();
                m.setIdChat(mExtraIdChat);
                m.setIdSender(mAuthProvider.getID());
                m.setIdReceiver(mExtraIdReceiver);
                m.setStatus("ENVIADO");
                m.setTimestamp(new Date().getTime());
                m.setType("imagen");
                m.setUrl(data.get(i));
                m.setMessage("\uD83D\uDCF7 imagen");

                messages.add(m);
            }

        }


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

    public void send()
    {
        /*for(int i = 0; i < data.size() ; i++)
        {
            Log.e("PRUEBA","Comentario: "+ messages.get(i));
        }*/

        mImageProvider.uploadMultiple(ConfirmImageSendActivity.this,messages);
        finish();//cerrar el view
    }

    public void setMessage(int position, String message)
    {
        messages.get(position).setMessage(message);
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