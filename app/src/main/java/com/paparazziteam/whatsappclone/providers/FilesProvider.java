package com.paparazziteam.whatsappclone.providers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paparazziteam.whatsappclone.models.Message;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class FilesProvider {

    StorageReference mStorage;
    MessageProvider mMessageProvider;
    AuthProvider mAuthProvider;

    public FilesProvider()
    {
        mStorage = FirebaseStorage.getInstance().getReference();
        mMessageProvider = new MessageProvider();
        mAuthProvider= new AuthProvider();
    }

    public void savaFiles(Context context, ArrayList<Uri> files, String idChat, String idReceiver)
    {

        for(int i=0; i< files.size(); i++)
        {
            Uri f = files.get(i); //this get the file name
            StorageReference ref = mStorage.child(f.getLastPathSegment());
            ref.putFile(f).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        //download la URL from the file
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString(); // this url will save on cloudfirestore
                                Message message = new Message();
                                message.setIdChat(idChat);
                                message.setIdReceiver(idReceiver);
                                message.setIdSender(mAuthProvider.getID());
                                message.setType("documento");
                                message.setUrl(url);
                                message.setStatus("ENVIADO");
                                message.setTimestamp(new Date().getTime());
                                message.setMessage(f.getLastPathSegment());//this get the file name

                                mMessageProvider.create(message);
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(context, "no se pudo guardar el archivo", Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }

    }
}
