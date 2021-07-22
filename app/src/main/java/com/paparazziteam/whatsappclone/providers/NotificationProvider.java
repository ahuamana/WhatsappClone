package com.paparazziteam.whatsappclone.providers;

import android.content.Context;
import android.widget.Toast;

import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.models.FCMBody;
import com.paparazziteam.whatsappclone.models.FCMResponse;
import com.paparazziteam.whatsappclone.retrofit.IFCMApi;
import com.paparazziteam.whatsappclone.retrofit.RetrofitClient;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
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

    public void send(Context context, String token, Map<String, String> data)
    {
        //ttl = in how many seconds this notification will send
        FCMBody body = new FCMBody(token,"high","4500s", data);

        sendNotification(body).enqueue(new Callback<FCMResponse>() {
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {

                if(response.body() != null)
                {
                    if(response.body().getSuccess() != 1)
                    {
                        Toast.makeText(context, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    Toast.makeText(context, "No hubo respuesta del servidor!", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Toast.makeText(context, "Fallo la peticion con Retrofit: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
