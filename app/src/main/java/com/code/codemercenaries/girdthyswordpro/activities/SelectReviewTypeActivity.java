package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectReviewTypeActivity extends AppCompatActivity {

    DatabaseReference chunkReference;
    FirebaseAuth mAuth;

    FontHelper fontHelper;
    FloatingActionButton manualReview;
    FloatingActionButton speechReview;
    TextView chunkTitle;
    TextView chunkText;

    String chunkID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_select_review_type);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        chunkID = getIntent().getStringExtra(DBConstants.REVIEW_CHUNK_ID);

        chunkTitle = findViewById(R.id.chunk_title);
        chunkText = findViewById(R.id.chunkText);
        manualReview = findViewById(R.id.selfReview);
        speechReview = findViewById(R.id.speechReview);

        chunkTitle.setText("");
        chunkText.setText("");

        mAuth = FirebaseAuth.getInstance();
        chunkReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_CHUNKS).child(mAuth.getCurrentUser().getUid()).child(chunkID);

        chunkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Chunk chunk = dataSnapshot.getValue(Chunk.class);
                StringBuilder builder = new StringBuilder();
                StringBuilder builder1 = new StringBuilder();

                if(chunk != null) {
                    builder.append(chunk.getBookName());
                    builder.append(" ");
                    builder.append(chunk.getChapterNum());
                    builder.append(":");
                    builder.append(chunk.getStartVerseNum());
                    builder.append("-");
                    builder.append(chunk.getEndVerseNum());

                    DBHandler dbHandler = new DBHandler(SelectReviewTypeActivity.this);
                    for(int i=chunk.getStartVerseNum();i<=chunk.getEndVerseNum();i++){
                        builder1.append(i);
                        builder1.append(" ");
                        builder1.append(dbHandler.getVerse(chunk.getVersionID(),chunk.getBookName(),chunk.getChapterNum(),i));
                        if(i!=chunk.getEndVerseNum()) {
                            builder1.append("\n\n");
                        }
                    }
                }
                chunkTitle.setText(builder.toString());
                chunkText.setText(builder1.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        manualReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectReviewTypeActivity.this,ReviewActivity.class);
                intent.putExtra(DBConstants.REVIEW_TYPE, DBConstants.MANUAL_REVIEW_TYPE);
                intent.putExtra(DBConstants.REVIEW_CHUNK_ID, chunkID);
                startActivity(intent);
            }
        });

        speechReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectReviewTypeActivity.this,ReviewActivity.class);
                intent.putExtra(DBConstants.REVIEW_TYPE, DBConstants.SPEECH_REVIEW_TYPE);
                intent.putExtra(DBConstants.REVIEW_CHUNK_ID, chunkID);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }


}
