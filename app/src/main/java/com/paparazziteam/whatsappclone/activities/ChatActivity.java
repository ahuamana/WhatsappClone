package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.ChatsAdapter;
import com.paparazziteam.whatsappclone.adapters.MessageAdapter;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ChatsProvider;
import com.paparazziteam.whatsappclone.providers.MessageProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser;
    String mExtraIdChat;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessageProvider mMessageProvider;

    TextView mTextViewUsername;
    CircleImageView mCircleImageUser;

    EditText mEditTextMessage;
    ImageView mImageViewSend;

    MessageAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;

    LinearLayoutManager mLinearLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser = getIntent().getStringExtra("idUser");//REcibimos id pasado desde el intent del recycler view
        mExtraIdChat = getIntent().getStringExtra("idChat");//REcibimos id pasado desde el intent del recycler view


        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mChatsProvider = new ChatsProvider();
        mMessageProvider = new MessageProvider();


        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSend = findViewById(R.id.imageViewSend);
        mRecyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager); // esto es para decirle al recycler view que se muestre de manera lineal, es decir uno debajo del otro

        showChatToolbar(R.layout.chat_toolbar);
        
        getUserInfo();//Obtenemos todos los datos despues de showChatToolbar


        checkIfExistChat();


        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //al iniciar la pantalla este escuchara otra vez
        if(mAdapter!= null)
        {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

         mAdapter.stopListening();
    }

    private void createMessage() {
        String textMessage = mEditTextMessage.getText().toString();

        if(!textMessage.equals(""))
        {
            Message message = new Message();
            message.setIdChat(mExtraIdChat);
            message.setIdSender(mAuthProvider.getID());
            message.setIdReceiver(mExtraIdUser);
            message.setMessage(textMessage);
            message.setStatus("ENVIADO");
            message.setTimestamp(new Date().getTime());

            mMessageProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mEditTextMessage.setText("");
                    Toast.makeText(ChatActivity.this, "El mensaje se envio correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Ingresa el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfExistChat() {

        mChatsProvider.getChatByUser1AndUser2(mAuthProvider.getID(), mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if(queryDocumentSnapshots != null)
                {
                    if(queryDocumentSnapshots.size() == 0)
                    {
                        createChat();
                    }else{
                         mExtraIdChat= queryDocumentSnapshots.getDocuments().get(0).getId();
                         getMessagesByChat();
                        Toast.makeText(ChatActivity.this, "EL chat entre estos dos usarios ya existe", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }

    private void getMessagesByChat() {

        Query query = mMessageProvider.getMessagesByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        mAdapter = new MessageAdapter(options, ChatActivity.this);//inicilizar el adaptador
        mRecyclerViewMessages.setAdapter(mAdapter);

        mAdapter.startListening();//que escuche en timepo real los cambios

    }

    private void createChat() {

        Chat chat = new Chat();
        chat.setId(mAuthProvider.getID() + mExtraIdUser);
        chat.setTimestamp(new Date().getTime());
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getID());
        ids.add(mExtraIdUser);

        chat.setIds(ids);

        mExtraIdChat = chat.getId();

        mChatsProvider.create(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getMessagesByChat();
                Toast.makeText(ChatActivity.this, "El chat se creo correctamente ", Toast.LENGTH_SHORT).show();
            }
        });


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