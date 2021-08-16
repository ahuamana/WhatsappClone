package com.paparazziteam.whatsappclone.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ProfileActivity;
import com.paparazziteam.whatsappclone.activities.StatusConfirmActivity;
import com.paparazziteam.whatsappclone.adapters.StatusAdapter;
import com.paparazziteam.whatsappclone.models.Status;
import com.paparazziteam.whatsappclone.providers.StatusProvider;

import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_CANCELED;


public class StatusFragment extends Fragment {

    View mView;

    LinearLayout mLinearLayoutAddStatus;

    Options mOptions;
    ArrayList<String> mReturnValues = new ArrayList<>();

    RecyclerView mRecyclerView;
    StatusAdapter mAdapter;
    StatusProvider mStatusProvider;

    ArrayList<Status> mNoRepeatStatusList;

    Gson mGson = new Gson();

    public StatusFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_status, container, false);

        mLinearLayoutAddStatus = mView.findViewById(R.id.linearLayputAddStatus);
        mRecyclerView = mView.findViewById(R.id.recyclerViewStatus);

        mStatusProvider = new StatusProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);


        //ImagePicker
        mOptions = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(5)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(mReturnValues)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setMode(Options.Mode.Picture)                                     //Option to select only pictures or videos or both
                .setVideoDurationLimitinSeconds(30)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");                                       //Custom Path For media Storage



        mLinearLayoutAddStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startPix();

            }
        });

        getStatus();


        return mView;
    }

    private void getStatus() {

        mStatusProvider.getStatusByTimestampLimit().addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {

                if(value != null)
                {

                    ArrayList<Status> statusList = new ArrayList<>();
                    mNoRepeatStatusList = new ArrayList<>();

                    for(DocumentSnapshot d: value.getDocuments())
                    {
                        Status s = d.toObject(Status.class);
                        statusList.add(s);

                    }

                    //AÑADIR a la lista mNoRepeatList ELEMENTOS NO REPETIDOS
                    for(Status status: statusList)
                    {
                        boolean isFound = false;

                        for(Status s: mNoRepeatStatusList)
                        {
                            if(s.getIdUser().equals(status.getIdUser()))
                            {
                                isFound=true;
                                break; // Para que no continue recorriendo el for
                            }
                        }

                        if(!isFound)
                        {
                            mNoRepeatStatusList.add(status);
                        }
                    }

                    //Empaquetar todos los estados by user
                    for(Status noRepeat: mNoRepeatStatusList)
                    {
                        ArrayList<Status> sList = new ArrayList<>();

                        for(Status s: statusList)
                        {
                            if(s.getIdUser().equals(noRepeat.getIdUser()))
                            {
                                sList.add(s);
                            }
                        }

                        String statusJSON = mGson.toJson(sList);
                        noRepeat.setJson(statusJSON);
                    }

                    mAdapter = new StatusAdapter(getActivity(),mNoRepeatStatusList);
                    mRecyclerView.setAdapter(mAdapter);

                }
            }
        });
    }


    public void startPix() {
        Pix.start(StatusFragment.this, mOptions);
    }



    //fetch only one image for camera
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_CANCELED)
        {
            if (data != null)
            {
                if (resultCode == Activity.RESULT_OK && requestCode == 100)
                {
                    //Log.e("DATA INGRESASTE: ", "RequestCode: " + requestCode + " & resultacode: "+resultCode);
                    mReturnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);

                    Intent intent = new Intent(getContext(), StatusConfirmActivity.class);
                    intent.putExtra("data",mReturnValues);
                    startActivity(intent);

                } else {
                    Toast.makeText(getContext(), "error al seleccionar la foto", Toast.LENGTH_SHORT).show();
                }
            }

        }else { Toast.makeText(getContext(), "operacion Cancelado!", Toast.LENGTH_SHORT).show(); }

        super.onActivityResult(requestCode, resultCode, data);
    }

    //set permission to use camera


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(StatusFragment.this, mOptions);


                } else {
                    Toast.makeText(getContext(), "Approve permissions to open Pix ImagePicker", Toast.LENGTH_LONG).show();
                }
                return;



            }

        }
    }



}