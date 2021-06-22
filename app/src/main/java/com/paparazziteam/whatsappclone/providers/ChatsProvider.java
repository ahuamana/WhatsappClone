package com.paparazziteam.whatsappclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.paparazziteam.whatsappclone.models.Chat;

import java.util.ArrayList;

public class ChatsProvider {

    CollectionReference mCollection;

    public  ChatsProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public Task<Void> create (Chat chat)
    {
        return mCollection.document().set(chat);
    }

    public Query getChatByUser1AndUser2(String idUser1, String idUser2)
    {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1+idUser2);
        ids.add(idUser2+idUser1);

       return mCollection.whereIn("id",ids);
    }
}
