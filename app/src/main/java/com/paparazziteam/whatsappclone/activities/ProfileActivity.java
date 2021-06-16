package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.fragments.BottomSheetSelectImage;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;
import com.paparazziteam.whatsappclone.utils.Mytoolbar;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    CircleImageView mCircleImageProfile;

    FloatingActionButton mFabSelectImage;

    BottomSheetSelectImage mBottomSheetSelectImage;

    User mUser;


    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();
    File mImageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Mytoolbar.show(this,"Perfil",true); //mostrar toolbar

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mCircleImageProfile = findViewById(R.id.circleImageProfile);

        mFabSelectImage = findViewById(R.id.fabSelectImage);


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


        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetSelectImage();
            }
        });
        
        getUserInfo();

    }

    private void openBottomSheetSelectImage() {

        if(mUser != null)
        {
            mBottomSheetSelectImage = BottomSheetSelectImage.newInstance(mUser.getImage());
            mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());
        }else {
            Toast.makeText(this, "La informacion no se pudo cargar", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserInfo() {
        mUsersProvider.getUserInfo(mAuthProvider.getID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { // .get para obtener la informacion
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    mUser = documentSnapshot.toObject(User.class);//seteamos a nuestra clase modelo user para almacenar todos los datos ahi
                    mTextViewUsername.setText(mUser.getUsername());
                    mTextViewPhone.setText(mUser.getPhone());

                    if(mUser.getImage()!= null)
                    {
                        if(!mUser.getImage().equals(""))
                        {
                            Glide.with(ProfileActivity.this)
                                    .load(mUser.getImage())
                                    .into(mCircleImageProfile);
                        }
                    }

                }

            }
        });
    }

    public void setImageDefault() {
        mCircleImageProfile.setImageResource(R.drawable.ic_person_white);
    }


    public void startPix() {
        Pix.start(ProfileActivity.this, mOptions);
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
                    //Log.e("DATA INGRESASTE: ", "RequestCode: " + requestCode + " & resultacode: "+resultCode);
                    mReturnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    mImageFile = new File(mReturnValues.get(0)); // Guardar en File la imagen recibida si el usuario selecciono una imagen
                    mCircleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath())); //Asignar la imagen al id del xml
                } else {
                    Toast.makeText(this, "error al seleccionar la foto", Toast.LENGTH_SHORT).show();
                }
            }

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
                    Pix.start(ProfileActivity.this, mOptions);
                } else {
                    Toast.makeText(ProfileActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }

}