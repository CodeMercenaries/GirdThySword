package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectReviewTypeActivity extends AppCompatActivity {

    FontHelper fontHelper;
    FloatingActionButton manualReview;
    FloatingActionButton speechReview;
    TextView chunkTitle;
    TextView chunkText;

    String version;
    String bookName;
    int chapNum;
    int startVerseNum;
    int endVerseNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_select_review_type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        version = getIntent().getStringExtra(DBConstants.REVIEW_VERSION);
        bookName = getIntent().getStringExtra(DBConstants.REVIEW_BOOK_NAME);
        chapNum = getIntent().getIntExtra(DBConstants.REVIEW_CHAP_NUM,1);
        startVerseNum = getIntent().getIntExtra(DBConstants.REVIEW_START_VERSE_NUM,1);
        endVerseNum = getIntent().getIntExtra(DBConstants.REVIEW_END_VERSE_NUM,1);

        chunkTitle = findViewById(R.id.chunkTitle);
        chunkText = findViewById(R.id.chunkText);
        manualReview = findViewById(R.id.selfReview);
        speechReview = findViewById(R.id.speechReview);

        StringBuilder builder = new StringBuilder();
        builder.append(bookName);
        builder.append(" ");
        builder.append(chapNum);
        builder.append(":");
        builder.append(startVerseNum);
        builder.append("-");
        builder.append(endVerseNum);
        chunkTitle.setText(builder.toString());

        DBHandler dbHandler = new DBHandler(this);
        StringBuilder builder1 = new StringBuilder();
        for(int i=startVerseNum;i<=endVerseNum;i++){
            builder1.append(i);
            builder1.append(" ");
            builder1.append(dbHandler.getVerse(version,bookName,chapNum,i));
            if(i!=endVerseNum) {
                builder1.append("\n\n");
            }
        }
        chunkText.setText(builder1.toString());

        manualReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectReviewTypeActivity.this,ReviewActivity.class);
                intent.putExtra(DBConstants.REVIEW_TYPE, DBConstants.MANUAL_REVIEW_TYPE);
                startActivity(intent);
            }
        });

        speechReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectReviewTypeActivity.this,ReviewActivity.class);
                intent.putExtra(DBConstants.REVIEW_TYPE, DBConstants.SPEECH_REVIEW_TYPE);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
