package com.paparazziteam.whatsappclone.providers;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.paparazziteam.whatsappclone.activities.CodeVerificationActivity;
import com.paparazziteam.whatsappclone.activities.MainActivity;

import java.util.concurrent.TimeUnit;

public class AuthProvider {
    private FirebaseAuth mAuth;

    public AuthProvider() {
        this.mAuth = FirebaseAuth.getInstance();
    }

    public void sendCodeVerification(String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks, Activity context)
    {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(context)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);

    }

    public Task<AuthResult> signInPhone(String verificationId, String code)
    {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        return mAuth.signInWithCredential(credential);
    }

    public FirebaseUser getSessionUser()
    {
        return mAuth.getCurrentUser(); //Retornara nullo si es que no ah iniciado sesion
    }

    public  String getID()
    {
        if(mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        } else {
            return null;
        }
    }


}
