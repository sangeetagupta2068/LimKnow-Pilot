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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import static com.pukhuriandbeels.limknowpilot.LakeHealthActivity.CAMERA_REQUEST_CODE;

public class InvasiveSpeciesWatchActivity extends AppCompatActivity {

    private RadioGroup radioGroupInvasiveSpecies;
    private RadioButton[] radioButtons;
    private ImageView[] imageViews;
    private TextView textViewinvasiveSpeciesOther;
    private Button buttoninvasiveSpeciesOther;
    private ImageView imageViewSampleInvasvieSpecies;
    private SeekBar seekBarWideSpread;
    private Button buttonSubmit;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference firebaseStorageReference;
    private String userName;
    private String userEmailId;
    private CollectionReference collectionReference;
    private String currentPhotoPath;
    private Uri imageUri;
    private double latitude, longitude;
    private int widespreadPercentage;
    private Date date;
    private String invasiveSpeciesName;
    private Object lakeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invasive_species_watch);
        initialize();
        firebaseFirestoreSetup();
        setListeners();
    }

    private void initialize() {
        lakeName = "Deepor Beel";
        radioGroupInvasiveSpecies = findViewById(R.id.animal_ar_radio_group);
        radioButtons = new RadioButton[5];
        radioButtons[0] = findViewById(R.id.animal_radio_1);
        radioButtons[1] = findViewById(R.id.invasive_species_2);
        radioButtons[2] = findViewById(R.id.invasive_species_3);
        radioButtons[3] = findViewById(R.id.invasive_species_4);
        radioButtons[4] = findViewById(R.id.invasive_species_5);

        imageViews = new ImageView[4];
        imageViews[0] = findViewById(R.id.invasive_species_image_1);
        imageViews[1] = findViewById(R.id.invasive_species_image_2);
        imageViews[2] = findViewById(R.id.invasive_species_image_3);
        imageViews[3] = findViewById(R.id.invasive_species_image_4);

        textViewinvasiveSpeciesOther = findViewById(R.id.invasive_species_other_label);
        buttoninvasiveSpeciesOther = findViewById(R.id.button_invasive_species_upload);
        imageViewSampleInvasvieSpecies = findViewById(R.id.sample_invasive_species_image_view);

        seekBarWideSpread = findViewById(R.id.invasive_species_spread_bar);

        buttonSubmit = findViewById(R.id.button_invasive_species);

        textViewinvasiveSpeciesOther.setVisibility(View.GONE);
        buttoninvasiveSpeciesOther.setVisibility(View.GONE);
        imageViewSampleInvasvieSpecies.setVisibility(View.GONE);
    }

    private void setListeners() {
        radioGroupInvasiveSpecies.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(group.getCheckedRadioButtonId());
                invasiveSpeciesName = radioButton.getText().toString();
            }
        });
        radioButtons[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    textViewinvasiveSpeciesOther.setVisibility(View.VISIBLE);
                    buttoninvasiveSpeciesOther.setVisibility(View.VISIBLE);
                } else {
                    textViewinvasiveSpeciesOther.setVisibility(View.GONE);
                    buttoninvasiveSpeciesOther.setVisibility(View.GONE);
                    imageViewSampleInvasvieSpecies.setVisibility(View.GONE);
                }
            }
        });

        buttoninvasiveSpeciesOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(InvasiveSpeciesWatchActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InvasiveSpeciesWatchActivity.this, new String[]{Manifest.permission.CAMERA}, LakeHealthActivity.CAMERA_REQUEST_CODE);
                }
                dispatchTakePictureIntent();
            }
        });

        seekBarWideSpread.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                widespreadPercentage = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocationSetup();
                date = Calendar.getInstance().getTime();

                File file = new File(currentPhotoPath);
                imageUri = Uri.fromFile(file);
                final StorageReference storageReference = firebaseStorageReference.child("Reported Invasive Species").child(imageUri.getLastPathSegment());
                storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                firebaseFirestoreTransaction(uri.toString());
                                Toast.makeText(InvasiveSpeciesWatchActivity.this, "Uploaded image url : " + uri.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(InvasiveSpeciesWatchActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                });
                storageReference.putFile(imageUri).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        firebaseFirestoreTransaction(null);
                        Toast.makeText(InvasiveSpeciesWatchActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void firebaseFirestoreTransaction(@Nullable Object value) {
        final Map<String, Object> documentData = new HashMap<>();
        documentData.put("name", userName);
        documentData.put("email", userEmailId);
        documentData.put("latitude", latitude);
        documentData.put("longitude", longitude);
        documentData.put("date", date);
        documentData.put("lake", lakeName);
        documentData.put("invasive_species_widespread", widespreadPercentage);

        String id = collectionReference.document().getId();

        if (value != null) {
            documentData.put("other_image_url", value);
        }
        if (!invasiveSpeciesName.equals("")) {
            documentData.put("invasive_species_presence", invasiveSpeciesName);
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

    private void firebaseFirestoreSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorageReference = FirebaseStorage.getInstance().getReference();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        userEmailId = firebaseUser.getEmail();

        collectionReference = firebaseFirestore.collection("Reported Invasive Species");

        firebaseFirestore.collection("Macrophytes")
                .whereEqualTo("invasive_species", true)
                .orderBy("name")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Toast.makeText(getApplicationContext(), "Successful!", Toast.LENGTH_SHORT).show();
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for (int count = 0; count < imageViews.length; count++) {
                            radioButtons[count].setText(documentSnapshots.get(count).getString("name"));
                            Uri uri = Uri.parse(documentSnapshots.get(count).getString("image_url"));
                            Log.i("INDEX_RESULT", documentSnapshots.get(count).getString("name") + documentSnapshots.get(count).getString("image_url"));
                            if (uri != null) {
                                Glide.with(InvasiveSpeciesWatchActivity.this)
                                        .load(uri)
                                        .placeholder(R.drawable.sample_macrophyte)
                                        .error(R.drawable.sample_macrophyte)
                                        .into(imageViews[count]);
                            }
                        }

                    }
                });

        firebaseFirestore.collection("Macrophytes")
                .whereEqualTo("invasive_species", true)
                .orderBy("name")
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
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
                startActivityForResult(takePictureIntent, LakeHealthActivity.REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LakeHealthActivity.REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            File file = new File(currentPhotoPath);
            imageViewSampleInvasvieSpecies.setVisibility(View.VISIBLE);
            imageViewSampleInvasvieSpecies.setImageURI(Uri.fromFile(file));
            imageUri = Uri.fromFile(file);
        }
    }

    private void getCurrentLocationSetup() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(InvasiveSpeciesWatchActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            final LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.getFusedLocationProviderClient(InvasiveSpeciesWatchActivity.this)
                    .requestLocationUpdates(locationRequest,
                            new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    super.onLocationResult(locationResult);
                                    LocationServices.getFusedLocationProviderClient(InvasiveSpeciesWatchActivity.this)
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
                LocationServices.getFusedLocationProviderClient(InvasiveSpeciesWatchActivity.this)
                        .requestLocationUpdates(locationRequest,
                                new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        LocationServices.getFusedLocationProviderClient(InvasiveSpeciesWatchActivity.this)
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

}