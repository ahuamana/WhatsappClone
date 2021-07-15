package com.paparazziteam.whatsappclone.fragments;

import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.activities.ConfirmImageSendActivity;
import com.paparazziteam.whatsappclone.interfaces.CardAdapter;

import java.io.File;

public class ImagePagerFragment extends Fragment {

    CardView mCardViewOptions;
    View mView;

    ImageView mImageViewPicture;
    ImageView mImageViewBack;

    LinearLayout mLinearLayoutImagePager;

    EditText mEditTextComment;
    ImageView mImageViewSend;

    public ImagePagerFragment() {
        // Required empty public constructor
    }




    public static Fragment newInstance(int position, String imagePath, int size) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position); // to know in which current fragment is the user
        args.putInt("size", size);
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
        mImageViewSend = mView.findViewById(R.id.imageViewSend_image_pager);

        mLinearLayoutImagePager = mView.findViewById(R.id.linearLayoutViewPager);
        mEditTextComment = mView.findViewById(R.id.editTextComment_image_pager);


        String imagePath = getArguments().getString("image");//get arguments from constructor
        int size = getArguments().getInt("size");//get arguments from constructor
        int position = getArguments().getInt("position");//get arguments from constructor


        if(size == 1)
        {
            //Remove paddings from linear layout when you set 1 image
            mLinearLayoutImagePager.setPadding(0,0,0,0);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mImageViewBack.getLayoutParams();
            params.leftMargin = 10;
            params.topMargin = 35;

        }

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

        mEditTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                ((ConfirmImageSendActivity) getActivity()).setMessage(position, charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((ConfirmImageSendActivity) getActivity()).send();
            }
        });


        return mView;
    }

    public CardView getCardView()
    {
        return mCardViewOptions;
    }
}