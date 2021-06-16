package com.paparazziteam.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ProfileActivity;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ImageProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

public class BottomSheetUsername extends BottomSheetDialogFragment {

    Button mButtonSave;
    Button mButtonCancel;
    EditText mEditTextUsername;

    ImageProvider mImageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;

    String username;


    public static BottomSheetUsername newInstance(String username) {

        BottomSheetUsername bottomSheetSelectImage = new BottomSheetUsername();

        Bundle args = new Bundle();
        args.putString("username",username);
        bottomSheetSelectImage.setArguments(args);


        return bottomSheetSelectImage;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username=getArguments().getString("username");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_username, container , false);


        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();


        mButtonSave = view.findViewById(R.id.btnSave);
        mButtonCancel = view.findViewById(R.id.btnCancel);
        mEditTextUsername = view.findViewById(R.id.editTextUsername);
        mEditTextUsername.setText(username);


        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    updateUsername();
            }
        });

        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void updateUsername() {

        String NewUsername = mEditTextUsername.getText().toString();

        if(!username.equals(""))
        {
            mUserProvider.updateUsername(mAuthProvider.getID(), username).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    setNewUsername(NewUsername);
                    dismiss();
                    Toast.makeText(getContext(), "El nombre de usuario se ah actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    private void setNewUsername(String username) {
        ((ProfileActivity)getActivity()).setUsernameNew(username);
    }


}
