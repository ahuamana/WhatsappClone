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

    public Query getLastMessagesByChatAndSender(String idChat , String idSender)
    {
        ArrayList<String> status = new ArrayList<>();
        status.add("ENVIADO");
        status.add("RECIBIDO");

        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                //.whereEqualTo("status","ENVIADO")
                .whereIn("status",status)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5);
    }

    public Task<Void> updateStatus(String idMessage, String status)
    {
        Map<String, Object> map = new HashMap<>();
        map.put("status",status);
        return mCollection.document(idMessage).update(map);
    }

    public Query getMessageNotRead(String idChat)
    {
        ArrayList<String> status = new ArrayList<>();
        status.add("ENVIADO");
        status.add("RECIBIDO");

        //cuando concatenamos dos valors, en firestore debemos añadir indices para que nos devuelva valores y nos de esta respuesta verdadera
        return mCollection.whereEqualTo("idChat", idChat)
                .whereIn("status", status);
    }

    public Query getReceiverMessageNotRead(String idChat, String idReciever)
    {
        ArrayList<String> status = new ArrayList<>();
        status.add("ENVIADO");
        status.add("RECIBIDO");
        //cuando concatenamos dos valors, en firestore debemos añadir indices para que nos devuelva valores y nos de esta respuesta verdadera
        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereIn("status", status)
                .whereEqualTo("idReceiver",idReciever);
    }

    public Query getLastMessage(String idChat)
    {
        //return last message that is created
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }

    public DocumentReference getMessagesById(String idMessage)
    {
        return mCollection.document(idMessage);
    }


}
