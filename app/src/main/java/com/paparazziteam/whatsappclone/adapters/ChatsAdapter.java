package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.viewHolder > {

    Context context;
    AuthProvider authProvider;


    public ChatsAdapter(@NonNull FirestoreRecyclerOptions options, Context context) {
        super(options);

        this.context = context;
        this.authProvider = new AuthProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull Chat chat) {


        //holder.textViewUsername.setText(user.getUsername());
        //holder.textViewInformation.setText(user.getInfo());

        /*
        if(user.getImage() != null)
        {
            if(!user.getImage().equals(""))
            {
                Glide.with(context)
                        .load(user.getImage())
                        .into(holder.circleImageViewUser);
            }else {holder.circleImageViewUser.setImageResource(R.drawable.ic_person);}

        }else {holder.circleImageViewUser.setImageResource(R.drawable.ic_person);}


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(user.getId()); // Envias el id del usuario que seleccione
            }
        });

         */


    }

    private void goToChatActivity(String id) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("id",id);
        context.startActivity(intent);
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

        public  viewHolder(View view)
        {
            super(view);
            myView=view;
            textViewUsername = view.findViewById(R.id.textViewUser_chat_adapter);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessage_chat_adapter);
            textViewTimeStamp = view.findViewById(R.id.textViewTimeStamp_chat_adapter);
            imageViewCheck = view.findViewById(R.id.imageViewCheck_chat_adapter);
            circleImageViewUser = view.findViewById(R.id.circleImageUser_chat_adapter);

        }

    }
}
