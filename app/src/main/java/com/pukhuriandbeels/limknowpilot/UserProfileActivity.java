package com.pukhuriandbeels.limknowpilot;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UserProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 501;
    ShapeableImageView imageViewProfile;
    private TextView textViewName, textViewEmail;
    private FloatingActionButton floatingActionButton;
    private TextInputEditText editTextAbout, editTextProfession, editTextAge, editTextGender;
    private Button buttonSave, buttonCancel;

    private FirebaseAuth firebaseAuth;
    private String userEmail, userName, userProfession, userAbout,userGender;
    private int userAge;
    private Uri userProfileUri;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setFirebaseAuthorizedUser();
        initialize();
        setListeners();
    }

    private void initialize() {
        userGender = "";
        userProfession = "";
        userAbout = "";
        userAge = 18;

        imageViewProfile = findViewById(R.id.user_profile_picture);
        textViewEmail = findViewById(R.id.user_profile_email);
        textViewName = findViewById(R.id.user_profile_name);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        editTextAbout = findViewById(R.id.about);
        editTextProfession = findViewById(R.id.profession);
        editTextAge = findViewById(R.id.age);
        editTextGender = findViewById(R.id.gender);

        buttonCancel = findViewById(R.id.button_user_profile_cancel);
        buttonSave = findViewById(R.id.button_user_profile_save);

        textViewName.setText(userName);
        textViewEmail.setText(userEmail);
        editTextGender.setFocusable(false);
        editTextGender.setClickable(true);
        Glide.with(this).load(userProfileUri).error(R.drawable.ic_baseline_person_24).into(imageViewProfile);

    }

    private void setListeners(){
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
                Intent intent = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userAbout = editTextAbout.getText().toString();
                userAge = Integer.valueOf(editTextAge.getText().toString());
                if(userAge < 6 || userAge > 90){
                    Toast.makeText(getApplicationContext(),"Not a valid age for LimKnow",Toast.LENGTH_SHORT).show();
                    return;
                }
                userProfession = editTextProfession.getText().toString();

                Toast.makeText(getApplicationContext(),userAbout + userGender + userProfession, Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UserProfileActivity.this);
        alertDialog.setTitle("Pronoun");
        String[] items = {"He/Him","She/Her","They/Them"};
        int check = 1;
        alertDialog.setSingleChoiceItems(items,1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: userGender = items[0];
                            break;
                    case 1:
                        userGender = items[1];
                        break;
                    case 2:
                        userGender = items[2];
                        break;
                }
            }
        });
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                editTextGender.setText(userGender);
                dialog.dismiss();
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                userGender = editTextGender.getHint().toString();
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


    private void setFirebaseAuthorizedUser() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null) {
            userEmail = firebaseUser.getEmail();
            userName = firebaseUser.getDisplayName();
        } else {
            userEmail = "";
            userName = "";
        }

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Users");

//        collectionReference.whereEqualTo("email", userEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                queryDocumentSnapshots.getDocuments().
//            }
//        });
    }
}