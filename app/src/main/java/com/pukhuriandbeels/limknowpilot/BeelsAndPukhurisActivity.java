package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class BeelsAndPukhurisActivity extends AppCompatActivity {
    //View declaration
    private TextView mLakeDescriptionTextView;
    private Button mBackButton;
    private ImageView mLakeimageView, mLakeThreatImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beels_and_pukhuris);

        initialize();
        setListeners();
    }

    private void initialize(){
        //View initialization
        mLakeDescriptionTextView = findViewById(R.id.description);
        mBackButton = findViewById(R.id.button_back_about);
        mLakeimageView = findViewById(R.id.macrophyte_image);
        mLakeThreatImageView = findViewById(R.id.threats);

        //Setting lake description
        String description = getResources().getString(R.string.description);
        mLakeDescriptionTextView.setText(description);

        //Loading images for Lake and it's threats
        Glide.with(this).load(R.drawable.about_us)
                .placeholder(R.drawable.about_us).into(mLakeimageView);
        Glide.with(this).load(R.drawable.threats)
                .placeholder(R.drawable.threats).into(mLakeThreatImageView);
    }

    private void setListeners(){
        //Attaching listener to Back button
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}