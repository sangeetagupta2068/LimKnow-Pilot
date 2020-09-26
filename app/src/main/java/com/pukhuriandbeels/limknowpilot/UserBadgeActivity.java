package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class UserBadgeActivity extends AppCompatActivity {
    private TextView lakeFinderTextView, lakeObserverTextView, lakeSaviourTextView, lakePhotographerTextView;
    private ImageView lakeFinderImageView, lakeObserverImageView, lakeSaviourImageView, lakePhotographerImageView;
    private TextView userNameTextView, userProfileTextView;
    private ImageView userProfileImageView;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private String userEmail;
    private Uri userProfileUri;
    private String userName;
    private StorageReference firebaseStorageReference;
    private CollectionReference collectionReference;
    private boolean isLakeFinder, isLakeObserver, isLakePhotographer, isLakeSaviour;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View header;
    private TextView userEmailTextView;
    private ImageView userImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_badge);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        header = navigationView.getHeaderView(0);
        userNameTextView = header.findViewById(R.id.user_name);
        userEmailTextView = header.findViewById(R.id.user_email);
        userImageView = header.findViewById(R.id.user_image);

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
                        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(getApplicationContext(), LakeActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.policy:
                        break;

                    case R.id.macrophytes:
                        Intent intent = new Intent(getApplicationContext(), MacrophyteListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.augmented_reality_game:
                        Intent lakeARIntent = new Intent(getApplicationContext(), LakeARQuizActivity.class);
                        startActivity(lakeARIntent);
                        break;
                    case R.id.reporting:
                        Intent intentCitizenScience = new Intent(getApplicationContext(), CitizenScienceActivity.class);
                        startActivity(intentCitizenScience);
                        break;

                    case R.id.badges:
                        Intent userBadgeIntent = new Intent(getApplicationContext(), UserBadgeActivity.class);
                        startActivity(userBadgeIntent);
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

                    case R.id.share:
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
                navigationView.setCheckedItem(R.id.home);
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navigationView.setCheckedItem(R.id.home);
        initialize();
        firebaseTransaction();
    }

    private void initialize() {
        lakeFinderTextView = findViewById(R.id.lake_finder);
        lakeObserverTextView = findViewById(R.id.lake_observer);
        lakeSaviourTextView = findViewById(R.id.lake_saviour);
        lakePhotographerTextView = findViewById(R.id.lake_photographer);

        lakeFinderImageView = findViewById(R.id.lake_finder_badge);
        lakeObserverImageView = findViewById(R.id.lake_observer_badge);
        lakeSaviourImageView = findViewById(R.id.lake_saviour_badge);
        lakePhotographerImageView = findViewById(R.id.lake_photographer_badge);

        userNameTextView = findViewById(R.id.user_profile_badge_name);
        userProfileTextView = findViewById(R.id.user_profile__badge_email);

        userProfileImageView = findViewById(R.id.user_profile__badge_picture);

        isLakeFinder = false;
        isLakeObserver = false;
        isLakePhotographer = false;
        isLakeSaviour = false;

        lakePhotographerImageView.setVisibility(View.GONE);
        lakePhotographerTextView.setVisibility(View.GONE);
        lakeSaviourImageView.setVisibility(View.GONE);
        lakeSaviourTextView.setVisibility(View.GONE);
        lakeObserverImageView.setVisibility(View.GONE);
        lakeObserverTextView.setVisibility(View.GONE);
        lakeFinderImageView.setVisibility(View.GONE);
        lakeFinderTextView.setVisibility(View.GONE);
    }

    private void firebaseTransaction() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
            userProfileUri = firebaseUser.getPhotoUrl();
            userName = firebaseUser.getDisplayName();

            userNameTextView.setText(userName);
            userProfileTextView.setText(userEmail);

            Glide.with(getApplicationContext()).load(userProfileUri).error(R.drawable.ic_baseline_person_24).into(userProfileImageView);
            Glide.with(this).load(userProfileUri).error(R.drawable.ic_baseline_person_24).apply(RequestOptions.circleCropTransform()).into(userImageView);

            userEmailTextView.setText(userEmail);
            userNameTextView.setText(userName);
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
        collectionReference = firebaseFirestore.collection("User");

        if (firebaseUser != null) {
            collectionReference.document(firebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        isLakeFinder = documentSnapshot.getBoolean("is_lake_finder");
                        isLakeObserver = documentSnapshot.getBoolean("is_lake_observer");
                        isLakePhotographer = documentSnapshot.getBoolean("is_lake_photographer");
                        isLakeSaviour = documentSnapshot.getBoolean("is_lake_saviour");
                    } else {
                        isLakeFinder = false;
                        isLakeObserver = false;
                        isLakePhotographer = false;
                        isLakeSaviour = false;
                    }

                    if (isLakeFinder) {
                        lakeFinderImageView.setVisibility(View.VISIBLE);
                        lakeFinderTextView.setVisibility(View.VISIBLE);
                    }
                    if (isLakeObserver) {
                        lakeObserverImageView.setVisibility(View.VISIBLE);
                        lakeObserverTextView.setVisibility(View.VISIBLE);
                    }
                    if (isLakePhotographer) {
                        lakePhotographerImageView.setVisibility(View.VISIBLE);
                        lakePhotographerTextView.setVisibility(View.VISIBLE);
                    }
                    if(isLakeSaviour){
                        lakeSaviourImageView.setVisibility(View.VISIBLE);
                        lakeSaviourTextView.setVisibility(View.VISIBLE);
                    }
                }
            });

            collectionReference.document(firebaseUser.getEmail()).get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    lakePhotographerImageView.setVisibility(View.GONE);
                    lakePhotographerTextView.setVisibility(View.GONE);
                    lakeSaviourImageView.setVisibility(View.GONE);
                    lakeSaviourTextView.setVisibility(View.GONE);
                    lakeObserverImageView.setVisibility(View.GONE);
                    lakeObserverTextView.setVisibility(View.GONE);
                    lakeFinderImageView.setVisibility(View.GONE);
                    lakeFinderTextView.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Failed to load details.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

        @Override
        public void onBackPressed () {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }
    }