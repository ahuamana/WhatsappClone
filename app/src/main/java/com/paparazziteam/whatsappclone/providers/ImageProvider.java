package com.paparazziteam.whatsappclone.providers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.Status;
import com.paparazziteam.whatsappclone.utils.CompressorBitmapImage;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;
    FirebaseStorage mFirebaseStorage;
    int index =0;
    MessageProvider mMessageProvider;
    StatusProvider mStatusProvider;

    public ImageProvider()
    {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorage = mFirebaseStorage.getReference();
        mMessageProvider = new MessageProvider();
        mStatusProvider = new StatusProvider();

    }

    public UploadTask save (Context context, File file)
    {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(),500,500); //
        StorageReference storage = mStorage.child(new Date() + ".jpg"); // almacera la imagen y se le asigna el nombre con la fecha actual + .jpg
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    public Task<Uri> getDownloadUri()
    {
        return mStorage.getDownloadUrl();
    }

    public Task<Void> delete(String url)
    {
       return mFirebaseStorage.getReferenceFromUrl(url).delete(); // eliminar photo de firebase
    }

    public void uploadMultiple (Context context, ArrayList<Message> messages)
    {
        Uri[] uri = new Uri[messages.size()];

        for(int i=0; i < messages.size(); i++)
        {
            File file = CompressorBitmapImage.reduceImageSize(new File(messages.get(i).getUrl())); // Coger la Rutal del archivo y reducir el tamaño y luego convertirlo en un archivo para enviarlo a firebase

            uri[i] = Uri.parse("file://"+file.getPath());

            StorageReference ref = mStorage.child(uri[i].getLastPathSegment());

            ref.putFile(uri[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = uri.toString();
                                messages.get(index).setUrl(url); // metodo de alamcenar varias imagenes en una sola peticion
                                mMessageProvider.create(messages.get(index));
                                index++;
                            }
                        });

                    }else
                    {
                        Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                    }

                    
                }
            });//almacenar cada imagen en la base de datos
        }

    }


    public void uploadMultipleStatus (Context context, ArrayList<Status> statusList)
    {
        Uri[] uri = new Uri[statusList.size()];

        for(int i=0; i < statusList.size(); i++)
        {
            File file = CompressorBitmapImage.reduceImageSize(new File(statusList.get(i).getUrl())); // Coger la Rutal del archivo y reducir el tamaño y luego convertirlo en un archivo para enviarlo a firebase

            uri[i] = Uri.parse("file://"+file.getPath());

            StorageReference ref = mStorage.child(uri[i].getLastPathSegment());

            ref.putFile(uri[i]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<UploadTask.TaskSnapshot> task) {

                    if(task.isSuccessful())
                    {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = uri.toString();
                                statusList.get(index).setUrl(url); // metodo de alamcenar varias imagenes en una sola peticion
                                mStatusProvider.create(statusList.get(index));
                                index++;
                            }
                        });

                    }else
                    {
                        Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                    }


                }
            });//almacenar cada imagen en la base de datos
        }

    }
}
