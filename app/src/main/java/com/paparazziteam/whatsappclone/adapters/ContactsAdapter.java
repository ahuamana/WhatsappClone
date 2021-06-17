package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.models.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends FirestoreRecyclerAdapter<User, ContactsAdapter.viewHolder > {

    Context context;

    public ContactsAdapter(@NonNull FirestoreRecyclerOptions options, Context context) {
        super(options);

        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull User user) {

        holder.textViewUsername.setText(user.getUsername());
        holder.textViewInformation.setText(user.getInfo());

        if(user.getImage() != null)
        {
            if(!user.getImage().equals(""))
            {
                Glide.with(context)
                        .load(user.getImage())
                        .into(holder.circleImageViewUser);
            }else {holder.circleImageViewUser.setImageResource(R.drawable.ic_person);}

        }else {holder.circleImageViewUser.setImageResource(R.drawable.ic_person);}


    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts, parent, false);


        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewUsername;
        TextView textViewInformation;
        CircleImageView circleImageViewUser;

        public  viewHolder(View view)
        {
            super(view);
            textViewUsername = view.findViewById(R.id.textViewUser_);
            textViewInformation = view.findViewById(R.id.textViewInfo_);
            circleImageViewUser = view.findViewById(R.id.circleImageUser_);

        }

    }
}
