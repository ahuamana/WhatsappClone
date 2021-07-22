package com.paparazziteam.whatsappclone.retrofit;

import com.paparazziteam.whatsappclone.models.FCMBody;
import com.paparazziteam.whatsappclone.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAA6-nu6yA:APA91bEJeTgrGEgMUt_dmHBDsYiHD5Tc4YJRKvWdB9Zuw9fEMr1AOhIadpcwH6xW47tH6UCqcYuZqbs_Racj4ykZEyET9zJl8i7iKg-WcEm4prUch2s52bUkBni5KnD7Z_bxfhot1Nua"

    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
