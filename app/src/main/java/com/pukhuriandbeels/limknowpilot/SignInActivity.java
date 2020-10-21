package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
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

    //Firebase authentication declaration
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_in);

        initialize();
        setListners();
    }

    private void initialize() {
        //View initialization
        mGoogleSignInButton = findViewById(R.id.sign_in_button);
        mProgressBar = findViewById(R.id.badge_connection_status);
        mProgressBar.setVisibility(View.GONE);

        //Set Lake AR quiz to first question
        LakeARActivity.count = 0;

        //instantiate Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Configure Google Sign In client
        GoogleSignInOptions googleSignInOptions = new
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("376214147539-43mekerd0d50kumvmpp1te4irqe8fv1o.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    private void setListners() {
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
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),
                null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //On successful user authentication
                        if (task.isSuccessful()) {
                            FirebaseUser user = mFirebaseAuth.getCurrentUser();
                            CollectionReference collectionReference = FirebaseFirestore
                                    .getInstance().collection("User");

                            //If user already exists, launch Home Activity
                            collectionReference.document(
                                    user.getEmail())
                                    .get().addOnSuccessListener(
                                    new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            mProgressBar.setVisibility(View.GONE);
                                            Intent intent = new Intent(getApplicationContext(),
                                                    HomeActivity.class);
                                            startActivity(intent);
                                            Toast.makeText(getApplicationContext(),
                                                    "Welcome back to LimKnow, "
                                                            + user.getDisplayName(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                            //If user signs in for the first time, launch User Profile Activity to collect user details
                            collectionReference.document(user.getEmail())
                                    .get().addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Intent intent = new Intent(
                                            getApplicationContext(), UserProfileActivity.class);
                                    intent.putExtra("KEY", 1);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Welcome to LimKnow, " +
                                                    user.getDisplayName(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            //On failed user authentication
                            mProgressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this,
                                    "Sorry authentication failed.",
                                    Toast.LENGTH_SHORT).show();
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

        //If user hasn't Signed out, go to Home Activity directly
        FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}