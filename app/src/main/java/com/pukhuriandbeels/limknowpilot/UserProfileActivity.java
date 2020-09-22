package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    ImageView imageViewProfile;
    private TextView textViewName, textViewEmail;

    private FirebaseAuth firebaseAuth;
    private String userEmail, userName;
    private Uri userProfileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setFirebaseAuthorizedUser();
        initialize();

    }

    private void initialize(){
        imageViewProfile = findViewById(R.id.user_profile_picture);
        textViewEmail = findViewById(R.id.user_profile_email);
        textViewName = findViewById(R.id.user_profile_name);

        textViewName.setText(userName);
        textViewEmail.setText(userEmail);
        Glide.with(this).load(userProfileUri).error(R.drawable.ic_baseline_person_24).apply(RequestOptions.circleCropTransform()).into(imageViewProfile);

    }

    private void setFirebaseAuthorizedUser(){
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
            userName = firebaseUser.getDisplayName();
            userProfileUri = firebaseUser.getPhotoUrl();
        } else {
            userEmail = "";
            userName = "";
            userProfileUri = null;
        }
    }
}