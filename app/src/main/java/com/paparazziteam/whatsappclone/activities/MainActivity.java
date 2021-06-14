package com.paparazziteam.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {

    Button mButtonSendCode;
    EditText mEditTextPhone;
    CountryCodePicker mCountryCode;

    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSendCode = findViewById(R.id.btnSendCode);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        mCountryCode = findViewById(R.id.cpp);

        mAuthProvider = new AuthProvider();


        mButtonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goToVerificationActivity();
                getData();
            }
        });


    }

    //Ejecuta este metodo justo antes del Oncreate, es parte del ciclo de vida de android
    @Override
    protected void onStart() {
        super.onStart();

        if(mAuthProvider.getSessionUser() != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //Eliminar actividades que quedaron atras
            startActivity(intent);
        }

    }

    public void getData()
    {
        String code = mCountryCode.getSelectedCountryCodeWithPlus();
        String phone = mEditTextPhone.getText().toString();

        if(phone.equals(""))
        {
            Toast.makeText(this, "Debes ingresar un numero de telefono", Toast.LENGTH_SHORT).show();
        } else {
            goToVerificationActivity(code + phone);
        }
    }

    public void goToVerificationActivity(String phone){

        Intent intent= new Intent(MainActivity.this,CodeVerificationActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);

    }
}