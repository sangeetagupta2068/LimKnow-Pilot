package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import androidx.appcompat.app.AppCompatActivity;

public class MacrophyteItemActivity extends AppCompatActivity {

    private TextView macrophyteNameTextView, macrophyteCommonNameTextView, macrophyteTypeTextView;
    private TextView macrophyteDescriptionTextView, macrophytePictureCreditTextView;
    private ImageView macrophyteImageView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_item);
        initialize();

        Intent intent = getIntent();
        if (intent.getSerializableExtra("MACROPHYTE_ITEM") != null) {
            Macrophyte macrophyte = (Macrophyte) intent.getSerializableExtra("MACROPHYTE_ITEM");
            if (macrophyte.isInvasiveSpecies()) {
                macrophyteNameTextView.setText(macrophyte.getMacrophyteName() + " " + "\n(Invasive Species)");
            } else {
                macrophyteNameTextView.setText(macrophyte.getMacrophyteName());
            }
            macrophyteTypeTextView.setText(macrophyte.getMacrophyteType());
            macrophytePictureCreditTextView.setText(macrophyte.getMacrophyteImageCredit());
            macrophyteDescriptionTextView.setText(macrophyte.getMacrophyteDescription());
            macrophyteCommonNameTextView.setText(macrophyte.getCommonName());
            if (macrophyte.getMacrophyteImageURL() != null) {
                Glide.with(this).load(Uri.parse(macrophyte.getMacrophyteImageURL())).into(macrophyteImageView);
            } else {
                macrophyteImageView.setImageResource(R.drawable.sample_macrophyte);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initialize() {
        macrophyteNameTextView = findViewById(R.id.macrophyte_scientific_name);
        macrophyteCommonNameTextView = findViewById(R.id.macrophyte_common_name);
        macrophyteDescriptionTextView = findViewById(R.id.macrophyte_about);
        macrophyteTypeTextView = findViewById(R.id.macrophyte_type);
        macrophytePictureCreditTextView = findViewById(R.id.macrophyte_picture_credits);
        macrophyteImageView = findViewById(R.id.macrophyte_image);
        button = findViewById(R.id.button_submit_macrophyte);
    }
}