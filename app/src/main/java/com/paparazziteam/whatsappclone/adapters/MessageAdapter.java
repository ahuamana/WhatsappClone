package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

        //puras configuraciones para verificar quien envia el mensaje
        if(message.getIdSender().equals(authProvider.getID()))
        {
            //signica que nosotros somos el que envio el mensaje, entonces setearemos para que el mensaje se posicione a la derecha
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(150,0,0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,50,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_right));
            holder.textViewMessage.setTextColor(Color.BLACK);
            holder.textViewDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.VISIBLE);

            //change state from sensed to viewed
            if(message.getStatus().equals("ENVIADO"))
            {
                holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_gray);
            }
            else
            {
                if(message.getStatus().equals("VISTO"))
                {
                    holder.imageViewCheck.setImageResource(R.drawable.icon_double_check_blue);
                }
            }


        }else
            {
                //signica que nosotros NO somos el que envio el mensaje, entonces setearemos para que el mensaje se posicione a la izquierda
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params.setMargins(0,0,150,0);
                holder.linearLayoutMessage.setLayoutParams(params);
                holder.linearLayoutMessage.setPadding(80,20,30,20);
                holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_left));
                holder.textViewMessage.setTextColor(Color.BLACK);
                holder.textViewDate.setTextColor(Color.DKGRAY);
                holder.imageViewCheck.setVisibility(View.GONE);

            }


        showImage(holder, message);




    }

    private void showImage(viewHolder holder, Message message) {

        if(message.getType().equals("imagen"))
        {
            if(message.getUrl() != null)
            {

                Log.e("DATA","imagen no es nula" + message.getUrl());

                if( !message.getUrl().equals(""))
                {
                    Log.e("DATA","imagen no esta vacia" + message.getUrl());

                    holder.imageViewMessage.setVisibility(View.VISIBLE);
                    Glide.with(context)
                            .load(message.getUrl())
                            .into(holder.imageViewMessage);

                    ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                    ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();

                    marginDate.topMargin =15;
                    marginCheck.topMargin =15;

                    Log.e("MARGIN","APPLIED");


                }else
                {
                    Log.e("DATA","imagen Vacia" + message.getUrl());
                    holder.imageViewMessage.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }

            } else
            {
                Log.e("DATA","imagen es nula");
                holder.imageViewMessage.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }else
        {
            Log.e("DATA","no es una imagen");
            holder.imageViewMessage.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
        }
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
        ImageView imageViewMessage;
        View myView;
        LinearLayout linearLayoutMessage;

        public  viewHolder(View view)
        {
            super(view);
            myView=view;
            textViewMessage = view.findViewById(R.id.textViewMessage_message);
            textViewDate = view.findViewById(R.id.textViewDate_message);
            imageViewCheck = view.findViewById(R.id.imageViewCheck_message);
            imageViewMessage = view.findViewById(R.id.imageViewMessage_message);
            linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage_message);

        }

    }
}
