package com.paparazziteam.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.ViewPagerAdapter;
import com.paparazziteam.whatsappclone.fragments.ChatsFragment;
import com.paparazziteam.whatsappclone.fragments.ContacsFragment;
import com.paparazziteam.whatsappclone.fragments.StatusFragment;
import com.paparazziteam.whatsappclone.providers.AuthProvider;

public class HomeActivity extends AppCompatActivity implements MaterialSearchBar.OnSearchActionListener {

    AuthProvider mAuthProvider;

    MaterialSearchBar mSearchBar;

    TabLayout mTabLayout;

    ViewPager2 mViewPager;

    ChatsFragment mChatsFragment;
    ContacsFragment mContactsFragment;
    StatusFragment mStatusFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mSearchBar = findViewById(R.id.searchBar);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);



        mViewPager.setOffscreenPageLimit(3); // Contendrá el numero de fragmentos

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        mChatsFragment = new ChatsFragment();
        mContactsFragment = new ContacsFragment();
        mStatusFragment = new StatusFragment();

        adapter.addFragment(mChatsFragment, "Chats");
        adapter.addFragment(mContactsFragment, "Contactos");
        adapter.addFragment(mStatusFragment, "Status");

        mViewPager.setAdapter(adapter);


        new TabLayoutMediator(mTabLayout, mViewPager, adapter.configurationTitle()).attach();  //Asignar todos los titulos y fragmentos



        mSearchBar.setOnSearchActionListener(this); //implementar metodos para buscar
        mSearchBar.inflateMenu(R.menu.main_menu); //Añadir el menu opciones al search bar
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId() == R.id.itemSignOut)
                {
                    signOut();
                }

                return true;
            }
        });

        mAuthProvider = new AuthProvider();



    }

    private void signOut() {
        mAuthProvider.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //Methos implemented from search bar

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}