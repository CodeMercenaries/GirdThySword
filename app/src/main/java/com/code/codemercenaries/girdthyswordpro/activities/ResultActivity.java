package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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
import com.code.codemercenaries.girdthyswordpro.utilities.Algorithms;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class ResultActivity extends AppCompatActivity {

    private static final String TAG = "ResultActivity";

    FontHelper fontHelper;
    DatabaseReference chunkReference;
    DatabaseReference sectionReference;
    DatabaseReference userBibleReference;
    DatabaseReference userReference;
    FirebaseAuth mAuth;

    TextView finalScoreTV;
    TextView spaceTV;
    TextView chunkTitle;
    TextView nextDateOfReviewTV;
    Button done;

    int finalScore;
    int newSpace;
    String chunkID;
    Section mSection;
    Chunk mChunk;
    Date newDate;
    Long versesMemorized;

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
        userReference = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USERS).
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

                    // Compute new space
                    newSpace = mChunk.getSpace() * calculateSpaceMultiplyingFactor();
                    if(newSpace < 1){
                        newSpace = 1;
                    }
                    spaceTV.setText(String.format(Locale.getDefault(),"%d",newSpace));

                    // Compute new next date of review
                    SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT,Locale.US);
                    newDate = new Date();
                    Calendar c = Calendar.getInstance();
                    c.setTime(newDate);
                    c.add(Calendar.DATE, newSpace);
                    newDate = c.getTime();
                    nextDateOfReviewTV.setText(String.format(Locale.getDefault(), "%s", df.format(newDate)));


                    // Fetch previous versesMemorizedCount
                    userReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            versesMemorized = dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED)
                                    .getValue(Long.class);
                            userReference.removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d(TAG, databaseError.toString());
                        }
                    });

                }
                chunkReference.removeEventListener(this);
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
                        getReference(DBConstants.FIREBASE_TABLE_CHUNKS).
                        child(mAuth.getCurrentUser().getUid()).child(chunkID);
                SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT, Locale.US);
                chunkReference.child(DBConstants.FIREBASE_C_KEY_SPACE).setValue(newSpace);
                chunkReference.child(DBConstants.FIREBASE_C_KEY_NEXT_DATE_OF_REVIEW).setValue(df.format(newDate));

                // If chunk is now memorized, set chunk to mastered and update user Bible
                if (finalScore >= DBConstants.MASTERED_MIN_THRESHOLD_SCORE) {
                    // Set chunk status to memorized
                    chunkReference.child(DBConstants.FIREBASE_C_MASTERED).setValue(true);
                    for (int i = mChunk.getStartVerseNum(); i <= mChunk.getEndVerseNum(); i++) {
                        userBibleReference.child(mChunk.getVersionID()).
                                child(mChunk.getBookName()).
                                child(String.valueOf(mChunk.getChapterNum())).
                                child(String.valueOf(i)).
                                child(DBConstants.FIREBASE_UB_KEY_MEMORY).
                                setValue(DBConstants.CODE_MEMORIZED);
                    }

                    // If chunk was not already mastered before, increase versesMemorized count for user
                    if (!mChunk.isMastered()) {
                        userReference = FirebaseDatabase.getInstance().
                                getReference(DBConstants.FIREBASE_TABLE_USERS).
                                child(mAuth.getCurrentUser().getUid());
                        userReference.child(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED).
                                setValue(versesMemorized + (mChunk.getEndVerseNum() - mChunk.getStartVerseNum() + 1));
                    }
                }
                // If chunk is not memorized now, check if space is less than minimum mastered threshold space
                else {
                    if (newSpace < DBConstants.MASTERED_MIN_THRESHOLD_SPACE) {
                        // Set chunk status to added
                        chunkReference.child(DBConstants.FIREBASE_C_MASTERED).setValue(false);
                        for (int i = mChunk.getStartVerseNum(); i <= mChunk.getEndVerseNum(); i++) {
                            userBibleReference.child(mChunk.getVersionID()).
                                    child(mChunk.getBookName()).
                                    child(String.valueOf(mChunk.getChapterNum())).
                                    child(String.valueOf(i)).
                                    child(DBConstants.FIREBASE_UB_KEY_MEMORY).
                                    setValue(DBConstants.CODE_ADDED);
                        }

                        // If chunk was already mastered before, decrease versesMemorized count for user
                        if (mChunk.isMastered()) {
                            userReference = FirebaseDatabase.getInstance().
                                    getReference(DBConstants.FIREBASE_TABLE_USERS).
                                    child(mAuth.getCurrentUser().getUid());
                            userReference.child(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED).
                                    setValue(versesMemorized - (mChunk.getEndVerseNum() - mChunk.getStartVerseNum() + 1));
                        }
                    }
                }

                // Merging Chunks
                chunkReference = FirebaseDatabase.getInstance().
                        getReference(DBConstants.FIREBASE_TABLE_CHUNKS).child(mAuth.getCurrentUser().getUid());
                chunkReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get all chunks, check if section memorized
                        boolean sectionMemorized = true;
                        ArrayList<Chunk> chunks = new ArrayList<>();
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                            Chunk chunk = snapshot.getValue(Chunk.class);
                            if(chunk!= null && mChunk.getSectionID().equals(chunk.getSectionID())) {
                                chunks.add(chunk);
                                if(!chunk.isMastered()) {
                                    sectionMemorized = false;
                                }
                            }
                        }

                        // Update next chunk's next date of review from NA to current date
                        if (finalScore >= DBConstants.MASTERED_MIN_THRESHOLD_SCORE) {
                            Collections.sort(chunks, new Comparator<Chunk>() {
                                @Override
                                public int compare(Chunk c1, Chunk c2) {
                                    return Integer.compare(c1.getSequenceNum(), c2.getSequenceNum());
                                }
                            });

                            Algorithms algorithms = new Algorithms();
                            int indexOfNextChunk = algorithms.searchChunk(chunks, mChunk.getChunkID()) + 1;

                            if (indexOfNextChunk != -1
                                    && indexOfNextChunk < chunks.size()
                                    && chunks.get(indexOfNextChunk).getNextDateOfReview().equals(DBConstants.NEXT_DATE_OF_REVIEW_NA)) {

                                SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT, Locale.US);
                                String newDate = df.format(new Date());

                                chunkReference.child(chunks.get(indexOfNextChunk).getChunkID()).
                                        child(DBConstants.FIREBASE_C_KEY_NEXT_DATE_OF_REVIEW).setValue(newDate);
                            }
                        }

                        // If section memorized, merge all chunks of section
                        if (sectionMemorized && chunks.size() > 1) {
                            for(Chunk c: chunks) {
                                // Delete chunks
                                chunkReference.child(c.getChunkID()).setValue(null);
                            }

                            // Add new master chunk
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

        if (finalScore >= DBConstants.MASTERED_MIN_THRESHOLD_SCORE) {
            factor = 2;
        } else if (finalScore >= DBConstants.MAINTAIN_MIN_THRESHOLD_SCORE) {
            factor = 1;
        } else {
            factor = 1/2;
        }

        return factor;
    }

    @Override
    public void onBackPressed() {
        //Disabled Back Button
        Snackbar snackbar = Snackbar.make(findViewById(R.id.parentLayout), "Back Button is Disabled", Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("CANCEL REVIEW", new GoHomeListener());
        snackbar.show();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public class GoHomeListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(ResultActivity.this, HomeActivity.class));
        }
    }

}
