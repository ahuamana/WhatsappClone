package com.paparazziteam.whatsappclone.providers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.paparazziteam.whatsappclone.models.User;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference mCollection;

    public UsersProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Query getAllUsersByName()
    {
        return mCollection.orderBy("username");
    }


    public Task<Void> create(User user)
    {
        return mCollection.document(user.getId()).set(user);
    }

    public void createToken(String idUser)
    {
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<String> task) {

                if(task.isSuccessful())
                {
                    String token = task.getResult();

                    Map<String, Object> map = new HashMap<>();
                    map.put("username",token);
                    mCollection.document(idUser).update(map);
                }
            }
        });
    }

    public Task<Void> update(User user)
    {
        //Actualizar username
        Map<String, Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("image",user.getImage());

        return mCollection.document(user.getId()).update(map);
    }

    public DocumentReference getUserInfo(String id)
    {
        return mCollection.document(id);
    }

    public Task<Void> updateImage(String id, String url)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("image",url);
        return mCollection.document(id).update(map);
    }

    public Task<Void> updateUsername(String id, String username)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("username",username);
        return mCollection.document(id).update(map);
    }

    public Task<Void> updateInfo(String id, String info)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("info",info);
        return mCollection.document(id).update(map);
    }

    public Task<Void> updateOnline(String idUser, boolean status)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("online",status);  // update status connected
        map.put("lastConnect", new Date().getTime()); //update status from last connected
        return mCollection.document(idUser).update(map);
    }
}
