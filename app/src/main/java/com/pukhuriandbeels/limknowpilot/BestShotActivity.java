package com.pukhuriandbeels.limknowpilot;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class BestShotActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 401;
    private Button buttonUpload, buttonSubmit;
    private ImageView imageView;
    private RadioButton[] radioButtons;
    private RadioGroup radioGroup;
    private EditText editTextOther, editTextName, editTextObservedOn;
    private ProgressBar progressBar;
    private Button buttonCancel;

    private String other, name, observedOn, type;
    private String userName, userEmailId;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private Uri filePath;
    private CollectionReference collectionReference;
    private StorageReference firebaseStorageReference;
    private boolean flag;
    private long lastClickTime;
    private boolean isLakePhotographer;
    private FirebaseUser firebaseUser;
    private CollectionReference userCollectionReference;
    private DatePickerDialog datepicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_best_shot);
        firebaseFirestoreSetup();
        initailize();
        setListeners();
    }

    private void initailize() {
        name = "";
        observedOn = "";
        type = "";
        flag = true;
        isLakePhotographer = false;

        buttonUpload = findViewById(R.id.button_upload_shot);
        imageView = findViewById(R.id.sample_image_view_best_shot);

        radioGroup = findViewById(R.id.radio_group_best_shot);
        radioButtons = new RadioButton[8];
        radioButtons[0] = findViewById(R.id.radio_best_1);
        radioButtons[1] = findViewById(R.id.radio_best_2);
        radioButtons[2] = findViewById(R.id.radio_best_3);
        radioButtons[3] = findViewById(R.id.radio_best_4);
        radioButtons[4] = findViewById(R.id.radio_best_5);
        radioButtons[5] = findViewById(R.id.radio_best_6);
        radioButtons[6] = findViewById(R.id.radio_best_7);
        radioButtons[7] = findViewById(R.id.radio_best_8);

        editTextOther = findViewById(R.id.edit_text_best_shot_other);
        editTextName = findViewById(R.id.edit_text_best_shot_name);
        editTextObservedOn = findViewById(R.id.edit_text_best_shot_observed_on);

        progressBar = findViewById(R.id.best_shot_connection_status);

        buttonSubmit = findViewById(R.id.button_best_shot);
        buttonCancel = findViewById(R.id.button_best_shot_cancel);

        editTextOther.setVisibility(View.GONE);
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    private void setListeners() {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(group.getCheckedRadioButtonId());
                type = radioButton.getText().toString();
            }
        });
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(BestShotActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BestShotActivity.this, new String[]{Manifest.permission.CAMERA}, PICK_IMAGE_REQUEST);
                } else {
                    selectImage();
                }
            }
        });

        editTextObservedOn.setInputType(InputType.TYPE_NULL);
        editTextObservedOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                datepicker = new DatePickerDialog(BestShotActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Date date = new Date();
                                String selectedDate = String.valueOf(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                try {
                                    Date datePicked = new SimpleDateFormat("dd/MM/yyyy").parse(selectedDate);
                                    if(date.compareTo(datePicked)>=0){
                                        editTextObservedOn.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                    } else {
                                        editTextObservedOn.setText("");
                                        Toast.makeText(BestShotActivity.this,"Observed on date cannot be after today's date",Toast.LENGTH_SHORT).show();
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }, year, month, day);
                datepicker.show();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = editTextName.getText().toString();
                observedOn = editTextObservedOn.getText().toString();
                other = editTextOther.getText().toString();

                if (name.equals("") && observedOn.equals("") && flag && type.equals("")) {
                    Toast.makeText(getApplicationContext(), "Can't submit empty form.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (type.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please select a type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (type.equals("Other") && other.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please mention other applicable type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (flag) {
                    Toast.makeText(getApplicationContext(), "Please upload picture", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (name.equals("") && observedOn.equals("")) {
                    Toast.makeText(getApplicationContext(), "Please fill in both photographer and observed on field", Toast.LENGTH_SHORT).show();
                    return;
                } else if(observedOn.equals("")){
                    Toast.makeText(getApplicationContext(), "Please fill in observed on field", Toast.LENGTH_SHORT).show();
                    return;
                } else if(name.equals("")){
                    Toast.makeText(getApplicationContext(), "Please add photographer name field", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000)
                    return;

                lastClickTime = SystemClock.elapsedRealtime();
                progressBar.setVisibility(View.VISIBLE);
                final StorageReference storageReference = firebaseStorageReference.child("Best Shots").child(filePath.getLastPathSegment());
                storageReference.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firebaseFirestoreTransaction(uri.toString());
                                flag = true;
                            }
                        });
                    }
                });
                storageReference.putFile(filePath).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseFirestoreTransaction(null);
                        flag = true;
                    }
                });

            }
        });

        radioButtons[7].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    editTextOther.setVisibility(View.VISIBLE);
                else
                    editTextOther.setVisibility(View.GONE);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
            flag = false;

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageBitmap(bitmap);
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

    private void firebaseFirestoreSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();
        collectionReference = firebaseFirestore.collection("User Shots");
        userCollectionReference = firebaseFirestore.collection("User");

        firebaseUser = firebaseAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        userEmailId = firebaseUser.getEmail();

        if (firebaseUser != null) {
            userCollectionReference.document(firebaseUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.contains("is_lake_photographer")) {
                            isLakePhotographer = documentSnapshot.getBoolean("is_lake_photographer");
                        }
                    }
                }
            });
        }
    }

    private void firebaseFirestoreTransaction(@Nullable Object uri) {
        final Map<String, Object> documentData = new HashMap<>();
        documentData.put("name", userName);
        documentData.put("email", userEmailId);
        documentData.put("observed_on", observedOn);
        documentData.put("photographed_by", name);
        documentData.put("type", type);
        if (!other.equals(""))
            documentData.put("other_type", other);
        if (uri != null)
            documentData.put("uploaded_image_url", uri);

        String id = collectionReference.document().getId();

        collectionReference.document(id).set(documentData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                if (isLakePhotographer) {
                    Toast.makeText(getApplicationContext(), "Thank you for your response", Toast.LENGTH_LONG).show();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                } else {
                    userCollectionReference.document(firebaseUser.getEmail()).update("is_lake_photographer", true).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(), "Congratulations! You have earned the Lake Photographer badge!", Toast.LENGTH_SHORT).show();
                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            finish();
                        }
                    });
                }

            }
        });
        collectionReference.document(id).set(documentData).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Couldn't add data! Try again!", Toast.LENGTH_LONG).show();
            }
        });
    }
}

