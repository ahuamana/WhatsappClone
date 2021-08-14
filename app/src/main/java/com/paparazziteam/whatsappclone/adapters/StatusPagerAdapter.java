package com.paparazziteam.whatsappclone.adapters;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.paparazziteam.whatsappclone.fragments.ImagePagerFragment;
import com.paparazziteam.whatsappclone.fragments.StatusPagerFragment;
import com.paparazziteam.whatsappclone.interfaces.CardAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatusPagerAdapter extends FragmentStatePagerAdapter implements CardAdapter {

    private List<StatusPagerFragment> fragments;
    private ArrayList<String> data;
    private float baseElevation;

    public StatusPagerAdapter(Context context, FragmentManager fm, float baseElevation, ArrayList<String> data) {
        super(fm);
        fragments = new ArrayList<>();
        this.baseElevation = baseElevation;
        this.data = data;

        // AÑADIR VIEWS
        for(int i = 0; i < data.size(); i++){
            addCardFragment(new StatusPagerFragment());
        }

    }

    public void addCardFragment(StatusPagerFragment fragment) {
        fragments.add(fragment);
    }


    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        return StatusPagerFragment.newInstance(position, data.get(position), data.size());
    }

    @Override
    public float getBaseElevation() {
        return baseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return fragments.get(position).getCardView();
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public int getItemPosition(@NonNull @NotNull Object object) {
        int index = fragments.indexOf (object);

        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull @NotNull ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        fragments.set(position, (StatusPagerFragment) fragment);
        return fragment;
    }
}
