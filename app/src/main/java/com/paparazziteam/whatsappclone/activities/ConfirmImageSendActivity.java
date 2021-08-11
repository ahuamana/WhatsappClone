package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.OptionsPagerAdapter;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ImageProvider;
import com.paparazziteam.whatsappclone.providers.NotificationProvider;
import com.paparazziteam.whatsappclone.utils.ShadowTransformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ConfirmImageSendActivity extends AppCompatActivity {

    ViewPager mViewPager;
    String mExtraIdChat;
    String mExtraIdReceiver;
    ArrayList<String> data;
    ArrayList<Message> messages = new ArrayList<>();

    User mExtraMyUser;
    User mExtraReceiverUser;

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    NotificationProvider mNotificationProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);
        setStatusBarColor(); // apply color of status bar

        mViewPager = findViewById(R.id.viewPager_Image_Confirm);

        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mNotificationProvider = new NotificationProvider();


        data = getIntent().getStringArrayListExtra("data");
        mExtraIdChat = getIntent().getStringExtra("idChat");
        mExtraIdReceiver = getIntent().getStringExtra("idReceiver");

        String myUser = getIntent().getStringExtra("myUser");
        String receiverUser = getIntent().getStringExtra("receiverUser");

        Gson gson = new Gson();

        mExtraMyUser = gson.fromJson(myUser, User.class);
        mExtraReceiverUser = gson.fromJson(receiverUser, User.class);


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
                m.setMessage("\uD83D\uDCF7imagen");

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

    private void sendNotification(ArrayList<Message> messages) {

        Map<String, String> data = new HashMap<>();
        data.put("title","MENSAJE");
        data.put("body", "texto mensaje");
        data.put("idNotification", String.valueOf(mChat.getIdNotification()));
        data.put("usernameReceiver", mUserReceiver.getUsername());
        data.put("usernameSender", mMyUser.getUsername());
        data.put("imageReceiver",mUserReceiver.getImage());
        data.put("imageSender",mMyUser.getImage());

        data.put("idChat",mExtraIdChat);
        data.put("idSender",mAuthProvider.getID());
        data.put("idReceiver",mExtraIdReceiver);

        data.put("tokenSender",mMyUser.getToken());
        data.put("tokenReceiver",mUserReceiver.getToken());

        Gson gson = new Gson();
        String messagesJSON = gson.toJson(messages); // Arralist to json

        data.put("messagesJSON", messagesJSON);



        mNotificationProvider.send(ConfirmImageSendActivity.this, mUserReceiver.getToken(), data);

    }


    public void send()
    {
        /*for(int i = 0; i < data.size() ; i++)
        {
            Log.e("PRUEBA","Comentario: "+ messages.get(i));
        }*/

        mImageProvider.uploadMultiple(ConfirmImageSendActivity.this,messages);

        //Notification for images


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