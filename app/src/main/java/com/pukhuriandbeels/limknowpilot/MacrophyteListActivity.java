package com.pukhuriandbeels.limknowpilot;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pukhuriandbeels.limknowpilot.adapter.MacrophyteAdapter;
import com.pukhuriandbeels.limknowpilot.model.Macrophyte;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class MacrophyteListActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private List<Macrophyte> macrophytes = new ArrayList<>();
    private MacrophyteAdapter macrophyteAdapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_macrophyte_list);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.about:
                        break;
                    case R.id.policy:
                        break;

                    case R.id.macrophytes:
                        break;
                    case R.id.augmented_reality_game:
                        Intent lakeARIntent = new Intent(MacrophyteListActivity.this, LakeARQuizActivity.class);
                        startActivity(lakeARIntent);
                        break;
                    case R.id.reporting:
                        Intent intent = new Intent(MacrophyteListActivity.this, InvasiveSpeciesWatchActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.badges:
                        break;
                    case R.id.edit_profile:
                        break;
                    case R.id.sign_out:
                        break;

                    case R.id.share:
                        break;
                    case R.id.contact_us:
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        navigationView.setCheckedItem(R.id.macrophytes);
        macrophyteAdapter = new MacrophyteAdapter(this, macrophytes);
        listView = findViewById(R.id.macrophyte_list);

        setFirebaseFirestoreTransaction();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Macrophyte macrophyte = macrophytes.get(position);
                Intent intent = new Intent(MacrophyteListActivity.this, MacrophyteItemActivity.class);
                intent.putExtra("MACROPHYTE_ITEM", macrophyte);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void setFirebaseFirestoreTransaction() {
        FirebaseAuth.getInstance();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = firebaseFirestore.collection("Macrophytes");

        collectionReference.orderBy("name").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                    String name = documentSnapshot.getString("name");
                    String commonName = documentSnapshot.getString("common_name");
                    String type = documentSnapshot.getString("type");
                    String imageURL = documentSnapshot.getString("image_url");
                    boolean invasiveSpecies = documentSnapshot.getBoolean("invasive_species");
                    String imageCredits = documentSnapshot.getString("image_credits");
                    String description = documentSnapshot.getString("about");

                    Log.i("FIREBASE_DATA", name + "\n" + documentSnapshot.getString("image_url"));

                    Macrophyte macrophyte = new Macrophyte(name, type, commonName, description, imageURL, imageCredits, invasiveSpecies);
                    macrophytes.add(macrophyte);
                }

                macrophyteAdapter = new MacrophyteAdapter(getApplicationContext(), macrophytes);
                listView.setAdapter(macrophyteAdapter);
            }
        });
        collectionReference.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "HELLO FAIL", Toast.LENGTH_SHORT).show();
            }
        });
    }
}