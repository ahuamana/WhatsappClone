package com.paparazziteam.whatsappclone.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.paparazziteam.whatsappclone.models.Chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsProvider {

    CollectionReference mCollection;

    public  ChatsProvider()
    {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public Task<Void> create (Chat chat)
    {
        return mCollection.document(chat.getId()).set(chat);
    }

    public Query getChatByUser1AndUser2(String idUser1, String idUser2)
    {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1+idUser2);
        ids.add(idUser2+idUser1);

       return mCollection.whereIn("id",ids);
    }

    public Query getUsersChats (String idUser)
    {
        //retornara si un array con todos los objetos que coincidad con nuestro id dentro del array "ids"
        //return mCollection.whereArrayContains("ids", idUser);
        return mCollection.whereArrayContains("ids", idUser).whereGreaterThanOrEqualTo("numberMessages",1);//List chat with iduser and also number of messages must be >= 1
    }

    public void updateNumberMessages(String idChat)
    {
        mCollection.document(idChat).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("numberMessages"))
                    {
                        Long numberMessages = documentSnapshot.getLong("numberMessages") + 1;//Update number of messages +1

                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages",numberMessages);

                        mCollection.document(idChat).update(map);

                    }
                    else
                    {
                        Long numberMessages = documentSnapshot.getLong("numberMessages");

                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages",1);

                        mCollection.document(idChat).update(map);

                    }
                }
            }
        });


    }
}
