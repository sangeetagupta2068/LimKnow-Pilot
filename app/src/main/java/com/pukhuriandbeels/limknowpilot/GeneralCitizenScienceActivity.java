package com.pukhuriandbeels.limknowpilot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class GeneralCitizenScienceActivity extends AppCompatActivity {

    private EditText editTextBeelRelation, editTextFees, editTextPotentialLakeName;
    private CheckBox[] checkBoxes;
    private Button buttonGeneralCitizenScience;

    private String beelRelation, potentialFees, potentialLake, lakeName;
    private double latitude, longitude;
    private Date date;

    private FirebaseAuth firebaseAuth;
    private String userName, userEmailId;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_citizen_science);
        firebaseFirestoreSetup();
        initialize();
        setListeners();
        getCurrentLocationSetup();
    }

    private void initialize() {
        editTextFees = findViewById(R.id.edit_text_fees);
        editTextBeelRelation = findViewById(R.id.edit_text_other);
        editTextPotentialLakeName = findViewById(R.id.edit_text_lake);
        lakeName = "Deepor Beel";

        checkBoxes = new CheckBox[7];
        checkBoxes[0] = findViewById(R.id.checkbox_1);
        checkBoxes[1] = findViewById(R.id.checkbox_2);
        checkBoxes[2] = findViewById(R.id.checkbox_3);
        checkBoxes[3] = findViewById(R.id.checkbox_4);
        checkBoxes[4] = findViewById(R.id.checkbox_5);
        checkBoxes[5] = findViewById(R.id.checkbox_6);
        checkBoxes[6] = findViewById(R.id.checkbox_7);

        buttonGeneralCitizenScience = findViewById(R.id.button_general);
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
                LocationServices.getFusedLocationProviderClient(GeneralCitizenScienceActivity.this)
                        .requestLocationUpdates(locationRequest,
                                new LocationCallback() {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        LocationServices.getFusedLocationProviderClient(GeneralCitizenScienceActivity.this)
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
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setListeners() {
        checkBoxes[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    editTextBeelRelation.setVisibility(View.VISIBLE);
                else
                    editTextBeelRelation.setVisibility(View.GONE);
            }
        });

        buttonGeneralCitizenScience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocationSetup();

                date = Calendar.getInstance().getTime();
                beelRelation = "";
                potentialFees = editTextFees.getText().toString();
                if (checkBoxes[6].isChecked()) {
                    beelRelation = editTextBeelRelation.getText().toString() + ",";
                }

                for (int count = 0; count < 6; count++) {
                    if (checkBoxes[count].isChecked())
                        beelRelation = beelRelation + checkBoxes[count].getText().toString() + ",";
                }
                potentialLake = editTextPotentialLakeName.getText().toString();
                firebaseFirestoreTransaction();
            }
        });
    }

    private void firebaseFirestoreSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        userEmailId = firebaseUser.getEmail();
    }

    private void getCurrentLocationSetup() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(GeneralCitizenScienceActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            final LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(3000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.getFusedLocationProviderClient(GeneralCitizenScienceActivity.this)
                    .requestLocationUpdates(locationRequest,
                    new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(GeneralCitizenScienceActivity.this)
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

    private void firebaseFirestoreTransaction() {
        Map<String, Object> documentData = new HashMap<>();
        documentData.put("name", userName);
        documentData.put("email", userEmailId);
        documentData.put("latitude", latitude);
        documentData.put("longitude", longitude);
        documentData.put("date", date);
        documentData.put("lake", lakeName);

        if (!potentialFees.equals("")) {
            documentData.put("potential_amount", potentialFees);
        }

        if (!potentialLake.equals("")) {
            documentData.put("potential_lake", potentialLake);
        }

        if (!beelRelation.equals("")) {
            beelRelation = beelRelation.substring(0,beelRelation.length()-1);
            documentData.put("beel_relation", beelRelation);
        }

        CollectionReference collectionReference = firebaseFirestore.collection("Lake Feedback");
        String id = collectionReference.document().getId();
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
}