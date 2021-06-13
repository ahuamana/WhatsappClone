package com.paparazziteam.whatsappclone.providers;

import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paparazziteam.whatsappclone.utils.CompressorBitmapImage;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    StorageReference mStorage;

    public ImageProvider()
    {
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    public UploadTask save (Context context, File file)
    {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(),500,500); //
        StorageReference storage = mStorage.child(new Date() + ".jpg"); // almacera la imagen y se le asigna el nombre con la fecha actual + .jpg
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }
}
