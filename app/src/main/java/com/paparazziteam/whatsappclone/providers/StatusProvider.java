package com.paparazziteam.whatsappclone.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.paparazziteam.whatsappclone.models.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StatusProvider {
    CollectionReference mCollection;

    public StatusProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Status");
    }

    public Task<Void> create (Message message)
    {
        DocumentReference document = mCollection.document();
        message.setId(document.getId());
        return document.set(message);
    }



}
