package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;
import com.paparazziteam.whatsappclone.utils.RelativeTime;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.viewHolder > {

    Context context;
    AuthProvider authProvider;
    UsersProvider mUsersProvider;
    User user;

    ListenerRegistration listener;


    public MessageAdapter(@NonNull FirestoreRecyclerOptions options, Context context) {
        super(options);

        this.context = context;
        this.authProvider = new AuthProvider();
        this.mUsersProvider = new UsersProvider();
        this.user = new User();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Message message) {

        holder.textViewMessage.setText(message.getMessage());
        holder.textViewDate.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context));


    }



    public ListenerRegistration getListener()
    {
        return listener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);


        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewMessage;
        TextView textViewDate;
        ImageView imageViewCheck;
        View myView;

        public  viewHolder(View view)
        {
            super(view);
            myView=view;
            textViewMessage = view.findViewById(R.id.textViewMessage_message);
            textViewDate = view.findViewById(R.id.textViewDate_message);
            imageViewCheck = view.findViewById(R.id.imageViewCheck_message);

        }

    }
}
