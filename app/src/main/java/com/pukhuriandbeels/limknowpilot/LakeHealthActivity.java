package com.pukhuriandbeels.limknowpilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class LakeHealthActivity extends AppCompatActivity {

    public static final int REQUEST_TAKE_PHOTO = 103;
    public static final int CAMERA_REQUEST_CODE = 101;

    private RadioButton[] radioButtons;
    private RadioGroup radioGroup;
    private CheckBox[] checkBoxes;
    private EditText editTextDeadFish, editTextLakeHealthProblem, editTextDeadAnimalDescription;
    private Button buttonLakeHealth, buttonUploadDeadAnimalImage;
    private TextView textViewDeadAnimalLabel, textViewDeadAnimalUploadImage;

    private ImageView imageViewSample;

    private String lakeWaterColor;
    private String deadFish;
    private String lakeHealthProblem;
    private String lakeName = "Deepor Beel";
    private String deadAnimalDescription;

    private String userName, userEmailId;
    private double latitude, longitude;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private Date date;
    private StorageReference firebaseStorageReference;
    private String currentPhotoPath;
    private Uri imageUri;
    private CollectionReference collectionReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake_health);
        initialize();
        firebaseFirestoreSetup();
        setListeners();
        getCurrentLocationSetup();
    }

    private void initialize() {
        radioGroup = findViewById(R.id.radio_group_lake_health);
        radioButtons = new RadioButton[4];
        radioButtons[0] = findViewById(R.id.radio_1);
        radioButtons[1] = findViewById(R.id.radio_2);
        radioButtons[2] = findViewById(R.id.radio_3);
        radioButtons[3] = findViewById(R.id.radio_4);

        checkBoxes = new CheckBox[5];
        checkBoxes[0] = findViewById(R.id.lake_health_checkbox_1);
        checkBoxes[1] = findViewById(R.id.lake_health_checkbox_2);
        checkBoxes[2] = findViewById(R.id.lake_health_checkbox_3);
        checkBoxes[3] = findViewById(R.id.lake_health_checkbox_4);
        checkBoxes[4] = findViewById(R.id.lake_health_checkbox_5);

        editTextDeadFish = findViewById(R.id.edit_text_lake_health_dead_fish);
        editTextLakeHealthProblem = findViewById(R.id.edit_text_other_lake_health);
        editTextDeadAnimalDescription = findViewById(R.id.edit_text_dead_animal_text);

        buttonUploadDeadAnimalImage = findViewById(R.id.button_dead_animal_upload);
        buttonLakeHealth = findViewById(R.id.button_lake_health);

        textViewDeadAnimalLabel = findViewById(R.id.dead_animal_description_label);
        textViewDeadAnimalUploadImage = findViewById(R.id.dead_animal_image_label);

        imageViewSample = findViewById(R.id.sample_image_view);

        textViewDeadAnimalLabel.setVisibility(View.GONE);
        editTextDeadAnimalDescription.setVisibility(View.GONE);
        textViewDeadAnimalUploadImage.setVisibility(View.GONE);
        buttonUploadDeadAnimalImage.setVisibility(View.GONE);
        imageViewSample.setVisibility(View.GONE);
    }

    private void setListeners() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(group.getCheckedRadioButtonId());
                lakeWaterColor = radioButton.getText().toString();
            }
        });
        checkBoxes[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    editTextLakeHealthProblem.setVisibility(View.VISIBLE);
                else
                    editTextLakeHealthProblem.setVisibility(View.GONE);
            }
        });
        checkBoxes[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewDeadAnimalLabel.setVisibility(View.VISIBLE);
                    editTextDeadAnimalDescription.setVisibility(View.VISIBLE);
                    textViewDeadAnimalUploadImage.setVisibility(View.VISIBLE);
                    buttonUploadDeadAnimalImage.setVisibility(View.VISIBLE);
                    imageViewSample.setVisibility(View.VISIBLE);

                } else {
                    textViewDeadAnimalLabel.setVisibility(View.GONE);
                    editTextDeadAnimalDescription.setVisibility(View.GONE);
                    textViewDeadAnimalUploadImage.setVisibility(View.GONE);
                    buttonUploadDeadAnimalImage.setVisibility(View.GONE);
                    imageViewSample.setVisibility(View.GONE);
                }
            }
        });
        buttonLakeHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocationSetup();
                date = Calendar.getInstance().getTime();
                deadFish = editTextDeadFish.getText().toString();
                lakeHealthProblem = "";
                if (checkBoxes[4].isChecked()) {
                    lakeHealthProblem = editTextLakeHealthProblem.getText().toString() + ",";
                }
                if (checkBoxes[3].isChecked()) {
                    deadAnimalDescription = editTextDeadAnimalDescription.getText().toString();
                }
                for (int count = 0; count < 4; count++) {
                    if (checkBoxes[count].isChecked()) {
                        lakeHealthProblem = lakeHealthProblem + checkBoxes[count].getText().toString() + ",";
                    }
                }

                File file = new File(currentPhotoPath);
                imageUri = Uri.fromFile(file);
                Toast.makeText(LakeHealthActivity.this,
                        lakeHealthProblem
                                + " "
                                + deadFish
                                + " "
                                + lakeWaterColor + currentPhotoPath + String.valueOf(imageUri),
                        Toast.LENGTH_SHORT).show();

                final StorageReference storageReference = firebaseStorageReference.child("Dead Animals").child(imageUri.getLastPathSegment());
                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firebaseFirestoreTransaction(uri.toString());
                                Toast.makeText(LakeHealthActivity.this, "Uploaded image url : " + uri.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(LakeHealthActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
                storageReference.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseFirestoreTransaction(null);
                        Toast.makeText(LakeHealthActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });


            }

        });
        buttonUploadDeadAnimalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageViewSample.setVisibility(View.VISIBLE);
                if (ContextCompat.checkSelfPermission(LakeHealthActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(LakeHealthActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void getCurrentLocationSetup() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(LakeHealthActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            final LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.getFusedLocationProviderClient(LakeHealthActivity.this)
                    .requestLocationUpdates(locationRequest,
                            new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(LakeHealthActivity.this)
                                            .removeLocationUpdates(this);
                                    if (locationResult != null && locationResult.getLocations().size() > 0) {
                                        int latestLocationIndex = locationResult.getLocations().size() - 1;
                                        latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                        longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                    }
                                }
                            },
                            Looper.getMainLooper());
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                final LocationRequest locationRequest = new LocationRequest();
                locationRequest.setInterval(1000);
                locationRequest.setFastestInterval(3000);
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationServices.getFusedLocationProviderClient(LakeHealthActivity.this)
                        .requestLocationUpdates(locationRequest,
                                new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        LocationServices.getFusedLocationProviderClient(LakeHealthActivity.this)
                                                .removeLocationUpdates(this);
                                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                        }
                                    }
                                },
                                Looper.getMainLooper());
            } else {
                Toast.makeText(getApplicationContext(), "Location Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Camera Permission denied", Toast.LENGTH_SHORT).show();
            } else {
                dispatchTakePictureIntent();
            }
        }
    }

    private void firebaseFirestoreSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        userEmailId = firebaseUser.getEmail();

        collectionReference = firebaseFirestore.collection("Lake Health");

    }

    private void firebaseFirestoreTransaction(@Nullable Object value) {
        final Map<String, Object> documentData = new HashMap<>();
        documentData.put("name", userName);
        documentData.put("email", userEmailId);
        documentData.put("latitude", latitude);
        documentData.put("longitude", longitude);
        documentData.put("date", date);
        documentData.put("lake", lakeName);

        String id = collectionReference.document().getId();

        if (!deadFish.equals("")) {
            documentData.put("dead_fish", deadFish);
        }
        if (!deadAnimalDescription.equals("")) {
            documentData.put("dead_animal_description", deadAnimalDescription);
        }
        if (value != null) {
            documentData.put("image_url", value);
        }
        if (!lakeWaterColor.equals("")) {
            documentData.put("lake_water_color", lakeWaterColor);
        }
        if (!lakeHealthProblem.equals("")) {
            lakeHealthProblem = lakeHealthProblem.substring(0, lakeHealthProblem.length() - 1);
            documentData.put("lake_health_problem", lakeHealthProblem);
        }
        collectionReference.document(id).set(documentData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Thank you for your response", Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        collectionReference.document(id).set(documentData).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Couldn't add data! Try again!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.pukhuriandbeels.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            imageViewSample.setImageURI(Uri.fromFile(file));
            imageUri = Uri.fromFile(file);
        }
    }
}