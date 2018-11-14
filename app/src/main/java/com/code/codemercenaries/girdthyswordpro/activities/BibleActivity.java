package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.local.Version;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BibleActivity extends AppCompatActivity {

    FontHelper fontHelper;
    Spinner selectVersion;
    Spinner selectBook;
    Spinner selectChapter;

    int currVersionPos;
    int currBookPos;
    int currChapterPos;

    List<Version> versions;
    List<String> bookNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_bible);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        int primaryVersePos = getIntent().getIntExtra(getString(R.string.title_intent_primary),0);
        int secondaryVersePos = getIntent().getIntExtra(getString(R.string.title_intent_secondary),-1);

        currVersionPos = primaryVersePos;
        currBookPos = 0;
        currChapterPos = 0;

        selectVersion = findViewById(R.id.selectVersion);
        selectBook = findViewById(R.id.selectBook);
        selectChapter = findViewById(R.id.selectChapter);

        DBHandler dbHandler = new DBHandler(this);
        versions = dbHandler.getAllVersions();
        List<String> versionsList = new ArrayList<>();

        for(Version version: versions) {
            versionsList.add(version.get_name() + " (" + version.get_lang() + ")");
        }

        ArrayAdapter<String> versionAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,versionsList);
        versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectVersion.setAdapter(versionAdapter);
        selectVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemsOnBookSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectVersion.setSelection(primaryVersePos,true);
    }

    private void addItemsOnBookSpinner() {
        DBHandler dbHandler = new DBHandler(this);
        bookNames = dbHandler.getBookNames(versions.get(currVersionPos).get_id());

        ArrayAdapter<String> bookNamesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bookNames);
        bookNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectBook.setAdapter(bookNamesAdapter);
        selectBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemsOnChapterSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectBook.setSelection(currBookPos,true);
    }

    private void addItemsOnChapterSpinner() {
        DBHandler dbHandler = new DBHandler(this);
        int numOfChap = dbHandler.getNumOfChap(versions.get(currVersionPos).get_id(),bookNames.get(currBookPos));
        List<Integer> chaptersList = new ArrayList<>();
        for(int i=1;i<=numOfChap;i++) {
            chaptersList.add(i);
        }

        ArrayAdapter<Integer> chaptersAdapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,chaptersList);
        chaptersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectChapter.setAdapter(chaptersAdapter);
        selectChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectChapter.setSelection(currChapterPos,true);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
