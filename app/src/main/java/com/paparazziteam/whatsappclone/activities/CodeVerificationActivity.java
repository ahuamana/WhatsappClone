package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.providers.AuthProvider;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerification;
    String mExtraPhone;

    AuthProvider mAuthProvider;
    String mVerificationId;
    EditText mEditTextCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerification= findViewById(R.id.btnCodeVerification);
        mEditTextCode = findViewById(R.id.editTextCodeVerification);

        //recibir telefono en la variable mExtraphone
        mExtraPhone = getIntent().getStringExtra("phone");

        //Instanciar mAuthProvider
        mAuthProvider = new AuthProvider();

        //ejecutar metodo de la clase
        mAuthProvider.sendCodeVerification(mExtraPhone, mCallbacks, this );


        mButtonCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Sign in
                String code = mEditTextCode.getText().toString();
                if(!code.equals("") && code.length()>=6)
                {
                    signIn(code);
                }else {
                    Toast.makeText(CodeVerificationActivity.this, "Ingresa el codigo", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Retornara el codigo que se envio al mensaje de texto y asiganre a code
            String code = phoneAuthCredential.getSmsCode();

            if(code !=null)
            {
                mEditTextCode.setText(code); // Asignar texto al edit text del xml
                signIn(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            //Ocurre si el codigo se envio
            Toast.makeText(CodeVerificationActivity.this, "El codigo se envio correctamente", Toast.LENGTH_SHORT).show();
            mVerificationId = verificationId;
        }


    };

    private void signIn(String code) {
        //llamar al metodo de la clase AuthProvide
        mAuthProvider.signInPhone(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(CodeVerificationActivity.this, "Exitoso!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CodeVerificationActivity.this, "No se pudo authenticar", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}