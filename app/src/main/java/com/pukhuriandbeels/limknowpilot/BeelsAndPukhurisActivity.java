package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

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

        textView.setText("In the southwestern part of Guwahati city lies Deepor Beel, the only Ramsar site of Assam. The name is derived from dipa and beel which loosely translates to a wetland inhabited by the elephants. The water from  Basistha and Kalamoni rivers feed the wetland.  As a natural storm water reservoir it acts as a buffer to Guwahati city’s flooding problem. \n" +
                "Surrounded by the Rani and Garbhanga reserve forest this water body supports immense biodiversity. It is a bird watchers delight with around 219 species of bird. Around 70 species of migratory birds from around the world visit this wetland. It has been declared as an Important Bird Area.\n" +
                "Besides the birds the beel is home and source of food for the microscopic phytoplankton to the majestic elephants.\n" +
                "It is an important source of livelihood for the fishermen community living in the nearby villages. Over the last few years this wetland is under severe threat.\n");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        imageViewThreats.setImageDrawable(null);
        imageViewLake.setImageDrawable(null);
        super.onStop();
    }
}