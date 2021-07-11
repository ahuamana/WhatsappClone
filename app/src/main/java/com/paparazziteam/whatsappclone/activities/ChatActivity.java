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
import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.ListenerRegistration;
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
import com.paparazziteam.whatsappclone.utils.AppBackgroundHelper;
import com.paparazziteam.whatsappclone.utils.RelativeTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser;
    String mExtraIdChat;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessageProvider mMessageProvider;

    TextView mTextViewUsername;
    TextView mTextViewOnline;
    CircleImageView mCircleImageUser;

    EditText mEditTextMessage;
    ImageView mImageViewSend;

    MessageAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;

    LinearLayoutManager mLinearLayoutManager;

    Timer mTimer;

    ListenerRegistration mListenerChat;


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
        mLinearLayoutManager.setStackFromEnd(true);//messages on recycler  put over keyboard
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager); // esto es para decirle al recycler view que se muestre de manera lineal, es decir uno debajo del otro

        showChatToolbar(R.layout.chat_toolbar);
        
        getUserInfo();//Obtenemos todos los datos despues de showChatToolbar


        checkIfExistChat();
        setWriting();


        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage();
            }
        });

    }

    private void setWriting() {
        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //SI ESTA ESCRIBIENDO
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(mExtraIdChat != null)
                {
                    mChatsProvider.updateWriting(mExtraIdChat,mAuthProvider.getID());//Update on firebase state
                }



            }

            //SI EL USUARIO DEJO DE ESCRIBIR
            @Override
            public void afterTextChanged(Editable s) {

                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if(mTimer != null)
                        {
                            //if it's not writing after 1000 miliseconds
                            if(mExtraIdChat != null)
                            {
                                mChatsProvider.updateWriting(mExtraIdChat,"");//Update on firebase state
                                mTimer.cancel();
                            }
                        }


                    }
                }, 2000);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        AppBackgroundHelper.setOnline(ChatActivity.this, true);//State connected change to true == means online

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
        AppBackgroundHelper.setOnline(ChatActivity.this, false);//State connected change to false == means online
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mListenerChat != null)
        {
            mListenerChat.remove();
        }
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

                    //go to last message when you write
                    //add settack == true when adapater is initialize
                    if(mAdapter != null)
                    {
                        mAdapter.notifyDataSetChanged();
                    }

                    mChatsProvider.updateNumberMessages(mExtraIdChat);

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
                         //Toast.makeText(ChatActivity.this, "EL chat entre estos dos usarios ya existe", Toast.LENGTH_SHORT).show();
                         updateStatusMessage();

                         getChatInfo();
                        
                    }
                }

            }
        });
    }

    private void getChatInfo() {

        mListenerChat = mChatsProvider.getChatById(mExtraIdChat).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                if(value != null)
                {
                    if(value.exists())
                    {
                        Chat chat = value.toObject(Chat.class);
                        if(chat.getWriting() != null)
                        {
                            if(!chat.getWriting().equals(""))
                            {
                                if(!chat.getWriting().equals(mAuthProvider.getID()))
                                {
                                    mTextViewOnline.setText("Escribiendo");

                                }else
                                {
                                    mTextViewOnline.setText("");
                                }
                            }else
                            {
                                mTextViewOnline.setText("");
                            }
                        }
                    }
                }

            }
        });
    }

    private void updateStatusMessage() {
        //.get() para obtener
        mMessageProvider.getMessageNotRead(mExtraIdChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //Toast.makeText(ChatActivity.this, "EL chat se actualiza a VISTO", Toast.LENGTH_SHORT).show();
                //for each
                for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments())
                {
                    Message message = document.toObject(Message.class);
                    //validar solo de los mensajes que otros enviaron y no de el mismo usuario
                    if(!message.getIdSender().equals(mAuthProvider.getID()))
                    {
                        mMessageProvider.updateStatus(message.getId(), "VISTO");
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

        //go to last message when you open the chat, but if not works when you write ////also it listen if any new data is created
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                //update status if user is already inside the chatId

                updateStatusMessage();
                //
                int numberMessage = mAdapter.getItemCount();
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                if(lastMessagePosition == -1 || (positionStart >= (numberMessage - 1) && lastMessagePosition == (positionStart - 1)))
                {
                    mRecyclerViewMessages.scrollToPosition(positionStart);
                }

            }
        });



    }

    private void createChat() {

        Chat chat = new Chat();
        chat.setId(mAuthProvider.getID() + mExtraIdUser);
        chat.setTimestamp(new Date().getTime());
        chat.setNumberMessages(0);
        chat.setWriting("");

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
                        if(user.isOnline())
                        {
                            mTextViewOnline.setText("En Linea");
                        } else {
                            String relativeTime = RelativeTime.getTimeAgo(user.getLastConnect(), ChatActivity.this); //create relative time from last connected
                           if(relativeTime != null)
                           {
                               mTextViewOnline.setText(relativeTime);
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
        mTextViewOnline = findViewById(R.id.textviewOnline_chat);

        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();//on esto nos permitira ir hacia atras
            }
        });
    }
}