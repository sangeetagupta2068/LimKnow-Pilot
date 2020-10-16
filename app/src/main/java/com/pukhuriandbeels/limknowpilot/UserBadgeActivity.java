package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
    //Badge view declaration
    private TextView mLakeFinderTextView, mLakeObserverTextView, mLakeSaviourTextView, mLakePhotographerTextView;
    private ImageView mLakeFinderImageView, mLakeObserverImageView, mLakeSaviourImageView, mLakePhotographerImageView;

    //User view declaration
    private TextView mUserNameTextView, mUserProfileTextView;
    private ImageView mUserProfileImageView;

    //Progress bar declaration
    private ProgressBar mProgressBar;

    //User header declaration
    private TextView mUserEmailTextView;
    private ImageView mUserImageView;
    private TextView mUserNameView;

    //Drawer view declaration
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private Toolbar mToolbar;
    private View mHeader;

    //Firebase user declaration
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    //Cloud Firestore declaration
    private StorageReference mFirebaseStorageReference;
    private CollectionReference mCollectionReference;

    //User field declaration
    private String mUserEmail;
    private Uri mUserProfileUri;
    private String mUserName;

    //Badge flag declaration
    private boolean mIsLakeFinder, mIsLakeObserver, mIsLakePhotographer, mIsLakeSaviour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_badge);
        initialize();
        setListeners();
        firebaseTransaction();
    }

    private void initialize() {
        //Drawer view initialization
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        mHeader = mNavigationView.getHeaderView(0);
        mUserNameView = mHeader.findViewById(R.id.user_name);
        mUserEmailTextView = mHeader.findViewById(R.id.user_email);
        mUserImageView = mHeader.findViewById(R.id.user_image);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        mNavigationView.bringToFront();

        //View initialization for badge labels
        mLakeFinderTextView = findViewById(R.id.lake_finder);
        mLakeObserverTextView = findViewById(R.id.lake_observer);
        mLakeSaviourTextView = findViewById(R.id.lake_saviour);
        mLakePhotographerTextView = findViewById(R.id.lake_photographer);

        //View initialization for badges
        mLakeFinderImageView = findViewById(R.id.lake_finder_badge);
        mLakeObserverImageView = findViewById(R.id.lake_observer_badge);
        mLakeSaviourImageView = findViewById(R.id.lake_saviour_badge);
        mLakePhotographerImageView = findViewById(R.id.lake_photographer_badge);

        //View initialization for user
        mUserNameTextView = findViewById(R.id.user_profile_badge_name);
        mUserProfileTextView = findViewById(R.id.user_profile__badge_email);
        mUserProfileImageView = findViewById(R.id.user_profile__badge_picture);

        //View initialization for progress bar
        mProgressBar = findViewById(R.id.badge_connection_status);

        //Setting flag values
        mIsLakeFinder = false;
        mIsLakeObserver = false;
        mIsLakePhotographer = false;
        mIsLakeSaviour = false;

        //Setting view visibility
        mLakePhotographerImageView.setVisibility(View.GONE);
        mLakePhotographerTextView.setVisibility(View.GONE);
        mLakeSaviourImageView.setVisibility(View.GONE);
        mLakeSaviourTextView.setVisibility(View.GONE);
        mLakeObserverImageView.setVisibility(View.GONE);
        mLakeObserverTextView.setVisibility(View.GONE);
        mLakeFinderImageView.setVisibility(View.GONE);
        mLakeFinderTextView.setVisibility(View.GONE);

        //Setting badge view colors to monochrome
        setImageColor(0, mLakeFinderImageView.getId());
        setImageColor(0, mLakeObserverImageView.getId());
        setImageColor(0, mLakePhotographerImageView.getId());
        setImageColor(0, mLakeSaviourImageView.getId());
    }

    private void setListeners() {
        //Attaching listener to drawer navigation
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        //Launch Home Activity
                        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(homeIntent);
                        finish();
                        break;
                    case R.id.about:
                        //Launch Beels and Pukhuris Activity
                        Intent aboutIntent = new Intent(getApplicationContext(), BeelsAndPukhurisActivity.class);
                        startActivity(aboutIntent);
                        break;
                    case R.id.policy:
                        //Launch Privacy Policy Activity
                        Intent policyIntent = new Intent(getApplicationContext(), PrivacyPolicyActivity.class);
                        startActivity(policyIntent);
                        break;

                    case R.id.macrophytes:
                        //Launch Macrophyte List Activity (Aquatic plants)
                        Intent intent = new Intent(getApplicationContext(), MacrophyteListActivity.class);
                        startActivity(intent);
                        break;
                    case R.id.augmented_reality_game:
                        //Launch Lake AR Quiz Activity (Lake AR)
                        Intent lakeARIntent = new Intent(getApplicationContext(), LakeARQuizActivity.class);
                        startActivity(lakeARIntent);
                        break;
                    case R.id.reporting:
                        //Launch Citizen Science Menu Activity
                        Intent intentCitizenScience = new Intent(getApplicationContext(), CitizenScienceActivity.class);
                        startActivity(intentCitizenScience);
                        break;

                    case R.id.badges:
                        Intent userBadgeIntent = new Intent(getApplicationContext(), UserBadgeActivity.class);
                        startActivity(userBadgeIntent);
                        break;
                    case R.id.edit_profile:
                        //Launch User Profile Activity
                        Intent profileIntent = new Intent(getApplicationContext(), UserProfileActivity.class);
                        startActivity(profileIntent);
                        break;
                    case R.id.sign_out:
                        //Sign out User
                        FirebaseAuth.getInstance().signOut();
                        Intent signOutIntent = new Intent(getApplicationContext(), SignInActivity.class);
                        startActivity(signOutIntent);
                        break;
                    case R.id.contact_us:
                        //Set mail field values
                        String contactEmail = getResources().getString(R.string.contact);
                        String messageType = getResources().getString(R.string.message_type);
                        String packageName = getResources().getString(R.string.package_name);

                        //Launch Gmail Compose email
                        Intent contactIntent = new Intent(Intent.ACTION_SEND);

                        contactIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{contactEmail});
                        contactIntent.putExtra(Intent.EXTRA_SUBJECT, "Query from " + mUserName);
                        contactIntent.setType(messageType);
                        contactIntent.setPackage(packageName);

                        if (contactIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(contactIntent);
                        }
                        break;

                }
                mNavigationView.setCheckedItem(R.id.badges);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        mNavigationView.setCheckedItem(R.id.badges);
    }

    private void firebaseTransaction() {
        //Firebase Auth initialization
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        if (mFirebaseUser != null) {
            //If user exists, initialize user fields
            mUserEmail = mFirebaseUser.getEmail();
            mUserProfileUri = mFirebaseUser.getPhotoUrl();
            mUserName = mFirebaseUser.getDisplayName();

            //User header view initialization
            mUserNameView.setText(mUserName);
            mUserEmailTextView.setText(mUserEmail);
            Glide.with(this).load(mUserProfileUri).error(R.drawable.ic_baseline_person_24).apply(RequestOptions.circleCropTransform()).into(mUserImageView);

            //User view initialization
            mUserProfileTextView.setText(mUserEmail);
            mUserNameTextView.setText(mUserName);
            Glide.with(getApplicationContext()).load(mUserProfileUri).error(R.drawable.ic_baseline_person_24).into(mUserProfileImageView);
        }

        //Cloud Firestore initialization
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorageReference = FirebaseStorage.getInstance().getReference();
        mCollectionReference = firebaseFirestore.collection("User");

        //If user exists, fetch badge details for user
        if (mFirebaseUser != null) {
            mCollectionReference.document(mFirebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("is_lake_finder")) {
                            mIsLakeFinder = documentSnapshot.getBoolean("is_lake_finder");
                        }
                        if (documentSnapshot.contains("is_lake_observer")) {
                            mIsLakeObserver = documentSnapshot.getBoolean("is_lake_observer");
                        }
                        if (documentSnapshot.contains("is_lake_photographer")) {
                            mIsLakePhotographer = documentSnapshot.getBoolean("is_lake_photographer");
                        }
                        if (documentSnapshot.contains("is_lake_saviour")) {
                            mIsLakeSaviour = documentSnapshot.getBoolean("is_lake_saviour");
                        }

                    } else {
                        mIsLakeFinder = false;
                        mIsLakeObserver = false;
                        mIsLakePhotographer = false;
                        mIsLakeSaviour = false;
                    }

                    //If user has earned badges, set badge colors to colored
                    if (mIsLakeFinder) {
                        setImageColor(1, mLakeFinderImageView.getId());
                    }
                    if (mIsLakeObserver) {
                        setImageColor(1, mLakeObserverImageView.getId());
                    }
                    if (mIsLakePhotographer) {
                        setImageColor(1, mLakePhotographerImageView.getId());
                    }
                    if (mIsLakeSaviour) {
                        setImageColor(1, mLakeSaviourImageView.getId());
                    }

                    //Set visibility of badges to true once user badge details are loaded
                    mLakeFinderImageView.setVisibility(View.VISIBLE);
                    mLakeFinderTextView.setVisibility(View.VISIBLE);
                    mLakeObserverImageView.setVisibility(View.VISIBLE);
                    mLakeObserverTextView.setVisibility(View.VISIBLE);
                    mLakePhotographerImageView.setVisibility(View.VISIBLE);
                    mLakePhotographerTextView.setVisibility(View.VISIBLE);
                    mLakeSaviourImageView.setVisibility(View.VISIBLE);
                    mLakeSaviourTextView.setVisibility(View.VISIBLE);

                    //Hide progress bar on completion of transaction
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            //If User badge details couldn't be loaded
            mCollectionReference.document(mFirebaseUser.getEmail()).get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Display badges in monochrome
                    mLakeFinderImageView.setVisibility(View.VISIBLE);
                    mLakeFinderTextView.setVisibility(View.VISIBLE);
                    mLakeObserverImageView.setVisibility(View.VISIBLE);
                    mLakeObserverTextView.setVisibility(View.VISIBLE);
                    mLakePhotographerImageView.setVisibility(View.VISIBLE);
                    mLakePhotographerTextView.setVisibility(View.VISIBLE);
                    mLakeSaviourImageView.setVisibility(View.VISIBLE);
                    mLakeSaviourTextView.setVisibility(View.VISIBLE);

                    //Display Toast message that badges couldn't be loaded
                    Toast.makeText(getApplicationContext(), "Failed to load details.", Toast.LENGTH_SHORT).show();

                    //Hide progress bar on completion of transaction
                    mProgressBar.setVisibility(View.GONE);

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //Configure drawer navigation
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            //On back button press, launch Home Activity
            super.onBackPressed();
            finish();
        }
    }

    private void setImageColor(int value, int id) {
        //Setting image color
        ImageView imageview = findViewById(id);
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(value);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageview.setColorFilter(filter);
    }

    @Override
    protected void onStop() {
        mNavigationView.setCheckedItem(R.id.badges);
        super.onStop();
    }
}