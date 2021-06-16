package com.paparazziteam.whatsappclone.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.providers.AuthProvider;
import com.paparazziteam.whatsappclone.providers.ImageProvider;
import com.paparazziteam.whatsappclone.providers.UsersProvider;

public class BottomSheetSelectImage  extends BottomSheetDialogFragment {


    LinearLayout mLinearLayoutDeteleImage;
    ImageProvider mImageProvider;
    AuthProvider mAuthProivder;
    UsersProvider mUserProvider;

    String image;

    public static BottomSheetSelectImage newInstance(String url) {

        BottomSheetSelectImage bottomSheetSelectImage = new BottomSheetSelectImage();

        Bundle args = new Bundle();
        args.putString("image",url);
        bottomSheetSelectImage.setArguments(args);


        return bottomSheetSelectImage;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        image=getArguments().getString("image");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_image, container , false);

        mLinearLayoutDeteleImage = view.findViewById(R.id.linearLayoutDeleteImage);

        mImageProvider = new ImageProvider();
        mUserProvider = new UsersProvider();
        mAuthProivder = new AuthProvider();



        mLinearLayoutDeteleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                deleteImage();

            }
        });



        return view;
    }

    private void deleteImage() {

        mImageProvider.delete(image).addOnCompleteListener(new OnCompleteListener<Void>() {  //Eliminar tarea de Storage
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    mUserProvider.deleteImage(mAuthProivder.getID()).addOnCompleteListener(new OnCompleteListener<Void>() { //Actualizar campo a nulo de la CloudFirestorage
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {

                            if(task2.isSuccessful())
                            {
                                Toast.makeText(getContext(), "La imagen se elimino correctamente", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(getContext(), "No se pudo eliminar el dato de la imagen", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });


                }else {
                    Toast.makeText(getContext(), "No se puedo  eliminar la imagen", Toast.LENGTH_SHORT).show();
                }
                
                
            }
        });

    }
}
