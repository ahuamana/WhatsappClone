package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.circularstatusview.CircularStatusView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ChatActivity;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.Message;
import com.paparazziteam.whatsappclone.models.Status;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.MessageProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;
import com.paparazziteam.whatsappclone.utils.RelativeTime;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.viewHolder > {

    FragmentActivity context;
    AuthProvider authProvider;
    UsersProvider mUsersProvider;
    MessageProvider messageProvider;
    User user;

    ArrayList<Status> statusList;

    Gson gson = new Gson();

    public StatusAdapter(FragmentActivity context, ArrayList<Status> statusList) {

        this.context = context;
        this.statusList = statusList;
        this.authProvider = new AuthProvider();
        this.mUsersProvider = new UsersProvider();
        this.messageProvider = new MessageProvider();
        this.user = new User();
    }


    private void getUserInfo(viewHolder holder , String idUser) {

        mUsersProvider.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null)
                {
                    if(documentSnapshot.exists())
                    {
                        user = documentSnapshot.toObject(User.class);
                        holder.textViewUsername.setText(user.getUsername());


                    }
                }

            }
        });

    }


    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_status, parent, false);


        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull viewHolder holder, int position) {

        Status[] statusGSON = gson.fromJson(statusList.get(position).getJson(), Status[].class);

        holder.circularStatusView.setPortionsCount(statusGSON.length);

        setImageStatus(statusGSON, holder);

        getUserInfo(holder,statusList.get(position).getIdUser());

    }

    private void setImageStatus(Status[] statusGSON, viewHolder holder) {

        if(statusGSON.length > 0)
        {
            Glide.with(context)
                    .load(statusGSON[statusGSON.length-1].getUrl())
                    .into(holder.circleImageViewUser);

            String relativeTime = RelativeTime.timeFormatAMPM(statusGSON[statusGSON.length-1].getTimestamp(), context);

            holder.textViewDate.setText(relativeTime);
        }
    }

    @Override
    public int getItemCount() {
        return statusList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView textViewUsername;
        TextView textViewDate;
        CircleImageView circleImageViewUser;
        CircularStatusView circularStatusView;

        View myView;


        public  viewHolder(View view)
        {
            super(view);
            myView=view;
            textViewUsername = view.findViewById(R.id.textViewUsername_status_adapter);
            textViewDate = view.findViewById(R.id.textViewDate_status_adapter);
            circleImageViewUser = view.findViewById(R.id.circleImageUser_status_adapter);
            circularStatusView = view.findViewById(R.id.circularStatusView);

        }

    }
}
