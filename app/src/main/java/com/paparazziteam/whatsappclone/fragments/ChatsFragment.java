package com.paparazziteam.whatsappclone.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.ChatsAdapter;
import com.paparazziteam.whatsappclone.adapters.ContactsAdapter;
import com.paparazziteam.whatsappclone.models.Chat;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ChatsProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;


public class ChatsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewChats;

    ChatsAdapter mAdapter;
    UsersProvider mUsersProvider;
    ChatsProvider mChatsProvider;
    AuthProvider mAuthProvider;


    public ChatsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chats, container, false);
        mRecyclerViewChats = mView.findViewById(R.id.recyclerViewChats);
        mUsersProvider = new UsersProvider();
        mChatsProvider = new ChatsProvider();
        mAuthProvider = new AuthProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewChats.setLayoutManager(linearLayoutManager); //para decirle que se posicione cada objeto uno debajo del otro


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mChatsProvider.getUsersChats(mAuthProvider.getID());
        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        mAdapter = new ChatsAdapter(options, getContext());//inicilizar el adaptador
        mRecyclerViewChats.setAdapter(mAdapter);

        mAdapter.startListening();//que escuche en timepo real los cambios

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}