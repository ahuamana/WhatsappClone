package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteInfoActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonConfirm;
    CircleImageView mCircleImagePhoto;


    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    Options mOptions;


    ArrayList<String> mReturnValues = new ArrayList<>();

    File mImageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        mTextInputUsername = findViewById(R.id.textInputUsername);
        mButtonConfirm = findViewById(R.id.btnConfirm);
        mCircleImagePhoto = findViewById(R.id.circleImagePhoto);

        //Instanciar usuario
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //ImagePicker
        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(1)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(mReturnValues)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.Picture)                                     //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");                                       //Custom Path For media Storage

        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });

        mCircleImagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPix();
            }
        });


    }

    private void startPix() {
        Pix.start(CompleteInfoActivity.this, mOptions);
    }


    private void updateUserInfo() {
        String username = mTextInputUsername.getText().toString();
        if (!username.equals("")) {
            User user = new User();
            user.setUsername(username);
            user.setId(mAuthProvider.getID());
            mUsersProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CompleteInfoActivity.this, "La informacion de actualizo correctamente", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //fetch only one image for camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (resultCode != RESULT_CANCELED)
        {
            if (data != null)
            {
                if (resultCode == Activity.RESULT_OK && requestCode == 100)
                {
                    Log.e("DATA INGRESASTE: ", "RequestCode: " + requestCode + " & resultacode: "+resultCode);
                    mReturnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    mImageFile = new File(mReturnValues.get(0)); // Guardar en File la imagen recibida si el usuario selecciono una imagen
                    mCircleImagePhoto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath())); //Asignar la imagen al id del xml
                } else {
                    Toast.makeText(this, "error al seleccionar la foto", Toast.LENGTH_SHORT).show();
                }
            }else {Log.e("DATA: ", "RequestCode: " + requestCode + " & resultacode: "+resultCode);}

        }else { Toast.makeText(this, "operacion Cancelado!", Toast.LENGTH_SHORT).show(); }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //set permission to use camera


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(CompleteInfoActivity.this, mOptions);
                } else {
                    Toast.makeText(CompleteInfoActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}