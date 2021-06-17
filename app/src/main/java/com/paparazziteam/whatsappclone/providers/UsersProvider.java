package com.paparazziteam.whatsappclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paparazziteam.whatsappclone.models.User;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference mCollection;

    public UsersProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<Void> create(User user)
    {
        return mCollection.document(user.getId()).set(user);
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
}
