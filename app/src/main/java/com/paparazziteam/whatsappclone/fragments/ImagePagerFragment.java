package com.paparazziteam.whatsappclone.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.paparazziteam.whatsappclone.R;

public class ImagePagerFragment extends Fragment {



    public ImagePagerFragment() {
        // Required empty public constructor
    }

    /*
    public static ImagePagerFragment newInstance(String param1, String param2) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    */


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_pager, container, false);
    }
}