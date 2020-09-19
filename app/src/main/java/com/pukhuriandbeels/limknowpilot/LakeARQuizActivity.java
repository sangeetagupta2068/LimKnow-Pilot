package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pukhuriandbeels.limknowpilot.model.Animal;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LakeARQuizActivity extends AppCompatActivity {

    public static int questionCount = 0;

    public static ArrayList<Animal> animals = new ArrayList<>();

    private TextView questionTextView;
    private RadioGroup questionRadioGroup;
    private RadioButton[] radioButtons;
    private Button submit;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake_a_r_quiz);
        firebaseFirestoreSetup();
        initialize();
    }

    private void initialize() {
        questionTextView = findViewById(R.id.animal_ar_quiz_question);
        questionRadioGroup = findViewById(R.id.animal_ar_radio_group);

        radioButtons = new RadioButton[3];
        radioButtons[0] = findViewById(R.id.animal_radio_1);
        radioButtons[1] = findViewById(R.id.animal_radio_2);
        radioButtons[2] = findViewById(R.id.animal_radio_3);

        submit = findViewById(R.id.button_animal_ar);

        questionTextView.setVisibility(View.GONE);
        questionRadioGroup.setVisibility(View.GONE);

        radioButtons[0].setVisibility(View.GONE);
        radioButtons[1].setVisibility(View.GONE);
        radioButtons[2].setVisibility(View.GONE);

        submit.setVisibility(View.GONE);

        if (questionCount == animals.size()) {
            questionCount = 0;
        }

        if (animals.size() > 0 && questionCount < animals.size()) {
            questionTextView.setText(animals.get(questionCount).getAssociatedQuestion());
            radioButtons[0].setText(animals.get(questionCount).getOptionOne());
            radioButtons[1].setText(animals.get(questionCount).getOptionTwo());
            radioButtons[2].setText(animals.get(questionCount).getOptionThree());

            questionTextView.setVisibility(View.VISIBLE);
            questionRadioGroup.setVisibility(View.VISIBLE);

            radioButtons[0].setVisibility(View.VISIBLE);
            radioButtons[1].setVisibility(View.VISIBLE);
            radioButtons[2].setVisibility(View.VISIBLE);

            submit.setVisibility(View.VISIBLE);

        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LakeARQuizActivity.this, LakeARActivity.class);
                startActivity(intent);
            }
        });
    }

    private void firebaseFirestoreSetup() {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        if (animals.size() <= 0) {
            CollectionReference collectionReference = firebaseFirestore.collection("AR Biodiversity");
            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Animal animal = new Animal();

                        animal.setAnimalCommonName(documentSnapshot.getString("common_name"));
                        animal.setAnimalName(documentSnapshot.getString("name"));
                        animal.setAnimalWaterbodyAssociation(documentSnapshot.getString("waterbody_association"));
                        animal.setAnimalImageURL(documentSnapshot.getString("image_url"));
                        animal.setAnimalThreat(documentSnapshot.getString("threats"));
                        animal.setAnimalImageCredits(documentSnapshot.getString("image_credits"));
                        animal.setAnimalARModelURL(documentSnapshot.getString("ar_model_url"));

                        animal.setAssociatedQuestion(documentSnapshot.getString("question"));

                        animal.setOptionOne(documentSnapshot.getString("option_one"));
                        animal.setOptionTwo(documentSnapshot.getString("option_two"));
                        animal.setOptionThree(documentSnapshot.getString("option_three"));

                        animals.add(animal);
                        Log.i("TAG", animal.getOptionOne() + animal.getOptionThree() + animal.getOptionTwo());

                        if (animal.getAnimalCommonName().equals("Indian elephant"))
                            animal.setAnimalScale(1.85f);
                        else if (animal.getAnimalCommonName().equals("Greater Adjutant stork"))
                            animal.setAnimalScale(0.5f);
                        else if (animal.getAnimalCommonName().equals("Lesser Adjutant Stork"))
                            animal.setAnimalScale(0.5f);
                        else if (animal.getAnimalCommonName().equals("Sambar Deer"))
                            animal.setAnimalScale(0.03f);
                        else if (animal.getAnimalCommonName().equals("Black winged stilt"))
                            animal.setAnimalScale(0.06f);

                        questionTextView.setText(animals.get(questionCount).getAssociatedQuestion());
                        radioButtons[0].setText(animals.get(questionCount).getOptionOne());
                        radioButtons[1].setText(animals.get(questionCount).getOptionTwo());
                        radioButtons[2].setText(animals.get(questionCount).getOptionThree());

                        questionTextView.setVisibility(View.VISIBLE);
                        questionRadioGroup.setVisibility(View.VISIBLE);

                        radioButtons[0].setVisibility(View.VISIBLE);
                        radioButtons[1].setVisibility(View.VISIBLE);
                        radioButtons[2].setVisibility(View.VISIBLE);

                        submit.setVisibility(View.VISIBLE);
                    }

                }
            });

            Toast.makeText(getApplicationContext(), "Ready", Toast.LENGTH_SHORT).show();
            collectionReference.get().addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}