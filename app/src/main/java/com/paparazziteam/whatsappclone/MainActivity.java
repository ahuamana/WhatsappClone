package com.paparazziteam.whatsappclone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Button mButtonSendCode;
    EditText mEditTextPhone;
    CountryCodePicker mCountryCode;


    FirebaseFirestore mFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonSendCode = findViewById(R.id.btnSendCode);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        mCountryCode = findViewById(R.id.cpp);

        mFirestore = FirebaseFirestore.getInstance();


        mButtonSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goToVerificationActivity();
                getData();
            }
        });

        saveData();
    }

    public void saveData()
    {
        Map<String, Object> map = new HashMap<>();
        map.put("name","Antony Nicolas");
        mFirestore.collection("Usuarios").document().set(map);
    }


    public void getData()
    {
        String code = mCountryCode.getSelectedCountryCodeWithPlus();
        String phone = mEditTextPhone.getText().toString();

        if(phone.equals(""))
        {
            Toast.makeText(this, "Debes ingresar un numero de telefono", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Telefono: " + code +" " + phone, Toast.LENGTH_SHORT).show();
        }
    }

    public void goToVerificationActivity(){

        Intent intent= new Intent(MainActivity.this,CodeVerificationActivity.class);
        startActivity(intent);

    }
}