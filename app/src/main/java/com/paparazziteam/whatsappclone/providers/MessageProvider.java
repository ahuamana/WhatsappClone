package com.paparazziteam.whatsappclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paparazziteam.whatsappclone.models.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageProvider {
    CollectionReference mCollection;

    public MessageProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Message");
    }

    public Task<Void> create (Message message)
    {
        DocumentReference document = mCollection.document();
        message.setId(document.getId());
        return document.set(message);
    }

    public Query getMessagesByChat(String idChat)
    {
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public Task<Void> updateStatus(String idMessage, String status)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("status",status);
        return mCollection.document(idMessage).update(map);
    }
}
