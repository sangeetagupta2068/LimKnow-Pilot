package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    //Sign in flag declaration
    private static final int RC_SIGN_IN = 1001;

    //View declaration
    private Button mGoogleSignInButton;
    private ProgressBar mProgressBar;
    private ImageView mLimKnowLogoImageView;
    private TextView mKnowYourLakesTextView, mLimKnowTextView;

    //Firebase authentication declaration
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        initialize();
        setListners();
    }

    private void initialize(){
        //Set views
        mGoogleSignInButton = findViewById(R.id.sign_in_button);
        mProgressBar = findViewById(R.id.badge_connection_status);
        mProgressBar.setVisibility(View.GONE);
        mLimKnowLogoImageView = findViewById(R.id.cover_image_view);
        mKnowYourLakesTextView = findViewById(R.id.sub_heading_text_view);
        mLimKnowTextView = findViewById(R.id.heading_text_view);

        //Set Lake AR quiz to first question
        LakeARActivity.count = 0;

        //instantiate Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Configure Google Sign In client
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .***REMOVED***
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void setListners(){
        //Attach on click listener to Sign In With Google button
        mGoogleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                signIn();
            }
        });
    }
    private void signIn() {
        //Launch intent for Google Sign in activity
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        //Authenticate Firebase user based on result returned from Google Sign In Intent
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //On successful user authentication
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("User");

                            //If user already exists, launch Home Activity
                            collectionReference.document(user.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    mProgressBar.setVisibility(View.GONE);
                                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                    startActivity(intent);
                                }
                            });

                            //If user signs in for the first time, launch User Profile Activity to collect user details
                            collectionReference.document(user.getEmail()).get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                                    intent.putExtra("KEY",1);
                                    startActivity(intent);
                                }
                            });

                        } else {
                            //On failed user authentication
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check if result was returned from Google Sign In Activity
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                //Authenticate user based on result returned from Google Sign In Activity
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        //If user hasn't Signed out, go to Home Screen directly
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //deallocate memory once activity stops
        mLimKnowLogoImageView.setImageDrawable(null);
        mKnowYourLakesTextView.setText(null);
        mLimKnowTextView.setText(null);
        mGoogleSignInButton.setText(null);
    }
}

