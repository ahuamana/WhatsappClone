package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.fragments.BottomSheetSelectImage;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;
import com.paparazziteam.whatsappclone.utils.Mytoolbar;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    CircleImageView mCircleImageProfile;

    FloatingActionButton mFabSelectImage;

    BottomSheetSelectImage mBottomSheetSelectImage;

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

        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetSelectImage();
            }
        });
        
        getUserInfo();

    }

    private void openBottomSheetSelectImage() {

        mBottomSheetSelectImage = new BottomSheetSelectImage();
        mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());

    }

    private void getUserInfo() {
        mUsersProvider.getUserInfo(mAuthProvider.getID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { // .get para obtener la informacion
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    User user = documentSnapshot.toObject(User.class);//seteamos a nuestra clase modelo user para almacenar todos los datos ahi
                    mTextViewUsername.setText(user.getUsername());
                    mTextViewPhone.setText(user.getPhone());

                    if(user.getImage()!= null)
                    {
                        if(!user.getImage().equals(""))
                        {
                            Glide.with(ProfileActivity.this)
                                    .load(user.getImage())
                                    .into(mCircleImageProfile);
                        }
                    }

                }

            }
        });
    }
}