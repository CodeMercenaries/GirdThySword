package com.code.codemercenaries.girdthyswordpro.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.ViewPagerStateAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.local.Version;
import com.code.codemercenaries.girdthyswordpro.fragments.ChapterFragment;
import com.code.codemercenaries.girdthyswordpro.fragments.DualChapterFragment;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;

import java.util.ArrayList;
import java.util.List;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class BibleActivity extends AppCompatActivity
        implements ChapterFragment.OnFragmentInteractionListener,
        DualChapterFragment.OnFragmentInteractionListener{

    FontHelper fontHelper;
    Spinner selectVersion;
    Spinner selectBook;
    Spinner selectChapter;
    ViewPager viewPager;
    ViewPagerStateAdapter viewPagerStateAdapter;

    PopulateViewPager task1;

    int primaryVersePos;
    int secondaryVersePos;

    int currVersionPos;
    int currBookPos;
    int currChapterPos;
    int currViewPagerPos;

    ArrayList<Version> versions;
    List<String> bookNames1;
    List<String> bookNames2;
    ArrayList<Integer> chaptersInEachBook;

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

        primaryVersePos = getIntent().getIntExtra(getString(R.string.title_intent_primary),0);
        secondaryVersePos = getIntent().getIntExtra(getString(R.string.title_intent_secondary),-1);

        currVersionPos = primaryVersePos;

        currViewPagerPos = 0;

        selectVersion = findViewById(R.id.selectVersion);
        selectBook = findViewById(R.id.selectBook);
        selectChapter = findViewById(R.id.selectChapter);
        viewPager = findViewById(R.id.viewPager);

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
                currVersionPos = position;
                addItemsOnBookSpinner();
                setupViewPager(viewPager);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectVersion.setSelection(primaryVersePos,true);
    }

    private void addItemsOnBookSpinner() {

        currBookPos = 0;

        DBHandler dbHandler = new DBHandler(this);
        bookNames1 = dbHandler.getBookNames(versions.get(currVersionPos).get_id());
        if(secondaryVersePos != -1) {
            bookNames2 = dbHandler.getBookNames(versions.get(secondaryVersePos).get_id());
        }
        ArrayAdapter<String> bookNamesAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, bookNames1);
        bookNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectBook.setAdapter(bookNamesAdapter);
        selectBook.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currBookPos = position;
                addItemsOnChapterSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectBook.setSelection(currBookPos,true);
    }

    private void addItemsOnChapterSpinner() {

        currChapterPos = 0;

        DBHandler dbHandler = new DBHandler(this);
        int numOfChap = dbHandler.getNumOfChap(versions.get(currVersionPos).get_id(), bookNames1.get(currBookPos));
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
                currChapterPos = position;
                currViewPagerPos = 0;
                for(int i=0;i<currBookPos;i++) {
                    currViewPagerPos += chaptersInEachBook.get(i);
                }
                currViewPagerPos += currChapterPos;
                viewPager.setCurrentItem(currViewPagerPos,true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectChapter.setSelection(currChapterPos,true);

    }

    private void setupViewPager(final ViewPager viewPager) {
        viewPagerStateAdapter = new ViewPagerStateAdapter(getSupportFragmentManager());

        PopulateViewPager populateViewPager = new PopulateViewPager();
        populateViewPager.execute();

        viewPager.setAdapter(viewPagerStateAdapter);
        currViewPagerPos = 0;
        viewPager.setCurrentItem(currViewPagerPos);

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public class PopulateViewPager extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(BibleActivity.this);
            progressDialog.setMessage("Loading Version");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            chaptersInEachBook = new ArrayList<>();
            DBHandler dbHandler = new DBHandler(BibleActivity.this);
            for(String bookName: bookNames1) {
                chaptersInEachBook.add(dbHandler.getNumOfChap(versions.get(currVersionPos).get_id(),bookName));
            }

            if(secondaryVersePos == -1) {
                for(int i=0;i<chaptersInEachBook.size();i++){
                    for(int j=1;j<=chaptersInEachBook.get(i);j++) {
                        ChapterFragment chapterFragment = ChapterFragment.newInstance(versions.get(currVersionPos).get_id(), bookNames1.get(i),j);
                        viewPagerStateAdapter.addFragment(chapterFragment,getResources().getString(R.string.book_name_with_chap_num, bookNames1.get(i),j));
                    }
                }
            } else {
                for(int i=0;i<chaptersInEachBook.size();i++) {
                    for (int j = 1; j <= chaptersInEachBook.get(i); j++) {
                        DualChapterFragment dualChapterFragment = DualChapterFragment.newInstance(versions.get(currVersionPos).get_id(),versions.get(secondaryVersePos).get_id(), bookNames1.get(i), bookNames2.get(i), j);
                        viewPagerStateAdapter.addFragment(dualChapterFragment,getResources().getString(R.string.book_name_with_chap_num,bookNames2.get(i),j));
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            viewPagerStateAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }
    }
}
