package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import com.google.firebase.firestore.QuerySnapshot;
import com.pukhuriandbeels.limknowpilot.adapter.MacrophyteAdapter;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MacrophyteListActivity extends AppCompatActivity {
    //View declaration
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ListView mMacrophyteListView;
    private ProgressBar mProgressBar;

    //Macrophyte Adapter declaration
    private MacrophyteAdapter mMacrophyteAdapter;

    //User field declaration
    private String userEmail, userName;
    private Uri uriProfilePicture;

    //Firebase Auth declaration
    private FirebaseAuth firebaseAuth;

    //Macrophyte collection declaration
    private List<Macrophyte> macrophytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_list);
        userName = "";
        userEmail = "";
        macrophytes = new ArrayList<>();
        setFirebaseAuthorizedUser();

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        View header = mNavigationView.getHeaderView(0);
        TextView userNameTextView = header.findViewById(R.id.user_name);
        TextView userEmailTextView = header.findViewById(R.id.user_email);
        ImageView userImageView = header.findViewById(R.id.user_image);

        userEmailTextView.setText(userEmail);
        userNameTextView.setText(userName);
        Glide.with(this).load(uriProfilePicture).error(R.drawable.ic_baseline_person_24).apply(RequestOptions.circleCropTransform()).into(userImageView);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        mNavigationView.bringToFront();
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                        finish();
                        break;
                    case R.id.about:
                        Intent aboutIntent = new Intent(MacrophyteListActivity.this, BeelsAndPukhurisActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.policy:
                        Intent policyIntent = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                        startActivity(policyIntent);
                        break;

                    case R.id.macrophytes:
                        break;
                    case R.id.augmented_reality_game:
                        Intent lakeARIntent = new Intent(MacrophyteListActivity.this, LakeARQuizActivity.class);
                        startActivity(lakeARIntent);
                        break;
                    case R.id.reporting:
                        Intent citizenScienceIntent = new Intent(MacrophyteListActivity.this, CitizenScienceActivity.class);
                        startActivity(citizenScienceIntent);
                        finish();
                        break;

                    case R.id.badges:
                        Intent userBadgeIntent = new Intent(getApplicationContext(),UserBadgeActivity.class);
                        startActivity(userBadgeIntent);
                        finish();
                        break;
                    case R.id.edit_profile:
                        Intent editProfileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        startActivity(editProfileIntent);
                        break;
                    case R.id.sign_out:
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
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mNavigationView.setCheckedItem(R.id.macrophytes);
        mMacrophyteAdapter = new MacrophyteAdapter(this, macrophytes);
        mMacrophyteListView = findViewById(R.id.macrophyte_list);
        mProgressBar = findViewById(R.id.macrophyte_list_connection_status);
        mProgressBar.setVisibility(View.VISIBLE);
        mMacrophyteListView.setVisibility(View.GONE);

        setFirebaseFirestoreTransaction();

        mMacrophyteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Macrophyte macrophyte = macrophytes.get(position);
                Intent intent = new Intent(MacrophyteListActivity.this, MacrophyteItemActivity.class);
                intent.putExtra("MACROPHYTE_ITEM", macrophyte);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            finish();
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

    private void setFirebaseFirestoreTransaction() {

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Macrophytes");

        collectionReference.orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    String name = documentSnapshot.getString("name");
                    String commonName = documentSnapshot.getString("common_name");
                    String type = documentSnapshot.getString("type");
                    String imageURL = documentSnapshot.getString("image_url");
                    boolean invasiveSpecies = documentSnapshot.getBoolean("invasive_species");
                    String imageCredits = documentSnapshot.getString("image_credits");
                    String description = documentSnapshot.getString("about");

                    Macrophyte macrophyte = new Macrophyte(name, type, commonName, description, imageURL, imageCredits, invasiveSpecies);
                    macrophytes.add(macrophyte);
                }

                mMacrophyteAdapter = new MacrophyteAdapter(getApplicationContext(), macrophytes);
                mMacrophyteListView.setAdapter(mMacrophyteAdapter);
                mMacrophyteListView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        });
        collectionReference.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    protected void onStop() {
        mNavigationView.setCheckedItem(R.id.macrophytes);
        super.onStop();
    }
}