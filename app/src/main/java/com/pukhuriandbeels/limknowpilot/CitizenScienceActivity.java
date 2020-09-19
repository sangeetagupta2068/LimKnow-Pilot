package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class CitizenScienceActivity extends AppCompatActivity {
    private CardView[] cardViews;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_science);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        finish();
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(CitizenScienceActivity.this, LakeActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.policy:
                        break;

                    case R.id.macrophytes:
                        Intent intent = new Intent(CitizenScienceActivity.this, MacrophyteListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.augmented_reality_game:
                        Intent lakeARIntent = new Intent(CitizenScienceActivity.this, LakeARQuizActivity.class);
                        startActivity(lakeARIntent);
                        break;
                    case R.id.reporting:
                        break;

                    case R.id.badges:
                        break;
                    case R.id.edit_profile:
                        break;
                    case R.id.sign_out:
                        FirebaseAuth.getInstance().signOut();
                        Intent signOutIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(signOutIntent);
                        break;

                    case R.id.share:
                        break;
                    case R.id.contact_us:
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        navigationView.setCheckedItem(R.id.reporting);

        cardViews = new CardView[4];
        cardViews[0] = findViewById(R.id.cardview_1);
        cardViews[1] = findViewById(R.id.cardview_2);
        cardViews[2] = findViewById(R.id.cardview_3);
        cardViews[3] = findViewById(R.id.citizen_science_cardview_4);

        cardViews[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitizenScienceActivity.this, GeneralCitizenScienceActivity.class);
                startActivity(intent);
            }
        });

        cardViews[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitizenScienceActivity.this, InvasiveSpeciesWatchActivity.class);
                startActivity(intent);
            }
        });

        cardViews[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitizenScienceActivity.this, LakeHealthActivity.class);
                startActivity(intent);
            }
        });

        cardViews[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CitizenScienceActivity.this, BestShotActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}