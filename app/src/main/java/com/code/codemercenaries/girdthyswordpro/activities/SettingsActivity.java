package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.SettingsRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.local.SettingsItem;
import com.code.codemercenaries.girdthyswordpro.beans.remote.User;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SettingsActivity extends AppCompatActivity {

    private static String TAG = "SettingsActivity";

    FirebaseAuth mAuth;
    DatabaseReference userReference;
    FontHelper fontHelper;
    SettingsRecycleListAdapter settingsListAdapter;
    RecyclerView list;

    ArrayList<SettingsItem> settingsItems;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        userReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(mAuth.getCurrentUser().getUid());
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    settingsItems = new ArrayList<>(Arrays.asList(
                            new SettingsItem("Opt-out of Leaderboard", "Skip your profile in Leaderboard")
                    ));

                    list = findViewById(R.id.list);
                    settingsListAdapter = new SettingsRecycleListAdapter(SettingsActivity.this, settingsItems, user);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SettingsActivity.this);
                    list.setLayoutManager(linearLayoutManager);
                    list.setAdapter(settingsListAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
