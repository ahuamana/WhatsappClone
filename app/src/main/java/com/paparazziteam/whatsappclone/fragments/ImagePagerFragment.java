package com.paparazziteam.whatsappclone.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.interfaces.CardAdapter;

public class ImagePagerFragment extends Fragment {

    CardView mCardViewOptions;
    View mView;

    public ImagePagerFragment() {
        // Required empty public constructor
    }




    public static Fragment newInstance(int position) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position); // to know in which current fragment is the user
        //args.putString(ARG_PARAM2, param2);
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

        


        return mView;
    }

    public CardView getCardView()
    {
        return mCardViewOptions;
    }
}