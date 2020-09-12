package com.pukhuriandbeels.limknowpilot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.navigation.NavigationView;
import com.pukhuriandbeels.limknowpilot.adapter.MacrophyteAdapter;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import java.util.ArrayList;

public class MacrophyteListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_list);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.about: break;
                    case R.id.policy: break;

                    case R.id.macrophytes: break;
                    case R.id.augmented_reality_game: break;
                    case R.id.reporting: break;

                    case R.id.badges: break;
                    case R.id.edit_profile: break;
                    case R.id.sign_out : break;

                    case R.id.share : break;
                    case R.id.contact_us: break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        navigationView.setCheckedItem(R.id.macrophytes);

        ArrayList<Macrophyte> macrophytes = new ArrayList<>();
        macrophytes.add(new Macrophyte("Azolla pinnata",
                "Free floationg",
                "feathered mosquitofern",
                "Used in constructed wetlands to remove nutrient load from wastewater." +
                        "It is a high protein food source waterbirds,fish,insects,snails and cattle. " +
                        "It has the nitrogen fixing cyanobacteria and can used as biofertilizer",
                R.drawable.sample_macrophyte,
                "https://commons.wikimedia.org/wiki/Category:Azolla_pinnata"));
        macrophytes.add(new Macrophyte("Azolla pinnata",
                "Free floationg",
                "feathered mosquitofern",
                "Used in constructed wetlands to remove nutrient load from wastewater." +
                        "It is a high protein food source waterbirds,fish,insects,snails and cattle. " +
                        "It has the nitrogen fixing cyanobacteria and can used as biofertilizer",
                R.drawable.sample_macrophyte,
                "https://commons.wikimedia.org/wiki/Category:Azolla_pinnata"));
        macrophytes.add(new Macrophyte("Azolla pinnata",
                "Free floationg",
                "feathered mosquitofern",
                "Used in constructed wetlands to remove nutrient load from wastewater." +
                        "It is a high protein food source waterbirds,fish,insects,snails and cattle. " +
                        "It has the nitrogen fixing cyanobacteria and can used as biofertilizer",
                R.drawable.sample_macrophyte,
                "https://commons.wikimedia.org/wiki/Category:Azolla_pinnata"));
        macrophytes.add(new Macrophyte("Azolla pinnata",
                "Free floationg",
                "feathered mosquitofern",
                "Used in constructed wetlands to remove nutrient load from wastewater." +
                        "It is a high protein food source waterbirds,fish,insects,snails and cattle. " +
                        "It has the nitrogen fixing cyanobacteria and can used as biofertilizer",
                R.drawable.sample_macrophyte,
                "https://commons.wikimedia.org/wiki/Category:Azolla_pinnata"));

        MacrophyteAdapter macrophyteAdapter = new MacrophyteAdapter(this,macrophytes);
        ListView listView = findViewById(R.id.macrophyte_list);
        listView.setAdapter(macrophyteAdapter);
        macrophyteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}