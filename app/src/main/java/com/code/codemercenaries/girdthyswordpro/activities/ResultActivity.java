package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Section;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    FontHelper fontHelper;
    DatabaseReference chunkReference;
    DatabaseReference sectionReference;
    DatabaseReference userBibleReference;
    FirebaseAuth mAuth;

    TextView finalScoreTV;
    TextView spaceTV;
    TextView chunkTitle;
    TextView nextDateOfReviewTV;
    Button done;

    int finalScore;
    String chunkID;
    Section mSection;
    Chunk mChunk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        finalScoreTV = findViewById(R.id.final_score);
        spaceTV = findViewById(R.id.space);
        chunkTitle = findViewById(R.id.chunk_title);
        nextDateOfReviewTV = findViewById(R.id.next_date_of_review);
        done = findViewById(R.id.done);

        finalScore = getIntent().getIntExtra(DBConstants.REVIEW_FINAL_SCORE,0);
        chunkID = getIntent().getStringExtra(DBConstants.REVIEW_CHUNK_ID);

        finalScoreTV.setText(String.format(Locale.getDefault(),"%d",finalScore));

        mAuth = FirebaseAuth.getInstance();

        chunkReference = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_CHUNKS).
                child(mAuth.getCurrentUser().getUid()).child(chunkID);
        userBibleReference = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid());

        chunkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChunk = dataSnapshot.getValue(Chunk.class);

                sectionReference = FirebaseDatabase.getInstance().
                        getReference(DBConstants.FIREBASE_TABLE_SECTIONS).child(mAuth.getCurrentUser().getUid()).child(mChunk.getSectionID());
                sectionReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        mSection = dataSnapshot.getValue(Section.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.toString());
                    }
                });

                if(mChunk != null) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(mChunk.getBookName());
                    builder.append(" ");
                    builder.append(mChunk.getChapterNum());
                    builder.append(":");
                    builder.append(mChunk.getStartVerseNum());
                    builder.append("-");
                    builder.append(mChunk.getEndVerseNum());
                    chunkTitle.setText(builder.toString());

                    int newSpace = mChunk.getSpace()*calculateSpaceMultiplyingFactor();
                    if(newSpace < 1){
                        newSpace = 1;
                    }
                    spaceTV.setText(String.format(Locale.getDefault(),"%d",newSpace));
                    chunkReference.child(DBConstants.FIREBASE_C_KEY_SPACE).setValue(newSpace);

                    SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT,Locale.US);
                    Date date = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    c.add(Calendar.DATE, newSpace);
                    date = c.getTime();
                    nextDateOfReviewTV.setText(String.format(Locale.getDefault(),"%s",df.format(date)));
                    chunkReference.child(DBConstants.FIREBASE_C_KEY_NEXT_DATE_OF_REVIEW).setValue(df.format(date));

                    if(finalScore >= DBConstants.MASTERED_MIN_THRESHOLD) {
                        chunkReference.child(DBConstants.FIREBASE_C_MASTERED).setValue(true);
                        for(int i=mChunk.getStartVerseNum();i<=mChunk.getEndVerseNum();i++) {
                            userBibleReference.child(mChunk.getVersionID()).
                                    child(mChunk.getBookName()).
                                    child(String.valueOf(mChunk.getChapterNum())).
                                    child(String.valueOf(i)).
                                    child(DBConstants.FIREBASE_UB_KEY_MEMORY).
                                    setValue(DBConstants.CODE_MEMORIZED);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chunkReference = FirebaseDatabase.getInstance().
                        getReference(DBConstants.FIREBASE_TABLE_CHUNKS).child(mAuth.getCurrentUser().getUid());
                chunkReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean sectionMemorized = true;
                        ArrayList<Chunk> chunks = new ArrayList<>();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Chunk chunk = snapshot.getValue(Chunk.class);
                            if(chunk!= null && mChunk.getSectionID().equals(chunk.getSectionID())) {
                                chunks.add(chunk);
                                if(!chunk.isMastered()) {
                                    sectionMemorized = false;
                                    break;
                                }
                            }
                        }
                        if(sectionMemorized) {
                            for(Chunk c: chunks) {
                                chunkReference.child(c.getChunkID()).setValue(null);
                            }

                            String newChunkID = chunkReference.push().getKey();
                            SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT,Locale.US);
                            Date date = new Date();
                            Calendar c = Calendar.getInstance();
                            c.setTime(date);
                            c.add(Calendar.DATE, 2);
                            date = c.getTime();
                            Chunk chunk = new Chunk(newChunkID,mSection.getSectionID(),
                                    mSection.getVersionID(),
                                    mSection.getBookName(),
                                    mSection.getChapterNum(),
                                    mSection.getStartVerseNum(),
                                    mSection.getEndVerseNum(), 2, 1,
                                    df.format(date), true);

                            if(newChunkID!=null) {
                                chunkReference.child(newChunkID).setValue(chunk);
                            }

                            Toast.makeText(ResultActivity.this,"Merged Section",Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d(TAG, databaseError.toString());
                    }
                });
                Intent intent = new Intent(ResultActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

    }

    private int calculateSpaceMultiplyingFactor() {
        int factor;

        if(finalScore >= DBConstants.MASTERED_MIN_THRESHOLD) {
            factor = 2;
        } else if(finalScore >= DBConstants.MAINTAIN_MIN_THRESHOLD) {
            factor = 1;
        } else {
            factor = 1/2;
        }

        return factor;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

}
