package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    TextView mTextViewUsername;
    CircleImageView mCircleImageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser = getIntent().getStringExtra("id");//REcibimos id pasado desde el intent
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

        showChatToolbar(R.layout.chat_toolbar);
        
        getUserInfo();//Obtenemos todos los datos despues de showChatToolbar
    }

    private void getUserInfo() {

        mUsersProvider.getUserInfo(mExtraIdUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null)
                {
                    if(documentSnapshot.exists())
                    {
                        User user = documentSnapshot.toObject(User.class);
                        mTextViewUsername.setText(user.getUsername());

                        if(user.getImage() !=null)
                        {
                            if(!user.getImage().equals(""))
                            {
                                Glide.with(ChatActivity.this)
                                        .load(user.getImage())
                                        .into(mCircleImageUser);
                            }
                        }
                    }
                }
            }
        });
    }

    private void showChatToolbar(int resource)
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true); // ocultar back buttom from toolbar
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource,null);
        actionBar.setCustomView(view);

        ImageView imageViewBack = findViewById(R.id.imageViewBack);
        mTextViewUsername = findViewById(R.id.textviewUsername_chat);
        mCircleImageUser = findViewById(R.id.circleImageUser_chat);


        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//on esto nos permitira ir hacia atras
            }
        });
    }
}