package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

public class MacrophyteItemActivity extends AppCompatActivity {

    private TextView macrophyteNameTextView, macrophyteCommonNameTextView, macrophyteTypeTextView;
    private TextView macrophyteDescriptionTextView, macrophytePictureCreditTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_item);
        initialize();

        Intent intent = getIntent();
        if(intent.getSerializableExtra("MACROPHYTE_ITEM")!=null){
            Macrophyte macrophyte = (Macrophyte) intent.getSerializableExtra("MACROPHYTE_ITEM");
            Toast.makeText(getApplicationContext(),macrophyte.getMacrophyteName(),Toast.LENGTH_SHORT).show();
            macrophyteNameTextView.setText(macrophyte.getMacrophyteName());
            macrophyteTypeTextView.setText(macrophyte.getMacrophyteType());
            macrophytePictureCreditTextView.setText(macrophyte.getMacrophyteImageCredit());
            macrophyteDescriptionTextView.setText(macrophyte.getMacrophyteDescription());
            macrophyteCommonNameTextView.setText(macrophyte.getCommonName());
        }
    }

    private void initialize(){
        macrophyteNameTextView = findViewById(R.id.macrophyte_scientific_name);
        macrophyteCommonNameTextView = findViewById(R.id.macrophyte_common_name);
        macrophyteDescriptionTextView = findViewById(R.id.macrophyte_about);
        macrophyteTypeTextView = findViewById(R.id.macrophyte_type);
        macrophytePictureCreditTextView = findViewById(R.id.macrophyte_picture_credits);
    }
}