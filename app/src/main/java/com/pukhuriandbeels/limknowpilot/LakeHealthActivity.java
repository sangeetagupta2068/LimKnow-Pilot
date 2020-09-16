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
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class LakeHealthActivity extends AppCompatActivity {

    private RadioButton[] radioButtons;
    private RadioGroup radioGroup;
    private CheckBox[] checkBoxes;
    private EditText editTextDeadFish, editTextLakeHealthProblem;
    private Button buttonLakeHealth;
    private String lakeWaterColor;
    private String deadFish;
    private String lakeHealthProblem;
    private String lakeName = "Deepor Beel";

    private String userName, userEmailId;
    private double latitude, longitude;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake_health);
        initialize();
        setListeners();
        getCurrentLocationSetup();
        firebaseFirestoreSetup();
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

        buttonLakeHealth = findViewById(R.id.button_lake_health);
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
        buttonLakeHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentLocationSetup();
                date = Calendar.getInstance().getTime();
                deadFish = editTextDeadFish.getText().toString();
                lakeHealthProblem = "";
                if (checkBoxes[checkBoxes.length - 1].isChecked()) {
                    lakeHealthProblem = editTextLakeHealthProblem.getText().toString() + ",";
                }
                for (int count = 0; count < 4; count++) {
                    if (checkBoxes[count].isChecked()) {
                        lakeHealthProblem = lakeHealthProblem + checkBoxes[count].getText().toString() + ",";
                    }
                }
                Toast.makeText(LakeHealthActivity.this,
                        lakeHealthProblem
                                + " "
                                + deadFish
                                + " "
                                + lakeWaterColor,
                        Toast.LENGTH_SHORT).show();
                firebaseFirestoreTransaction();
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
                Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseFirestoreSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        userName = firebaseUser.getDisplayName();
        userEmailId = firebaseUser.getEmail();
    }

    private void firebaseFirestoreTransaction() {
        Map<String, Object> documentData = new HashMap<>();
        documentData.put("name", userName);
        documentData.put("email", userEmailId);
        documentData.put("latitude", latitude);
        documentData.put("longitude", longitude);
        documentData.put("date", date);
        documentData.put("lake", lakeName);

        if (!deadFish.equals("")) {
            documentData.put("dead_fish", deadFish);
        }

        if (!lakeWaterColor.equals("")) {
            documentData.put("lake_water_color", lakeWaterColor);
        }

        if (!lakeHealthProblem.equals("")) {
            lakeHealthProblem = lakeHealthProblem.substring(0,lakeHealthProblem.length()-1);
            documentData.put("lake_health_problem", lakeHealthProblem);
        }

        CollectionReference collectionReference = firebaseFirestore.collection("Lake Health");
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