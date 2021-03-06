package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.MessageProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;
import com.paparazziteam.whatsappclone.utils.RelativeTime;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.viewHolder > {

    Context context;
    AuthProvider authProvider;
    UsersProvider mUsersProvider;
    MessageProvider messageProvider;
    User user;

    ListenerRegistration listener;
    ListenerRegistration listenerLastMessage;


    public ChatsAdapter(@NonNull FirestoreRecyclerOptions options, Context context) {
        super(options);

        this.context = context;
        this.authProvider = new AuthProvider();
        this.mUsersProvider = new UsersProvider();
        this.messageProvider = new MessageProvider();
        this.user = new User();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Chat chat) {

        String idUser="";

        //Verificar la posicion donde se encuentra el id del chat
        for(int i =0; i<chat.getIds().size() ; i++)
        {
            if(!authProvider.getID().equals(chat.getIds().get(i)))
            {
                idUser = chat.getIds().get(i);
                break;
            }

        }

        getLastMessage(holder, chat.getId());

        getUserInfo(holder, idUser);


        getMessagesNotRead(holder,chat.getId());


        setWriting(holder, chat);

        myViewClick(holder, idUser, chat);



    }

    private void setWriting(viewHolder holder, Chat chat) {

        if(chat.getWriting() != null)
        {
            if(!chat.getWriting().equals(""))
            {
                    if(!chat.getWriting().equals(authProvider.getID()))
                {
                    holder.textViewWriting.setVisibility(View.VISIBLE);
                    holder.textViewLastMessage.setVisibility(View.GONE);

                }else
                {
                    holder.textViewWriting.setVisibility(View.GONE);
                    holder.textViewLastMessage.setVisibility(View.VISIBLE);
                }
            }else
            {
                holder.textViewWriting.setVisibility(View.GONE);
                holder.textViewLastMessage.setVisibility(View.VISIBLE);
            }
        }

    }

    private void getMessagesNotRead(viewHolder holder, String idChat) {

        messageProvider.getReceiverMessageNotRead(idChat, authProvider.getID()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot querySnapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                    if(querySnapshot != null)
                    {


                        int size = querySnapshot.size();

                        Log.e("LOG",""+size);

                        if(size>0)
                        {
                            holder.frameLayoutMessagesNotRead.setVisibility(View.VISIBLE);
                            holder.textViewMessagesNotRead.setText(String.valueOf(size));
                            holder.textViewTimeStamp.setTextColor(context.getResources().getColor(R.color.colorGreenAccent));
                        }else
                        {
                            holder.frameLayoutMessagesNotRead.setVisibility(View.GONE);
                            holder.textViewTimeStamp.setTextColor(context.getResources().getColor(R.color.colorGrayDark));
                        }
                    }

            }
        });

    }

    private void getLastMessage(viewHolder holder, String idChat) {

        listenerLastMessage = messageProvider.getLastMessage(idChat).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {

                if(querySnapshot != null)
                {
                    int size = querySnapshot.size();

                    if(size > 0)
                    {
                        Message message = querySnapshot.getDocuments().get(0).toObject(Message.class);
                        holder.textViewLastMessage.setText(message.getMessage());//ultimo mensaje
                        holder.textViewTimeStamp.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context)); // last hour from message

                        if(message.getIdSender().equals(authProvider.getID()))
                        {

                            holder.imageViewCheck.setVisibility(View.VISIBLE);

                            if(message.getStatus().equals("ENVIADO"))
                            {
                                holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_gray);
                            }
                            else
                             {
                                if (message.getStatus().equals("VISTO"))
                                {
                                    holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_blue);
                                }
                            }
                        }
                        else
                        {

                            holder.imageViewCheck.setVisibility(View.GONE);
                        }


                    }
                }

            }
        });



    }

    private void myViewClick(viewHolder holder, String idUser, Chat chat) {

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(chat.getId(),  idUser); // Envias el id del usuario que seleccione
            }
        });
    }

    private void goToChatActivity(String idChat, String idUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idUser",idUser);
        intent.putExtra("idChat",idChat);
        context.startActivity(intent);
    }


    private void getUserInfo(viewHolder holder , String idUser) {

        listener=mUsersProvider.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null)
                {
                    if(documentSnapshot.exists())
                    {
                        user = documentSnapshot.toObject(User.class);
                        holder.textViewUsername.setText(user.getUsername());

                        if(user.getImage() != null)
                        {
                            if(!user.getImage().equals(""))
                            {
                                //Toast.makeText(context, "Imagen no es nula", Toast.LENGTH_SHORT).show();

                                Glide.with(context)
                                        .load(user.getImage())
                                        .into(holder.circleImageViewUser);

                            }else {holder.circleImageViewUser.setImageResource(R.drawable.ic_person);}
                        }else { holder.circleImageViewUser.setImageResource(R.drawable.ic_person); }
                    }
                }

            }
        });

    }



    public ListenerRegistration getListener()
    {
        return listener;
    }
    public ListenerRegistration getListenerLastMessage()
    {
        return listenerLastMessage;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);


        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewTimeStamp;
        CircleImageView circleImageViewUser;
        ImageView imageViewCheck;
        View myView;
        FrameLayout frameLayoutMessagesNotRead;
        TextView textViewMessagesNotRead;
        TextView textViewWriting;

        public  viewHolder(View view)
        {
            super(view);
            myView=view;
            textViewUsername = view.findViewById(R.id.textViewUser_chat_adapter);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessage_chat_adapter);
            textViewTimeStamp = view.findViewById(R.id.textViewTimeStamp_chat_adapter);
            imageViewCheck = view.findViewById(R.id.imageViewCheck_chat_adapter);
            circleImageViewUser = view.findViewById(R.id.circleImageUser_chat_adapter);
            frameLayoutMessagesNotRead = view.findViewById(R.id.frameLayoutMessagesNotRead);
            textViewMessagesNotRead = view.findViewById(R.id.textViewMessagesNotRead);
            textViewWriting = view.findViewById(R.id.textViewWriting_chat_adapter);

        }

    }
}
