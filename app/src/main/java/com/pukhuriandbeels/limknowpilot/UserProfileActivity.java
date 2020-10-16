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
    private Button buttonSave, buttonCancel;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    //Firebase Auth declaration
    private FirebaseAuth firebaseAuth;
    private CollectionReference collectionReference;
    private StorageReference firebaseStorageReference;
    private FirebaseUser firebaseUser;

    //Field declaration
    private String userEmail, userName, userProfession, userAbout, userGender;
    private String newUserAbout, newGender, newProfession, newAge, newIndex;
    private String userAge;
    private Uri userProfileUri;
    private Uri filePath;
    private String index;
    private String cancelIndex;

    //Profile flag declaration
    private boolean isLakeFinder, isLakeObserver, isLakeSaviour, isLakePhotographer;
    private boolean flagCancel;


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
        progressBar = findViewById(R.id.user_profile_connection_status);
        constraintLayout = findViewById(R.id.constraint_layout_profile);

        mAboutEditText = findViewById(R.id.about);
        mProfessionEditText = findViewById(R.id.profession);
        mAgeEditText = findViewById(R.id.age);
        mPronounEditText = findViewById(R.id.gender);

        buttonCancel = findViewById(R.id.button_user_profile_cancel);
        buttonSave = findViewById(R.id.button_user_profile_save);

        //Set view visibility
        mAboutEditText.setVisibility(View.GONE);
        mPronounEditText.setVisibility(View.GONE);
        mProfessionEditText.setVisibility(View.GONE);
        mAgeEditText.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        buttonSave.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);

        //If parent activity is Sign in, don't display Cancel Button
        Intent intent = getIntent();
        if(intent.getStringExtra("KEY")!=null){
            flagCancel = false;
        } else {
            flagCancel = true;
        }

        //Set badge flag values to false until data is loaded
        isLakeFinder = false;
        isLakeObserver = false;
        isLakePhotographer = false;
        isLakeSaviour = false;

    }

    private void setListeners() {
        //Attaching listener to button for selecting profile picture
        mCameraFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_REQUEST);
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
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Nothing to change", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Attaching listener to Save Button to create Firebase Auth request
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Retriving values for user fields
                newUserAbout = mAboutEditText.getText().toString();
                newAge = mAgeEditText.getText().toString();
                newProfession = mProfessionEditText.getText().toString();

                //Age validation
                if(!newAge.equals("")){
                    int age = Integer.valueOf(newAge);
                    if(age > 100 || age < 1){
                        Toast.makeText(getApplicationContext(),"Please add a valid age",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (filePath == userProfileUri) {
                    //If profile photo wasn't changed, create firebase request to upload other details of user
                    firebaseTransaction();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    //Upload user profile picture to Cloud Storage if profile photo was changed
                    final StorageReference storageReference = firebaseStorageReference.child("User Profile").child(filePath.getLastPathSegment());
                    //On Successful upload of user profile picture
                    storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    filePath = uri;
                                    //Create request to upload other details of user
                                    firebaseTransaction();
                                }
                            });
                        }
                    });
                    //On failed upload of user profile picture
                    storageReference.putFile(filePath).addOnFailureListener(new OnFailureListener() {
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
        String[] items = {"None","He/Him", "She/Her", "They/Them"};
        if(cancelIndex.equals("")|| cancelIndex == null){
            cancelIndex = "0";
        }
        if (index.equals("") || index == null) {
            index = "0";
        }
        int check = Integer.parseInt(index);
        alertDialog.setSingleChoiceItems(items, check, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newIndex = Integer.toString(which);
                switch (which) {
                    case 0:
                        newGender = items[0];
                        index = "0";
                        break;
                    case 1:
                        newGender = items[1];
                        index = "1";
                        break;
                    case 2:
                        newGender = items[2];
                        index = "2";
                        break;
                    case 3:
                        newGender = items[3];
                        index = "3";
                        break;
                }
            }
        });
        //Save selected pronoun to preview in user pronoun section
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(!newGender.equals("None"))
                    mPronounEditText.setText(newGender);
                else
                    mPronounEditText.setText("");
                cancelIndex = index;
                dialog.dismiss();
            }
        });
        //Cancel pronoun selection
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newGender = mPronounEditText.getHint().toString();
                index = cancelIndex;
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
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Check if app has permission to pick image from user gallery
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                //Preview selected image in user profile pic
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                mProfilePictureImageView.setVisibility(View.VISIBLE);
                mProfilePictureImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //Check if app has been granted permission to pick image from user gallery after requesting
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Gallery Permission denied", Toast.LENGTH_SHORT).show();
            } else {
                selectImage();
            }
        }
    }

    private void setFirebaseAuthorizedUser() {
        //Firebase Auth instantiation
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        //If firebase user exists, load user details
        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
            userProfileUri = firebaseUser.getPhotoUrl();
            userName = firebaseUser.getDisplayName();

            mNameTextView.setText(userName);
            mEmailTextView.setText(userEmail);

            Glide.with(getApplicationContext()).load(userProfileUri).error(R.drawable.ic_baseline_person_24).into(mProfilePictureImageView);
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
        collectionReference = firebaseFirestore.collection("User");

        if (firebaseUser != null) {
            //On successful retrieval of user details, set fields to loaded values
            collectionReference.document(firebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if(documentSnapshot.contains("about")) {
                            userAbout = documentSnapshot.getString("about");
                        }
                        if(documentSnapshot.contains("profession")) {
                            userProfession = documentSnapshot.getString("profession");
                        }
                        if(documentSnapshot.contains("pronoun")) {
                            userGender = documentSnapshot.getString("pronoun");
                        }
                        if(documentSnapshot.contains("age")) {
                            userAge = documentSnapshot.getString("age");
                        }
                        if(documentSnapshot.contains("index")) {
                            index = documentSnapshot.getString("index");
                            cancelIndex = index;
                        }

                        if(documentSnapshot.contains("is_lake_finder")) {
                            isLakeFinder = documentSnapshot.getBoolean("is_lake_finder");
                        }
                        if(documentSnapshot.contains("is_lake_observer")){
                            isLakeObserver = documentSnapshot.getBoolean("is_lake_observer");
                        }
                        if(documentSnapshot.contains("is_lake_photographer")){
                            isLakePhotographer = documentSnapshot.getBoolean("is_lake_photographer");
                        }
                        if(documentSnapshot.contains("is_lake_saviour")){
                            isLakeSaviour = documentSnapshot.getBoolean("is_lake_saviour");
                        }

                    } else {
                        isLakeFinder = false;
                        isLakeObserver = false;
                        isLakePhotographer = false;
                        isLakeSaviour = false;

                        userAbout = "";
                        userProfession = "";
                        userGender = "";
                        userAge = "";
                        index = "0";
                        cancelIndex = "0";
                    }

                    newUserAbout = userAbout;
                    newGender = userGender;
                    newProfession = userProfession;
                    newAge = userAge;
                    newIndex = index;
                    filePath = userProfileUri;

                    if(!userGender.equals("None")) {
                        mPronounEditText.setText(userGender);
                    } else {
                        mPronounEditText.setText("");
                    }
                    mAgeEditText.setText(userAge);
                    mProfessionEditText.setText(userProfession);
                    mAboutEditText.setText(userAbout);

                    mPronounEditText.setFocusable(false);
                    mPronounEditText.setClickable(true);

                    mAboutEditText.setVisibility(View.VISIBLE);
                    mPronounEditText.setVisibility(View.VISIBLE);
                    mProfessionEditText.setVisibility(View.VISIBLE);
                    mAgeEditText.setVisibility(View.VISIBLE);
                    if(flagCancel) {
                        buttonCancel.setVisibility(View.VISIBLE);
                    }
                    buttonSave.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });

            //On failed retrieval of user details, set fields to default values
            collectionReference.document(firebaseUser.getEmail()).get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    userAbout = "";
                    userProfession = "";
                    userGender = "";
                    userAge = "";
                    index = "0";
                    cancelIndex = "0";

                    newUserAbout = userAbout;
                    newGender = userGender;
                    newProfession = userProfession;
                    newAge = userAge;
                    newIndex = index;
                    filePath = userProfileUri;

                    mPronounEditText.setText(userGender);
                    mAgeEditText.setText(userAge);
                    mProfessionEditText.setText(userProfession);
                    mAboutEditText.setText(userAbout);

                    mPronounEditText.setFocusable(false);
                    mPronounEditText.setClickable(true);

                    mAboutEditText.setVisibility(View.VISIBLE);
                    mPronounEditText.setVisibility(View.VISIBLE);
                    mProfessionEditText.setVisibility(View.VISIBLE);
                    mAgeEditText.setVisibility(View.VISIBLE);

                    if(flagCancel) {
                        buttonCancel.setVisibility(View.VISIBLE);
                    }
                    buttonSave.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Failed to load details.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void firebaseTransaction() {
        //If field values weren't changed, cancel user details upload request
        if (newAge.equals(userAge) && newGender.equals(userGender) && newProfession.equals(userProfession) && newUserAbout.equals(userAbout) && userProfileUri == filePath) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Nothing to change", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        //On changed user field values, create request to upload user details
        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> user = new HashMap<>();
        user.put("about", newUserAbout);
        user.put("pronoun", newGender);
        user.put("profession", newProfession);
        user.put("age", newAge);
        user.put("index", newIndex);
        user.put("image_url", filePath.toString());
        user.put("name", userName);
        user.put("is_lake_finder",isLakeFinder);
        user.put("is_lake_observer",isLakeObserver);
        user.put("is_lake_photographer",isLakePhotographer);
        user.put("is_lake_saviour",isLakeSaviour);

        if (filePath != userProfileUri) {
            //Change profile picture of user
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(filePath)
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           Log.i("PROFILE_PHOTO_UPDATED","Updates profile photo");
                        }
                    });
        }

        //On success of adding user details to User collection
        collectionReference.document(userEmail).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Your profile has been updated.", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(50);

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unexpected error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //On Failed of adding user details to User collection
        collectionReference.document(userEmail).set(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //Display default Home Activity on press of back button
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(intent);
        finish();
    }
}