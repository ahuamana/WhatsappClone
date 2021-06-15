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
import android.widget.LinearLayout;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.paparazziteam.whatsappclone.R;
import com.paparazziteam.whatsappclone.adapters.ViewPagerAdapter;
import com.paparazziteam.whatsappclone.fragments.ChatsFragment;
import com.paparazziteam.whatsappclone.fragments.ContacsFragment;
import com.paparazziteam.whatsappclone.fragments.PhotoFragment;
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
    PhotoFragment mPhotoFratgment;

    int mTabSelected = 1 ;

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
        mPhotoFratgment = new PhotoFragment();

        adapter.addFragment(mPhotoFratgment, "");
        adapter.addFragment(mChatsFragment, "Chats");
        adapter.addFragment(mStatusFragment, "Status");
        adapter.addFragment(mContactsFragment, "Contactos");


        mViewPager.setAdapter(adapter);

        new TabLayoutMediator(mTabLayout, mViewPager, adapter.configurationTitle()).attach();  //Asignar todos los titulos y fragmentos

        mViewPager.setCurrentItem(mTabSelected);

        setupTabIcon();//Agregar con icono como tab


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

    private void setupTabIcon() {

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        LinearLayout linearLayout = ((LinearLayout) ((LinearLayout) mTabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight = 0.5f;
        linearLayout.setLayoutParams(layoutParams);
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