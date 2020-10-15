package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private String userEmail, userName;
    private FirebaseAuth firebaseAuth;
    private Uri uriProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citizen_science);
        userName = "";
        userEmail = "";
        setFirebaseAuthorizedUser();

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        View header = navigationView.getHeaderView(0);
        TextView userNameTextView = header.findViewById(R.id.user_name);
        TextView userEmailTextView = header.findViewById(R.id.user_email);
        ImageView userImageView = header.findViewById(R.id.user_image);

        Glide.with(this).load(uriProfilePicture).error(R.drawable.ic_baseline_person_24).apply(RequestOptions.circleCropTransform()).into(userImageView);

        userEmailTextView.setText(userEmail);
        userNameTextView.setText(userName);

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
                        Intent intent = new Intent(CitizenScienceActivity.this, HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(CitizenScienceActivity.this, BeelsAndPukhurisActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.policy:
                        Intent policyIntent = new Intent(CitizenScienceActivity.this, PrivacyActivity.class);
                        startActivity(policyIntent);
                        break;

                    case R.id.macrophytes:
                        Intent macrophyteIntent = new Intent(CitizenScienceActivity.this, MacrophyteListActivity.class);
                        startActivity(macrophyteIntent);
                        finish();
                        break;
                    case R.id.augmented_reality_game:
                        Intent lakeARIntent = new Intent(CitizenScienceActivity.this, LakeARQuizActivity.class);
                        startActivity(lakeARIntent);
                        break;
                    case R.id.reporting:
                        break;

                    case R.id.badges:
                        Intent userBadgeIntent = new Intent(getApplicationContext(),UserBadgeActivity.class);
                        startActivity(userBadgeIntent);
                        finish();
                        break;
                    case R.id.edit_profile:
                        Intent profileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.sign_out:
                        FirebaseAuth.getInstance().signOut();
                        Intent signOutIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(signOutIntent);
                        break;
                    case R.id.contact_us:
                        Intent contactIntent = new Intent(Intent.ACTION_SEND);

                        contactIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"limknow2020@gmail.com"});
                        contactIntent.putExtra(Intent.EXTRA_SUBJECT, "Query from " + userName);

                        contactIntent.setType("message/rfc822");
                        contactIntent.setPackage("com.google.android.gm");

                        if (contactIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(contactIntent);
                        }

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

    private void setFirebaseAuthorizedUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
            userName = firebaseUser.getDisplayName();
            uriProfilePicture = firebaseUser.getPhotoUrl();
        }
    }
}