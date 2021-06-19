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
import com.paparazziteam.whatsappclone.adapters.ContactsAdapter;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.UsersProvider;


public class ContacsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewContacts;

    ContactsAdapter mAdapter;

    UsersProvider mUsersProvider;

    public ContacsFragment() {
        // Required empty public constructor
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_contacs, container, false);
        mRecyclerViewContacts = mView.findViewById(R.id.recyclerViewContacts);
        mUsersProvider = new UsersProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewContacts.setLayoutManager(linearLayoutManager); //para decirle que se posicione cada objeto uno debajo del otro


        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mUsersProvider.getAllUsersByName();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                                                        .setQuery(query, User.class)
                                                        .build();

        mAdapter = new ContactsAdapter(options, getContext());//inicilizar el adaptador
        mRecyclerViewContacts.setAdapter(mAdapter);

        mAdapter.startListening();//que escuche en timepo real los cambios

    }

    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}