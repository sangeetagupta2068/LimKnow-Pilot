package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

public class MacrophyteItemActivity extends AppCompatActivity {

    private TextView macrophyteNameTextView, macrophyteCommonNameTextView, macrophyteTypeTextView;
    private TextView macrophyteDescriptionTextView, macrophytePictureCreditTextView;
    private ImageView macrophyteImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_item);
        initialize();

        Intent intent = getIntent();
        if(intent.getSerializableExtra("MACROPHYTE_ITEM")!=null){
            Macrophyte macrophyte = (Macrophyte) intent.getSerializableExtra("MACROPHYTE_ITEM");
            if(macrophyte.isInvasiveSpecies()){
                macrophyteNameTextView.setText(macrophyte.getMacrophyteName() + " " + "\n(Invasive Species)" );
            } else {
                macrophyteNameTextView.setText(macrophyte.getMacrophyteName());
            }
            macrophyteTypeTextView.setText(macrophyte.getMacrophyteType());
            macrophytePictureCreditTextView.setText(macrophyte.getMacrophyteImageCredit());
            macrophyteDescriptionTextView.setText(macrophyte.getMacrophyteDescription());
            macrophyteCommonNameTextView.setText(macrophyte.getCommonName());
            if(macrophyte.getMacrophyteImageURL()!=null) {
                Glide.with(this).load(Uri.parse(macrophyte.getMacrophyteImageURL())).into(macrophyteImageView);
            } else {
                macrophyteImageView.setImageResource(R.drawable.sample_macrophyte);
            }
        }
    }

    private void initialize(){
        macrophyteNameTextView = findViewById(R.id.macrophyte_scientific_name);
        macrophyteCommonNameTextView = findViewById(R.id.macrophyte_common_name);
        macrophyteDescriptionTextView = findViewById(R.id.macrophyte_about);
        macrophyteTypeTextView = findViewById(R.id.macrophyte_type);
        macrophytePictureCreditTextView = findViewById(R.id.macrophyte_picture_credits);
        macrophyteImageView = findViewById(R.id.macrophyte_image);
    }
}