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

    private TextView textView;
    private Button button;
    private ImageView imageViewLake, imageViewThreats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beels_and_pukhuris);

        textView = findViewById(R.id.description);
        button = findViewById(R.id.button_back_about);
        imageViewLake = findViewById(R.id.macrophyte_image);
        imageViewThreats = findViewById(R.id.threats);

        String description = getResources().getString(R.string.description);
        textView.setText(description);

        Glide.with(this).load(R.drawable.about_us).placeholder(R.drawable.about_us).into(imageViewLake);
        Glide.with(this).load(R.drawable.threats).placeholder(R.drawable.threats).into(imageViewThreats);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}