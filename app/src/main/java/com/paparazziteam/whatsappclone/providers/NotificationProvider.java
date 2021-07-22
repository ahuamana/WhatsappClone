package com.paparazziteam.whatsappclone.providers;

import com.paparazziteam.whatsappclone.models.FCMBody;
import com.paparazziteam.whatsappclone.models.FCMResponse;
import com.paparazziteam.whatsappclone.retrofit.IFCMApi;
import com.paparazziteam.whatsappclone.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Retrofit;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider()
    {

    }

    public Call<FCMResponse> sendNotification(FCMBody body)
    {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
