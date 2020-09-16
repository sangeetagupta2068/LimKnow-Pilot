package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

public class InvasiveSpeciesWatchActivity extends AppCompatActivity {

    private RadioGroup radioGroupInvasiveSpecies;
    private RadioButton[] radioButtons;
    private ImageView[] imageViews;
    private TextView textViewinvasiveSpeciesOther;
    private Button buttoninvasiveSpeciesOther;
    private ImageView imageViewSampleInvasvieSpecies;
    private SeekBar seekBarWideSpread;
    private Button buttonSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invasive_species_watch);
        initialize();
    }

    private void initialize(){
        radioGroupInvasiveSpecies = findViewById(R.id.invasive_species_radio_group);
        radioButtons = new RadioButton[5];
        radioButtons[0] = findViewById(R.id.invasive_species_1);
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

}