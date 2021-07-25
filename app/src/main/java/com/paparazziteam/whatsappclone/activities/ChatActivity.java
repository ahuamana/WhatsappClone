package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.MessageAdapter;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ChatsProvider;
import com.paparazziteam.whatsappclone.providers.FilesProvider;
import com.paparazziteam.whatsappclone.providers.MessageProvider;
import com.paparazziteam.whatsappclone.providers.NotificationProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;
import com.paparazziteam.whatsappclone.utils.AppBackgroundHelper;
import com.paparazziteam.whatsappclone.utils.RelativeTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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

    ImageView mImageViewSelectPictures;
    ImageView mImageViewSelectFiles;

    MessageAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;

    LinearLayoutManager mLinearLayoutManager;

    Timer mTimer;

    ListenerRegistration mListenerChat;

    User mUserReceiver;
    User mMyUser;
    Chat mChat;

    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();

    final int ACTION_FILE = 2;
    ArrayList<Uri> mFileList;
    FilesProvider mFilesProvider;

    NotificationProvider mNotificationProvider;

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
        mFilesProvider = new FilesProvider();

        mNotificationProvider = new NotificationProvider();


        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSend = findViewById(R.id.imageViewSend);
        mRecyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        mImageViewSelectPictures = findViewById(R.id.imageViewSelectPictures);
        mImageViewSelectFiles = findViewById(R.id.imageViewSelectFiles);

        //ImagePicker
        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(6)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(mReturnValues)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.Picture)                                     //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");                                       //Custom Path For media Storage

        mLinearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);//messages on recycler  put over keyboard
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager); // esto es para decirle al recycler view que se muestre de manera lineal, es decir uno debajo del otro

        showChatToolbar(R.layout.chat_toolbar);
        
        getUserReceiverInfo();//Obtenemos todos los datos despues de showChatToolbar

        getMyUserInfo();

        checkIfExistChat();
        setWriting();


        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage();
            }
        });

        mImageViewSelectPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPix();
            }
        });

        mImageViewSelectFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFiles();
            }
        });

    }

    private void selectFiles() {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), ACTION_FILE);
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

        if(mAdapter!= null)
        {
            mAdapter.stopListening();
        }
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
            message.setType("texto");
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

                    //send Notification
                    getLastMessages(message);

                    Toast.makeText(ChatActivity.this, "El mensaje se envio correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Ingresa el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    private void getLastMessages( Message message)
    {
        mMessageProvider.getLastMessagesByChatAndSender(mExtraIdChat, mAuthProvider.getID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if(querySnapshot != null)
                {
                    ArrayList<Message> messages = new ArrayList<>();

                    for(DocumentSnapshot document: querySnapshot.getDocuments())
                    {
                        Message m = document.toObject(Message.class);
                        messages.add(m);
                    }

                    if(messages.size()==0)
                    {
                        messages.add(message);
                    }

                    sendNotification(messages);
                }
            }
        });
    }


    private void sendNotification(ArrayList<Message> messages) {

        Map<String, String> data = new HashMap<>();
        data.put("title","MENSAJE");
        data.put("body", "texto mensaje");
        data.put("idNotification", String.valueOf(mChat.getIdNotification()));
        data.put("usernameReceiver", mUserReceiver.getUsername());
        data.put("usernameSender", mMyUser.getUsername());

        Gson gson = new Gson();
        String messagesJSON = gson.toJson(messages); // Arralist to json

        data.put("messages", messagesJSON);



        mNotificationProvider.send(ChatActivity.this, mUserReceiver.getToken(), data);

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
                        mChat = value.toObject(Chat.class);
                        if(mChat.getWriting() != null)
                        {
                            if(!mChat.getWriting().equals(""))
                            {
                                if(!mChat.getWriting().equals(mAuthProvider.getID()))
                                {
                                    mTextViewOnline.setText("Escribiendo");

                                }else
                                {
                                    if(mUserReceiver != null)
                                    {
                                        if(mUserReceiver.isOnline())
                                        {
                                            mTextViewOnline.setText("En Linea");
                                        } else {
                                            String relativeTime = RelativeTime.getTimeAgo(mUserReceiver.getLastConnect(), ChatActivity.this); //create relative time from last connected
                                            if(relativeTime != null)
                                            {
                                                mTextViewOnline.setText(relativeTime);
                                            }

                                        }

                                    }else
                                    {
                                        mTextViewOnline.setText("");
                                    }

                                }
                            }else
                            {
                                if(mUserReceiver != null)
                                {
                                    if(mUserReceiver.isOnline())
                                    {
                                        mTextViewOnline.setText("En Linea");
                                    } else {
                                        String relativeTime = RelativeTime.getTimeAgo(mUserReceiver.getLastConnect(), ChatActivity.this); //create relative time from last connected
                                        if(relativeTime != null)
                                        {
                                            mTextViewOnline.setText(relativeTime);
                                        }

                                    }

                                }else
                                {
                                    mTextViewOnline.setText("");
                                }
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

        Random random = new Random();
        int numeroRam = random.nextInt(10000);

        mChat = new Chat();

        mChat.setId(mAuthProvider.getID() + mExtraIdUser);
        mChat.setTimestamp(new Date().getTime());
        mChat.setNumberMessages(0);
        mChat.setWriting("");
        mChat.setIdNotification(numeroRam);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getID());
        ids.add(mExtraIdUser);

        mChat.setIds(ids);

        mExtraIdChat = mChat.getId();

        mChatsProvider.create(mChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getMessagesByChat();
                Toast.makeText(ChatActivity.this, "El chat se creo correctamente ", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void getMyUserInfo()
    {
        mUsersProvider.getUserInfo(mAuthProvider.getID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if(documentSnapshot.exists())
                {
                    mMyUser = documentSnapshot.toObject(User.class);

                }

            }
        });
    }

    private void getUserReceiverInfo() {

        mUsersProvider.getUserInfo(mExtraIdUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null)
                {
                    if(documentSnapshot.exists())
                    {
                        mUserReceiver = documentSnapshot.toObject(User.class);
                        mTextViewUsername.setText(mUserReceiver.getUsername());

                        if(mUserReceiver.getImage() !=null)
                        {
                            if(!mUserReceiver.getImage().equals(""))
                            {
                                Glide.with(ChatActivity.this)
                                        .load(mUserReceiver.getImage())
                                        .into(mCircleImageUser);
                            }
                        }
                        if(mUserReceiver.isOnline())
                        {
                            mTextViewOnline.setText("En Linea");
                        } else {
                            String relativeTime = RelativeTime.getTimeAgo(mUserReceiver.getLastConnect(), ChatActivity.this); //create relative time from last connected
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





    private void startPix() {
        Pix.start(ChatActivity.this, mOptions);
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
                    Intent intent = new Intent(ChatActivity.this, ConfirmImageSendActivity.class);
                    intent.putExtra("data", mReturnValues);
                    intent.putExtra("idChat", mExtraIdChat);
                    intent.putExtra("idReceiver", mExtraIdUser);
                    startActivity(intent);


                }

                if (requestCode == ACTION_FILE && resultCode == RESULT_OK)
                {
                    mFileList = new ArrayList<>();
                    ClipData clipData = data.getClipData();

                    //Selecciono solo un archivo
                    if(clipData == null)
                    {
                        Uri uri = data.getData();
                        mFileList.add(uri);
                    }else
                    {
                        //Seleeciono varios archivos
                        int count = clipData.getItemCount();

                        for(int i=0; i< count; i++)
                        {
                            Uri uri = clipData.getItemAt(i).getUri();
                            mFileList.add(uri); // capturing selected data from user
                        }
                    }

                    mFilesProvider.savaFiles(ChatActivity.this, mFileList,mExtraIdChat,mExtraIdUser);
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
                    Pix.start(ChatActivity.this, mOptions);
                } else {
                    Toast.makeText(ChatActivity.this, "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;
            }

        }
    }
}