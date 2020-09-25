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

    private static final int PICK_IMAGE_REQUEST = 501;
    ShapeableImageView imageViewProfile;
    private TextView textViewName, textViewEmail;
    private FloatingActionButton floatingActionButton;
    private TextInputEditText editTextAbout, editTextProfession, editTextAge, editTextGender;
    private Button buttonSave, buttonCancel;
    private ProgressBar progressBar;
    private ConstraintLayout constraintLayout;

    private FirebaseAuth firebaseAuth;
    private String userEmail, userName, userProfession, userAbout, userGender;
    private String newUserAbout, newGender, newProfession, newAge, newIndex;
    private String userAge;
    private Uri userProfileUri;
    private Uri filePath;
    private String index;
    private CollectionReference collectionReference;
    private StorageReference firebaseStorageReference;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        initialize();
        setFirebaseAuthorizedUser();
        setListeners();
    }

    private void initialize() {
        imageViewProfile = findViewById(R.id.user_profile_picture);
        textViewEmail = findViewById(R.id.user_profile_email);
        textViewName = findViewById(R.id.user_profile_name);
        floatingActionButton = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.user_profile_connection_status);
        constraintLayout = findViewById(R.id.constraint_layout_profile);

        editTextAbout = findViewById(R.id.about);
        editTextProfession = findViewById(R.id.profession);
        editTextAge = findViewById(R.id.age);
        editTextGender = findViewById(R.id.gender);

        buttonCancel = findViewById(R.id.button_user_profile_cancel);
        buttonSave = findViewById(R.id.button_user_profile_save);

        editTextAbout.setVisibility(View.GONE);
        editTextGender.setVisibility(View.GONE);
        editTextProfession.setVisibility(View.GONE);
        editTextAge.setVisibility(View.GONE);
        buttonCancel.setVisibility(View.GONE);
        buttonSave.setVisibility(View.GONE);
        constraintLayout.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
    }

    private void setListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(UserProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(UserProfileActivity.this, new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_REQUEST);
                } else {
                    selectImage();
                }
            }
        });

        editTextGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newUserAbout = editTextAbout.getText().toString();
                newAge = editTextAge.getText().toString();
                newProfession = editTextProfession.getText().toString();

                if (filePath == userProfileUri) {
                    firebaseTransaction();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    final StorageReference storageReference = firebaseStorageReference.child("User Profile").child(filePath.getLastPathSegment());
                    storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    filePath = uri;
                                    firebaseTransaction();
                                }
                            });
                        }
                    });
                    storageReference.putFile(filePath).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            firebaseTransaction();
                        }
                    });
                }
            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfileActivity.this);
        alertDialog.setTitle("Pronoun");
        String[] items = {"He/Him", "She/Her", "They/Them"};
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
                        break;
                    case 1:
                        newGender = items[1];
                        break;
                    case 2:
                        newGender = items[2];
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextGender.setText(newGender);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newGender = editTextGender.getHint().toString();
                dialog.dismiss();
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageViewProfile.setVisibility(View.VISIBLE);
                imageViewProfile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Gallery Permission denied", Toast.LENGTH_SHORT).show();
            } else {
                selectImage();
            }
        }
    }

    private void setFirebaseAuthorizedUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
            userProfileUri = firebaseUser.getPhotoUrl();
            userName = firebaseUser.getDisplayName();

            textViewName.setText(userName);
            textViewEmail.setText(userEmail);

            Glide.with(getApplicationContext()).load(userProfileUri).error(R.drawable.ic_baseline_person_24).into(imageViewProfile);
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
        collectionReference = firebaseFirestore.collection("User");

        if (firebaseUser != null) {
            collectionReference.document(firebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        userAbout = documentSnapshot.getString("about");
                        userProfession = documentSnapshot.getString("profession");
                        userGender = documentSnapshot.getString("pronoun");
                        userAge = documentSnapshot.getString("age");
                        index = documentSnapshot.getString("index");
                    } else {
                        userAbout = "";
                        userProfession = "";
                        userGender = "";
                        userAge = "";
                        index = "0";
                    }

                    newUserAbout = userAbout;
                    newGender = userGender;
                    newProfession = userProfession;
                    newAge = userAge;
                    newIndex = index;
                    filePath = userProfileUri;

                    editTextGender.setText(userGender);
                    editTextAge.setText(userAge);
                    editTextProfession.setText(userProfession);
                    editTextAbout.setText(userAbout);

                    editTextGender.setFocusable(false);
                    editTextGender.setClickable(true);

                    editTextAbout.setVisibility(View.VISIBLE);
                    editTextGender.setVisibility(View.VISIBLE);
                    editTextProfession.setVisibility(View.VISIBLE);
                    editTextAge.setVisibility(View.VISIBLE);
                    buttonCancel.setVisibility(View.VISIBLE);
                    buttonSave.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            });

            collectionReference.document(firebaseUser.getEmail()).get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    userAbout = "";
                    userProfession = "";
                    userGender = "";
                    userAge = "";
                    index = "0";

                    newUserAbout = userAbout;
                    newGender = userGender;
                    newProfession = userProfession;
                    newAge = userAge;
                    newIndex = index;
                    filePath = userProfileUri;

                    editTextGender.setText(userGender);
                    editTextAge.setText(userAge);
                    editTextProfession.setText(userProfession);
                    editTextAbout.setText(userAbout);

                    editTextGender.setFocusable(false);
                    editTextGender.setClickable(true);

                    editTextAbout.setVisibility(View.VISIBLE);
                    editTextGender.setVisibility(View.VISIBLE);
                    editTextProfession.setVisibility(View.VISIBLE);
                    editTextAge.setVisibility(View.VISIBLE);
                    buttonCancel.setVisibility(View.VISIBLE);
                    buttonSave.setVisibility(View.VISIBLE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(getApplicationContext(), "Failed to load details.", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void firebaseTransaction() {
        if (newAge.equals(userAge) && newGender.equals(userGender) && newProfession.equals(userProfession) && newUserAbout.equals(userAbout) && userProfileUri == filePath) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Nothing to change", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        Map<String, Object> user = new HashMap<>();
        user.put("about", newUserAbout);
        user.put("pronoun", newGender);
        user.put("profession", newProfession);
        user.put("age", newAge);
        user.put("index", newIndex);
        user.put("image_url", filePath.toString());
        user.put("name", userName);

        if (filePath != userProfileUri) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(filePath)
                    .build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("PROFILE_UPDATE", "User profile updated.");
                            }
                        }
                    });
        }


        collectionReference.document(userEmail).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Your profile has been updated.", Toast.LENGTH_SHORT).show();
                try {
                    Thread.sleep(50);

                    Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unexpected error occured", Toast.LENGTH_SHORT).show();
                }
            }
        });

        collectionReference.document(userEmail).set(user).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}