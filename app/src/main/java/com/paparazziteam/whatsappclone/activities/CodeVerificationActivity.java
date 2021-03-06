package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.models.User;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

import org.jetbrains.annotations.NotNull;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerification;
    EditText mEditTextCode;
    TextView mTextViewSMS;
    ProgressBar mProgressBar;

    AuthProvider mAuthProvider;

    String mExtraPhone;
    String mVerificationId;

    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerification= findViewById(R.id.btnCodeVerification);
        mEditTextCode = findViewById(R.id.editTextCodeVerification);
        mTextViewSMS = findViewById(R.id.textViewSMS);
        mProgressBar = findViewById(R.id.progressBar);

        //recibir telefono en la variable mExtraphone
        mExtraPhone = getIntent().getStringExtra("phone");

        //Instanciar mAuthProvider
        mAuthProvider = new AuthProvider();

        //ejecutar metodo de la clase
        mAuthProvider.sendCodeVerification(mExtraPhone, mCallbacks, this );

        //
        mUsersProvider = new UsersProvider();


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

            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);

            if(code !=null)
            {
                mEditTextCode.setText(code); // Asignar texto al edit text del xml
                signIn(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            mProgressBar.setVisibility(View.GONE);
            mTextViewSMS.setVisibility(View.GONE);
            Toast.makeText(CodeVerificationActivity.this, "Se produjo un error: "+ e.getMessage(), Toast.LENGTH_LONG).show();
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
                    User user = new User();
                    user.setId(mAuthProvider.getID());//Obtento el UID del telegono logeado
                    user.setPhone(mExtraPhone); //asignamos el telefono a la variable user

                    //Traer la info del documento para verificar si existe o no
                    mUsersProvider.getUserInfo(mAuthProvider.getID()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(!documentSnapshot.exists())
                            {
                                //Iniciar guardado en firestore si no existe el documento
                                    //guardamos a CloudFirestore
                                    mUsersProvider.create(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            goTocompleteInfo(); //si es que se guardo correctamente en CloudFirestore mostrar actividad nueva
                                        }
                                    });
                                    //fin guardamos a CloudFirestore

                            }else
                                {  if(documentSnapshot.contains("username") && documentSnapshot.contains("image"))
                                    {
                                        String username = documentSnapshot.getString("username");
                                        String image = documentSnapshot.getString("image");
                                        if(username != null && image != null)
                                        {
                                            if(!username.equals("") && !image.equals(""))
                                            {
                                                goToHomeActivity();
                                            } else
                                                {
                                                    goTocompleteInfo();
                                                }

                                        } else  {
                                                    goTocompleteInfo();
                                                }
                                    } else
                                            {
                                                goTocompleteInfo();
                                            }

                                }

                        }
                    });



                    Toast.makeText(CodeVerificationActivity.this, "Exitoso!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(CodeVerificationActivity.this, "No se pudo authenticar", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

                Toast.makeText(CodeVerificationActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToHomeActivity() {
        Intent intent = new Intent(CodeVerificationActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // eliminar activities creados anteriormente
        startActivity(intent);
    }

    private void goTocompleteInfo() {
        Intent intent = new Intent(CodeVerificationActivity.this, CompleteInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // eliminar activities creados anteriormente
        startActivity(intent);
    }


}