package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.SettingsRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.local.SettingsItem;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;

import java.util.ArrayList;
import java.util.Arrays;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SettingsActivity extends AppCompatActivity {

    FontHelper fontHelper;
    SettingsRecycleListAdapter settingsListAdapter;
    RecyclerView list;

    ArrayList<SettingsItem> settingsItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        settingsItems = new ArrayList<>(Arrays.asList(
                new SettingsItem("Opt-out of Leaderboard","Skip your profile in Leaderboard")
        ));

        list = findViewById(R.id.list);
        settingsListAdapter = new SettingsRecycleListAdapter(this,settingsItems);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        list.setLayoutManager(linearLayoutManager);
        list.setAdapter(settingsListAdapter);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
