package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.fragments.ManualReviewFragment;
import com.code.codemercenaries.girdthyswordpro.fragments.SpeechReviewFragment;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;

import java.util.Objects;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ReviewActivity extends AppCompatActivity
        implements ManualReviewFragment.OnFragmentInteractionListener,
        SpeechReviewFragment.OnFragmentInteractionListener{

    FontHelper fontHelper;
    FrameLayout fragmentHolder;
    private String TAG = "ReviewActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_review);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentHolder = findViewById(R.id.fragment_holder);

        Log.d(TAG, "Initialized");

        String reviewType = getIntent().getStringExtra(DBConstants.REVIEW_TYPE);
        String chunkID = getIntent().getStringExtra(DBConstants.REVIEW_CHUNK_ID);

        if(Objects.equals(reviewType, DBConstants.MANUAL_REVIEW_TYPE)) {
            ManualReviewFragment manualReviewFragment = ManualReviewFragment.newInstance(chunkID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, manualReviewFragment);
            fragmentTransaction.commit();
            Log.d(TAG, "ManualReviewFragment Set");
        } else if(Objects.equals(reviewType, DBConstants.SPEECH_REVIEW_TYPE)) {
            SpeechReviewFragment speechReviewFragment = SpeechReviewFragment.newInstance(chunkID);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_holder, speechReviewFragment);
            fragmentTransaction.commit();
            Log.d(TAG, "SpeechReviewFragment Set");
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
