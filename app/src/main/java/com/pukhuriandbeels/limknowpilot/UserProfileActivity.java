package com.pukhuriandbeels.limknowpilot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UserProfileActivity extends AppCompatActivity {
    //flag for selecting image from internal storage
    private static final int PICK_IMAGE_REQUEST = 501;

    //view declaration
    ShapeableImageView mProfilePictureImageView;
    private TextView mNameTextView, mEmailTextView;
    private FloatingActionButton mCameraFloatingActionButton;
    private TextInputEditText mAboutEditText, mProfessionEditText, mAgeEditText, mPronounEditText;
    private Button mSaveButton, mCancelButton;
    private ProgressBar mProgressBar;
    private ConstraintLayout mConstraintLayout;

    //Firebase Auth declaration
    private FirebaseAuth mFirebaseAuth;
    private CollectionReference mCollectionReference;
    private StorageReference mFirebaseStorageReference;
    private FirebaseUser mFirebaseUser;

    //Field declaration
    private String mUserEmail, mUserName, mUserProfession, mUserAbout,
            mUserPronoun, mUserAge, mUserPronounIndex;
    private String mNewUserAbout, mNewUserPronoun, mNewUserProfession,
            mNewUserAge, mNewUserPronounIndex;
    private Uri mUserProfileUri;
    private Uri mFilePath;
    private String mUserCancelIndex;

    //Profile flag declaration
    private boolean mIsLakeFinder, mIsLakeObserver, mIsLakeSaviour, mIsLakePhotographer;
    private boolean mScreenflagForCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initialize();
        setFirebaseAuthorizedUser();
        setListeners();
    }

    private void initialize() {
        //Initialize views
        mProfilePictureImageView = findViewById(R.id.user_profile_picture);
        mEmailTextView = findViewById(R.id.user_profile_email);
        mNameTextView = findViewById(R.id.user_profile_name);
        mCameraFloatingActionButton = findViewById(R.id.floatingActionButton);
        mProgressBar = findViewById(R.id.user_profile_connection_status);
        mConstraintLayout = findViewById(R.id.constraint_layout_profile);

        mAboutEditText = findViewById(R.id.about);
        mProfessionEditText = findViewById(R.id.profession);
        mAgeEditText = findViewById(R.id.age);
        mPronounEditText = findViewById(R.id.gender);

        mCancelButton = findViewById(R.id.button_user_profile_cancel);
        mSaveButton = findViewById(R.id.button_user_profile_save);

        //Set view visibility
        mAboutEditText.setVisibility(View.GONE);
        mPronounEditText.setVisibility(View.GONE);
        mProfessionEditText.setVisibility(View.GONE);
        mAgeEditText.setVisibility(View.GONE);
        mCancelButton.setVisibility(View.GONE);
        mSaveButton.setVisibility(View.GONE);
        mConstraintLayout.setVisibility(View.GONE);

        mProgressBar.setVisibility(View.VISIBLE);

        //If parent activity is Sign in, don't display Cancel Button
        Intent intent = getIntent();
        if (intent.getStringExtra("KEY") != null) {
            mScreenflagForCancel = false;
        } else {
            mScreenflagForCancel = true;
        }

        //Set badge flag values to false until data is loaded
        mIsLakeFinder = false;
        mIsLakeObserver = false;
        mIsLakePhotographer = false;
        mIsLakeSaviour = false;

    }

    private void setListeners() {
        //Attaching listener to button for selecting profile picture
        mCameraFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UserProfileActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this,
                            new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_REQUEST);
                } else {
                    selectImage();
                }
            }
        });

        //Attaching listener to pronoun field to display dialog menu
        mPronounEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        //Attaching listener when Activity is cancelled
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Attaching listener to Save Button to create Firebase Auth request
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retriving values for user fields
                mNewUserAbout = mAboutEditText.getText().toString();
                mNewUserAge = mAgeEditText.getText().toString();
                mNewUserProfession = mProfessionEditText.getText().toString();

                //Age validation
                if (!mNewUserAge.equals("")) {
                    int age = Integer.valueOf(mNewUserAge);
                    if (age > 100 || age < 1) {
                        Toast.makeText(getApplicationContext(), "Please add a valid age",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (mFilePath == mUserProfileUri) {
                    //If profile photo wasn't changed, create firebase request to upload other details of user
                    firebaseTransaction();
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    //Upload user profile picture to Cloud Storage if profile photo was changed
                    final StorageReference storageReference =
                            mFirebaseStorageReference.child("User Profile")
                                    .child(mFilePath.getLastPathSegment());
                    //On Successful upload of user profile picture
                    storageReference.putFile(mFilePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    storageReference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    mFilePath = uri;
                                                    //Create request to upload other details of user
                                                    firebaseTransaction();
                                                }
                                            });
                                }
                            });
                    //On failed upload of user profile picture
                    storageReference.putFile(mFilePath)
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    //Create request to upload other details of user
                                    firebaseTransaction();
                                }
                            });
                }
            }
        });
    }

    private void showAlertDialog() {
        //Display Dialog for selecting user pronoun
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfileActivity.this);
        alertDialog.setTitle("Pronoun");
        String[] items = {"None", "He/Him", "She/Her", "They/Them"};
        if (mUserCancelIndex.equals("") || mUserCancelIndex == null) {
            mUserCancelIndex = "0";
        }
        if (mUserPronounIndex.equals("") || mUserPronounIndex == null) {
            mUserPronounIndex = "0";
        }
        int check = Integer.parseInt(mUserPronounIndex);
        alertDialog.setSingleChoiceItems(items, check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNewUserPronounIndex = Integer.toString(which);
                switch (which) {
                    case 0:
                        mNewUserPronoun = items[0];
                        mUserPronounIndex = "0";
                        break;
                    case 1:
                        mNewUserPronoun = items[1];
                        mUserPronounIndex = "1";
                        break;
                    case 2:
                        mNewUserPronoun = items[2];
                        mUserPronounIndex = "2";
                        break;
                    case 3:
                        mNewUserPronoun = items[3];
                        mUserPronounIndex = "3";
                        break;
                }
            }
        });
        //Save selected pronoun to preview in user pronoun section
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!mNewUserPronoun.equals("None"))
                    mPronounEditText.setText(mNewUserPronoun);
                else
                    mPronounEditText.setText("");
                mUserCancelIndex = mUserPronounIndex;
                dialog.dismiss();
            }
        });
        //Cancel pronoun selection
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNewUserPronoun = mPronounEditText.getHint().toString();
                mUserPronounIndex = mUserCancelIndex;
                dialog.dismiss();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void selectImage() {
        //Launch Gallery to pick image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture")
                , PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if app has permission to pick image from user gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mFilePath = data.getData();

            try {
                //Preview selected image in user profile pic
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mFilePath);
                mProfilePictureImageView.setVisibility(View.VISIBLE);
                mProfilePictureImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check if app has been granted permission to pick image from user gallery after requesting
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(),
                        "Gallery Permission denied", Toast.LENGTH_SHORT).show();
            } else {
                selectImage();
            }
        }
    }

    private void setFirebaseAuthorizedUser() {
        //Firebase Auth instantiation
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();

        //If firebase user exists, load user details
        if (mFirebaseUser != null) {
            mUserEmail = mFirebaseUser.getEmail();
            mUserProfileUri = mFirebaseUser.getPhotoUrl();
            mUserName = mFirebaseUser.getDisplayName();

            mNameTextView.setText(mUserName);
            mEmailTextView.setText(mUserEmail);

            Glide.with(getApplicationContext())
                    .load(mUserProfileUri)
                    .error(R.drawable.ic_baseline_person_24)
                    .into(mProfilePictureImageView);
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        mFirebaseStorageReference = FirebaseStorage.getInstance().getReference();
        mCollectionReference = firebaseFirestore.collection("User");

        if (mFirebaseUser != null) {
            //On successful retrieval of user details, set fields to loaded values
            mCollectionReference.document(mFirebaseUser.getEmail())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("about")) {
                            mUserAbout = documentSnapshot.getString("about");
                        }
                        if (documentSnapshot.contains("profession")) {
                            mUserProfession = documentSnapshot.getString("profession");
                        }
                        if (documentSnapshot.contains("pronoun")) {
                            mUserPronoun = documentSnapshot.getString("pronoun");
                        }
                        if (documentSnapshot.contains("age")) {
                            mUserAge = documentSnapshot.getString("age");
                        }
                        if (documentSnapshot.contains("index")) {
                            mUserPronounIndex = documentSnapshot.getString("index");
                            mUserCancelIndex = mUserPronounIndex;
                        }

                        if (documentSnapshot.contains("is_lake_finder")) {
                            mIsLakeFinder = documentSnapshot.getBoolean("is_lake_finder");
                        }
                        if (documentSnapshot.contains("is_lake_observer")) {
                            mIsLakeObserver = documentSnapshot.getBoolean("is_lake_observer");
                        }
                        if (documentSnapshot.contains("is_lake_photographer")) {
                            mIsLakePhotographer = documentSnapshot
                                    .getBoolean("is_lake_photographer");
                        }
                        if (documentSnapshot.contains("is_lake_saviour")) {
                            mIsLakeSaviour = documentSnapshot.getBoolean("is_lake_saviour");
                        }

                    } else {
                        mIsLakeFinder = false;
                        mIsLakeObserver = false;
                        mIsLakePhotographer = false;
                        mIsLakeSaviour = false;

                        mUserAbout = "";
                        mUserProfession = "";
                        mUserPronoun = "";
                        mUserAge = "";
                        mUserPronounIndex = "0";
                        mUserCancelIndex = "0";
                    }

                    mNewUserAbout = mUserAbout;
                    mNewUserPronoun = mUserPronoun;
                    mNewUserProfession = mUserProfession;
                    mNewUserAge = mUserAge;
                    mNewUserPronounIndex = mUserPronounIndex;
                    mFilePath = mUserProfileUri;

                    if (!mUserPronoun.equals("None")) {
                        mPronounEditText.setText(mUserPronoun);
                    } else {
                        mPronounEditText.setText("");
                    }
                    mAgeEditText.setText(mUserAge);
                    mProfessionEditText.setText(mUserProfession);
                    mAboutEditText.setText(mUserAbout);

                    mPronounEditText.setFocusable(false);
                    mPronounEditText.setClickable(true);

                    mAboutEditText.setVisibility(View.VISIBLE);
                    mPronounEditText.setVisibility(View.VISIBLE);
                    mProfessionEditText.setVisibility(View.VISIBLE);
                    mAgeEditText.setVisibility(View.VISIBLE);
                    if (mScreenflagForCancel) {
                        mCancelButton.setVisibility(View.VISIBLE);
                    }
                    mSaveButton.setVisibility(View.VISIBLE);
                    mConstraintLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            });

            //On failed retrieval of user details, set fields to default values
            mCollectionReference.document(mFirebaseUser.getEmail()).
                    get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mUserAbout = "";
                    mUserProfession = "";
                    mUserPronoun = "";
                    mUserAge = "";
                    mUserPronounIndex = "0";
                    mUserCancelIndex = "0";

                    mNewUserAbout = mUserAbout;
                    mNewUserPronoun = mUserPronoun;
                    mNewUserProfession = mUserProfession;
                    mNewUserAge = mUserAge;
                    mNewUserPronounIndex = mUserPronounIndex;
                    mFilePath = mUserProfileUri;

                    mPronounEditText.setText(mUserPronoun);
                    mAgeEditText.setText(mUserAge);
                    mProfessionEditText.setText(mUserProfession);
                    mAboutEditText.setText(mUserAbout);

                    mPronounEditText.setFocusable(false);
                    mPronounEditText.setClickable(true);

                    mAboutEditText.setVisibility(View.VISIBLE);
                    mPronounEditText.setVisibility(View.VISIBLE);
                    mProfessionEditText.setVisibility(View.VISIBLE);
                    mAgeEditText.setVisibility(View.VISIBLE);

                    if (mScreenflagForCancel) {
                        mCancelButton.setVisibility(View.VISIBLE);
                    }
                    mSaveButton.setVisibility(View.VISIBLE);
                    mConstraintLayout.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(),
                            "Failed to load details.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void firebaseTransaction() {
        //If field values weren't changed, cancel user details upload request
        if (mNewUserAge.equals(mUserAge) && mNewUserPronoun.equals(mUserPronoun) &&
                mNewUserProfession.equals(mUserProfession) &&
                mNewUserAbout.equals(mUserAbout) &&
                mUserProfileUri == mFilePath) {
            mProgressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),
                    "Nothing to change", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //On changed user field values, create request to upload user details
        mProgressBar.setVisibility(View.VISIBLE);
        Map<String, Object> user = new HashMap<>();
        user.put("about", mNewUserAbout);
        user.put("pronoun", mNewUserPronoun);
        user.put("profession", mNewUserProfession);
        user.put("age", mNewUserAge);
        user.put("index", mNewUserPronounIndex);
        user.put("image_url", mFilePath.toString());
        user.put("name", mUserName);
        user.put("is_lake_finder", mIsLakeFinder);
        user.put("is_lake_observer", mIsLakeObserver);
        user.put("is_lake_photographer", mIsLakePhotographer);
        user.put("is_lake_saviour", mIsLakeSaviour);

        if (mFilePath != mUserProfileUri) {
            //Change profile picture of user
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(mFilePath)
                    .build();

            mFirebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.i("PROFILE_PHOTO_UPDATED", "Updates profile photo");
                        }
                    });
        }

        //On success of adding user details to User collection
        mCollectionReference.document(mUserEmail).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),
                                "Your profile has been updated.", Toast.LENGTH_SHORT).show();
                        try {
                            Thread.sleep(50);

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    "Unexpected error occured", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //On Failed of adding user details to User collection
        mCollectionReference.document(mUserEmail).set(user)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(),
                                "Failed to update profile.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}