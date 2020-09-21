package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pukhuriandbeels.limknowpilot.model.Animal;

import androidx.appcompat.app.AppCompatActivity;

public class ARAnimalItemActivity extends AppCompatActivity {

    private TextView textViewAnimalCommonName, textViewAnimalScientificName;
    private TextView textViewAnimalImageCredits, textViewAnimalWaterbodyAssociation, textViewAnimalThreats;
    private ImageView imageViewAnimal;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_animal_item);
        initialize();
        setListeners(); //lesser ad,ele,
    }

    private void initialize() {
        textViewAnimalCommonName = findViewById(R.id.animal_common_name);
        textViewAnimalScientificName = findViewById(R.id.animal_scientific_name);
        textViewAnimalImageCredits = findViewById(R.id.animal_picture_credits);
        textViewAnimalWaterbodyAssociation = findViewById(R.id.animal_waterbody_association);
        textViewAnimalThreats = findViewById(R.id.animal_threats);
        submit = findViewById(R.id.button_submit_animal);

        imageViewAnimal = findViewById(R.id.animal_image);

        textViewAnimalCommonName.setVisibility(View.GONE);
        textViewAnimalScientificName.setVisibility(View.GONE);
        textViewAnimalWaterbodyAssociation.setVisibility(View.GONE);
        textViewAnimalImageCredits.setVisibility(View.GONE);
        textViewAnimalThreats.setVisibility(View.GONE);

        imageViewAnimal.setVisibility(View.GONE);

        Animal animal = LakeARQuizActivity.animals.get(LakeARQuizActivity.questionCount);
        LakeARQuizActivity.questionCount = LakeARQuizActivity.questionCount + 1;

        textViewAnimalCommonName.setText(animal.getAnimalCommonName());
        textViewAnimalThreats.setText(animal.getAnimalThreat());
        textViewAnimalImageCredits.setText(animal.getAnimalImageCredits());
        textViewAnimalWaterbodyAssociation.setText(animal.getAnimalWaterbodyAssociation());
        textViewAnimalScientificName.setText(animal.getAnimalName());

        Glide.with(this)
                .load(Uri.parse(animal.getAnimalImageURL()))
                .placeholder(R.drawable.sample_animal)
                .error(R.drawable.sample_animal)
                .into(imageViewAnimal);

        textViewAnimalCommonName.setVisibility(View.VISIBLE);
        textViewAnimalScientificName.setVisibility(View.VISIBLE);
        textViewAnimalWaterbodyAssociation.setVisibility(View.VISIBLE);
        textViewAnimalImageCredits.setVisibility(View.VISIBLE);
        textViewAnimalThreats.setVisibility(View.VISIBLE);

        imageViewAnimal.setVisibility(View.VISIBLE);

    }

    private void setListeners() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LakeARQuizActivity.questionCount == LakeARQuizActivity.animals.size()) {
                    LakeARQuizActivity.animals.clear();
                    Toast.makeText(getApplicationContext(), "You completed Lake 3D experience!", Toast.LENGTH_SHORT).show();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    Intent intent = new Intent(ARAnimalItemActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(ARAnimalItemActivity.this, LakeARQuizActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}