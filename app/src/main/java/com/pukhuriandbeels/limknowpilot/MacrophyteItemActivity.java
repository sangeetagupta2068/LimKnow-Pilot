package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MacrophyteItemActivity extends AppCompatActivity {
    //View declaration
    private TextView mMacrophyteNameTextView, mMacrophyteCommonNameTextView, mMacrophyteTypeTextView;
    private TextView mMacrophyteDescriptionTextView, mMacrophytePictureCreditTextView;
    private ImageView mMacrophyteImageView;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_item);

        initialize();
        setListeners();
    }

    private void initialize() {
        //View initialization
        mMacrophyteNameTextView = findViewById(R.id.macrophyte_scientific_name);
        mMacrophyteCommonNameTextView = findViewById(R.id.macrophyte_common_name);
        mMacrophyteDescriptionTextView = findViewById(R.id.macrophyte_about);
        mMacrophyteTypeTextView = findViewById(R.id.macrophyte_type);
        mMacrophytePictureCreditTextView = findViewById(R.id.macrophyte_picture_credits);
        mMacrophyteImageView = findViewById(R.id.macrophyte_image);
        mBackButton = findViewById(R.id.button_submit_macrophyte);

        Intent intent = getIntent();
        //On successful retrieval of macrophyte data, set view values
        if (intent.getSerializableExtra("MACROPHYTE_ITEM") != null) {
            Macrophyte macrophyte = (Macrophyte) intent.getSerializableExtra("MACROPHYTE_ITEM");
            //Add invasive species to the name of every invasive species macrophyte
            if (macrophyte.isInvasiveSpecies()) {
                mMacrophyteNameTextView.setText(macrophyte.getMacrophyteName()
                        + " " + "\n(Invasive Species)");
            } else {
                mMacrophyteNameTextView.setText(macrophyte.getMacrophyteName());
            }
            mMacrophyteTypeTextView.setText(macrophyte.getMacrophyteType());
            mMacrophytePictureCreditTextView.setText(macrophyte.getMacrophyteImageCredit());
            mMacrophyteDescriptionTextView.setText(macrophyte.getMacrophyteDescription());
            mMacrophyteCommonNameTextView.setText(macrophyte.getCommonName());
            if (macrophyte.getMacrophyteImageURL() != null) {
                //Load macrophyte image
                Glide.with(this).load(Uri.parse
                        (macrophyte.getMacrophyteImageURL())).into(mMacrophyteImageView);
            } else {
                //Set macrophyte to sample image if image load failed
                mMacrophyteImageView.setImageResource(R.drawable.sample_macrophyte);
            }
        }
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