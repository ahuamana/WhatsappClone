package com.paparazziteam.whatsappclone.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.interfaces.CardAdapter;

import java.io.File;

public class ImagePagerFragment extends Fragment {

    CardView mCardViewOptions;
    View mView;

    ImageView mImageViewPicture;
    ImageView mImageViewBack;

    public ImagePagerFragment() {
        // Required empty public constructor
    }




    public static Fragment newInstance(int position, String imagePath) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position); // to know in which current fragment is the user
        args.putString("image", imagePath);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_image_pager, container, false);

        mCardViewOptions = mView.findViewById(R.id.cardViewOptionsXML);
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);
        mImageViewPicture = mView.findViewById(R.id.imageViewPicture_image_pager);
        mImageViewBack = mView.findViewById(R.id.imageViewBack_image_pager);


        String imagePath = getArguments().getString("image");//get arguments from constructor

        if(imagePath != null)
        {
            File file = new File(imagePath);
            mImageViewPicture.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath())); // show image from string on image view
        }

        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retroceder con un boton
                getActivity().finish();
            }
        });



        return mView;
    }

    public CardView getCardView()
    {
        return mCardViewOptions;
    }
}