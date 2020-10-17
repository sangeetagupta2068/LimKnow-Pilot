package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pukhuriandbeels.limknowpilot.model.Animal;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class ARAnimalItemActivity extends AppCompatActivity {
    //View declaration
    private TextView mAnimalCommonNameTextView, mAnimalScientificNameTextView;
    private TextView mAnimalImageCreditTextView, mAnimalWaterbodyAssociationTextView,
            mAnimalThreatTextView;
    private ImageView mAnimalImageView;
    private Button mNextButton;

    //Lake observer badge flag declaration
    private Boolean mIsLakeObserver;
    private long lastClickTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_animal_item);

        initialize();
        setListeners();
    }

    private void initialize() {
        //View initialization
        mAnimalCommonNameTextView = findViewById(R.id.animal_common_name);
        mAnimalScientificNameTextView = findViewById(R.id.animal_scientific_name);
        mAnimalImageCreditTextView = findViewById(R.id.animal_picture_credits);
        mAnimalWaterbodyAssociationTextView = findViewById(R.id.animal_waterbody_association);
        mAnimalThreatTextView = findViewById(R.id.animal_threats);
        mNextButton = findViewById(R.id.button_submit_animal);
        mAnimalImageView = findViewById(R.id.animal_image);

        //Set views to gone until view data is loaded
        mAnimalCommonNameTextView.setVisibility(View.GONE);
        mAnimalScientificNameTextView.setVisibility(View.GONE);
        mAnimalWaterbodyAssociationTextView.setVisibility(View.GONE);
        mAnimalImageCreditTextView.setVisibility(View.GONE);
        mAnimalThreatTextView.setVisibility(View.GONE);
        mAnimalImageView.setVisibility(View.GONE);

        //Store current Animal object and increment question count
        Animal animal = LakeARQuizActivity.animals.get(LakeARQuizActivity.questionCount);
        LakeARQuizActivity.questionCount = LakeARQuizActivity.questionCount + 1;

        //load values to views from Animal object
        mAnimalCommonNameTextView.setText(animal.getAnimalCommonName());
        mAnimalThreatTextView.setText(animal.getAnimalThreat());
        mAnimalImageCreditTextView.setText(animal.getAnimalImageCredits());
        mAnimalWaterbodyAssociationTextView.setText(animal.getAnimalWaterbodyAssociation());
        mAnimalScientificNameTextView.setText(animal.getAnimalName());
        Glide.with(this)
                .load(Uri.parse(animal.getAnimalImageURL()))
                .error(R.drawable.sample_animal)
                .into(mAnimalImageView);

        //Upon successful loading, make views visible
        mAnimalCommonNameTextView.setVisibility(View.VISIBLE);
        mAnimalScientificNameTextView.setVisibility(View.VISIBLE);
        mAnimalWaterbodyAssociationTextView.setVisibility(View.VISIBLE);
        mAnimalImageCreditTextView.setVisibility(View.VISIBLE);
        mAnimalThreatTextView.setVisibility(View.VISIBLE);
        mAnimalImageView.setVisibility(View.VISIBLE);

        //Set badge flag to false
        mIsLakeObserver = false;

        lastClickTime = SystemClock.elapsedRealtime();

    }

    private void setListeners() {
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //If all questions have been visited
                if (LakeARQuizActivity.questionCount == LakeARQuizActivity.animals.size()) {
                    //Clear static Animal collection
                    LakeARQuizActivity.animals.clear();


                    //Create Firebase User
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                    //Create request for checking if user has previously earned Lake Observer badge
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    StorageReference firebaseStorageReference = FirebaseStorage.getInstance()
                            .getReference();
                    CollectionReference collectionReference = firebaseFirestore
                            .collection("User");

                    if (firebaseUser != null) {
                        collectionReference.document(firebaseUser.getEmail())
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if (documentSnapshot.exists()) {
                                    if (documentSnapshot.contains("is_lake_observer")) {
                                        mIsLakeObserver = documentSnapshot
                                                .getBoolean("is_lake_observer");
                                    }
                                    if (mIsLakeObserver) {
                                        //If user has earned Lake Observer badge previously
                                        Toast.makeText(getApplicationContext(),
                                                "You completed Lake 3D experience!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(
                                                ARAnimalItemActivity.this,
                                                HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                        return;
                                    }
                                    DocumentReference documentReference = collectionReference
                                            .document(firebaseUser.getEmail());
                                    documentReference.update("is_lake_observer",
                                            true).addOnSuccessListener(
                                                    new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Congratulations! You have earned " +
                                                            "the Lake Observer badge!",
                                                    Toast.LENGTH_SHORT).show();
                                            try {
                                                Thread.sleep(50);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            Intent intent = new Intent(
                                                    ARAnimalItemActivity.this,
                                                    HomeActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                }
                            }
                        });
                    }
                    else {
                        //If user couldn't be found
                        Toast.makeText(getApplicationContext(),
                                "You completed Lake 3D experience!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(ARAnimalItemActivity.this,
                            LakeARQuizActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}