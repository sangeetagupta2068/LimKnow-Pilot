package com.pukhuriandbeels.limknowpilot;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PrivacyPolicyActivity extends AppCompatActivity {
    //View declaration
    private TextView[] mPrivacyPolicyTextViews;
    private Button mBackButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        initialize();
        setListeners();
    }

    private void initialize(){
        //View initialization
        mPrivacyPolicyTextViews = new TextView[18];
        mPrivacyPolicyTextViews[0] = findViewById(R.id.privacy_policy_label);
        mPrivacyPolicyTextViews[1] = findViewById(R.id.privacy_policy);
        mPrivacyPolicyTextViews[2] = findViewById(R.id.information_collection_and_use_label);
        mPrivacyPolicyTextViews[3] = findViewById(R.id.information_collection_and_use);
        mPrivacyPolicyTextViews[4] = findViewById(R.id.log_data_label);
        mPrivacyPolicyTextViews[5] = findViewById(R.id.log_data);
        mPrivacyPolicyTextViews[6] = findViewById(R.id.cookies_label);
        mPrivacyPolicyTextViews[7] = findViewById(R.id.cookies);
        mPrivacyPolicyTextViews[8] = findViewById(R.id.service_provider_label);
        mPrivacyPolicyTextViews[9] = findViewById(R.id.service_provider);
        mPrivacyPolicyTextViews[10] = findViewById(R.id.children_privacy_label);
        mPrivacyPolicyTextViews[11] = findViewById(R.id.children_privacy);
        mPrivacyPolicyTextViews[12] = findViewById(R.id.contact_us_label);
        mPrivacyPolicyTextViews[13] = findViewById(R.id.contact_us);
        mPrivacyPolicyTextViews[14] = findViewById(R.id.changes_to_privacy_policy_label);
        mPrivacyPolicyTextViews[15] = findViewById(R.id.changes_to_privacy_policy);
        mPrivacyPolicyTextViews[16] = findViewById(R.id.security_label);
        mPrivacyPolicyTextViews[17] = findViewById(R.id.security);

        mBackButton = findViewById(R.id.button_submit_policy);
    }

    private void setListeners(){
        //Attaching listener to Back button
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //finish activity
                finish();
            }
        });
    }

    @Override
    protected void onStop() {
        //deallocate memory once activity stops
        for(int count = 0; count < mPrivacyPolicyTextViews.length; count++){
            mPrivacyPolicyTextViews[count].setText(null);
        }
        mBackButton.setText(null);

        super.onStop();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}