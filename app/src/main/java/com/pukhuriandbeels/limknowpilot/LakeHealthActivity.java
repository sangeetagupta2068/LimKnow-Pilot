package com.pukhuriandbeels.limknowpilot;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LakeHealthActivity extends AppCompatActivity {

    private RadioButton[] radioButtons;
    private RadioGroup radioGroup;
    private CheckBox[] checkBoxes;
    private EditText editTextDeadFish, editTextLakeHealthProblem;
    private Button buttonLakeHealth;
    private String lakeWaterColour;
    private String deadFish;
    private String lakeHealthProblem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lake_health);
        initialize();
        setListeners();
    }

    private void initialize() {
        radioGroup = findViewById(R.id.radio_group_lake_health);
        radioButtons = new RadioButton[4];
        radioButtons[0] = findViewById(R.id.radio_1);
        radioButtons[1] = findViewById(R.id.radio_2);
        radioButtons[2] = findViewById(R.id.radio_3);
        radioButtons[3] = findViewById(R.id.radio_4);

        checkBoxes = new CheckBox[5];
        checkBoxes[0] = findViewById(R.id.lake_health_checkbox_1);
        checkBoxes[1] = findViewById(R.id.lake_health_checkbox_2);
        checkBoxes[2] = findViewById(R.id.lake_health_checkbox_3);
        checkBoxes[3] = findViewById(R.id.lake_health_checkbox_4);
        checkBoxes[4] = findViewById(R.id.lake_health_checkbox_5);

        editTextDeadFish = findViewById(R.id.edit_text_lake_health_dead_fish);
        editTextLakeHealthProblem = findViewById(R.id.edit_text_other_lake_health);

        buttonLakeHealth = findViewById(R.id.button_lake_health);
    }

    private void setListeners() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(group.getCheckedRadioButtonId());
                lakeWaterColour = radioButton.getText().toString();
            }
        });
        checkBoxes[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    editTextLakeHealthProblem.setVisibility(View.VISIBLE);
                else
                    editTextLakeHealthProblem.setVisibility(View.GONE);
            }
        });
        buttonLakeHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deadFish = editTextDeadFish.getText().toString();
                lakeHealthProblem = "";
                if (checkBoxes[checkBoxes.length - 1].isChecked()) {
                    lakeHealthProblem = editTextLakeHealthProblem.getText().toString() + ",";
                }
                for (int count = 0; count < checkBoxes.length - 2; count++) {
                    if (checkBoxes[count].isChecked()) {
                        lakeHealthProblem = lakeHealthProblem + checkBoxes[count].getText().toString() + ",";
                    }
                }
                Toast.makeText(LakeHealthActivity.this,
                        lakeHealthProblem
                                + " "
                                + deadFish
                                + " "
                                + lakeWaterColour,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}