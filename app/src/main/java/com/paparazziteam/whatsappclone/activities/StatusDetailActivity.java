package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.models.Status;

import java.net.URL;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StatusDetailActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    StoriesProgressView mStoriesProgressView;
    TextView mTextViewComment;
    ImageView mImageViewStatus;
    View mView;
    Gson mGson = new Gson();

    Status[] mStatus;
    int mCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_detail);
        setStatusBarColor();  //change full topbar black color

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mStoriesProgressView = findViewById(R.id.storiesProgressView);
        mTextViewComment = findViewById(R.id.textViewComment);
        mImageViewStatus = findViewById(R.id.imageViewStatus);
        mView = findViewById(R.id.mainView);

        mStoriesProgressView.setStoriesListener(this);

        String statusJson = getIntent().getStringExtra("status");
        mStatus = mGson.fromJson(statusJson, Status[].class);

        setStatusInfo();

    }

    private void setStatusInfo()
    {
        try{
            URL url = new URL(mStatus[mCounter].getUrl());
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            mStoriesProgressView.setStoriesCount(mStatus.length);
            mStoriesProgressView.setStoryDuration(4000);
            mStoriesProgressView.startStories(mCounter);

            mImageViewStatus.setImageBitmap(image);
            mTextViewComment.setText(mStatus[mCounter].getComment());

        }catch (Exception e)
        {
            Log.e("EXCEPTION","ERROR: "+e.getMessage());
        }


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

    //Change State to the other
    @Override
    public void onNext() {
        mCounter += 1;
        setStatusInfo();
    }

    //change state to the last
    @Override
    public void onPrev() {
       if(mCounter -1 < 0)
       {
           return;// Salir para que no se ejecute el metodo
       }
        mCounter -= 1;
        setStatusInfo();

    }

    //Terminaron de mostrarse todos los stados
    @Override
    public void onComplete() {

        finish();

    }
}