package com.paparazziteam.whatsappclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.paparazziteam.whatsappclone.models.Chat;

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
}
