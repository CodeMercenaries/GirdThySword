package com.code.codemercenaries.girdthyswordpro.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.local.Version;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Section;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AddSectionActivity extends AppCompatActivity {

    private final String TAG = "AddSectionActivity";
    SharedPreferences systemPreferences;

    FontHelper fontHelper;
    FirebaseAuth mAuth;

    Spinner setVersion;
    Spinner setBookName;
    Spinner setChapNum;
    Spinner setStartVerse;
    Spinner setEndVerse;
    Button submit;

    int chunkSize;
    Integer currentVersesAdded;
    List<String> bookItems = new ArrayList<>();
    ArrayList<Version> allVersions;
    ArrayList<String> availVersions;
    ArrayList<String> availBookNames;
    ArrayList<Integer> availChapNumbers;
    ArrayList<Integer> availStartVerses;
    ArrayList<Integer> availEndVerses;

    Version selectedVersion;
    String selectedBookName;
    int selectedChapterNum;
    int selectedStartVerse;
    int selectedEndVerse;

    DatabaseReference userBibleBooks;
    DatabaseReference userBibleChapters;
    DatabaseReference userBibleVerses;
    DatabaseReference chunksReference;
    DatabaseReference sectionsReference;
    DatabaseReference usersReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_add_section);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        currentVersesAdded = 0;

        usersReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(mAuth.getCurrentUser().getUid());
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_ADDED).getValue(Integer.class) != null)
                    currentVersesAdded = dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_ADDED).getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        submit = findViewById(R.id.button);
        addItemsOnVersionSpinner();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    public void addItemsOnVersionSpinner() {
        DBHandler dbHandler = new DBHandler(this);
        availVersions = new ArrayList<>();

        allVersions = dbHandler.getAllVersions();

        for (int i = 0; i < allVersions.size(); i++) {
            availVersions.add(allVersions.get(i).get_name() + " (" + allVersions.get(i).get_lang() + ")");
        }
        setVersion = findViewById(R.id.setVersion);

        ArrayAdapter<String> StringAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, availVersions);
        StringAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setVersion.setAdapter(StringAdapter);
        setVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemsOnBookNameSpinner(allVersions.get(position).get_id());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        StringAdapter.notifyDataSetChanged();
    }

    public void addItemsOnBookNameSpinner(final String version) {
        setBookName = findViewById(R.id.setBookName);

        DBHandler dbHandler = new DBHandler(this);

        final List<String> allBookNames = dbHandler.getBookNames(version);
        availBookNames = new ArrayList<>();

        final ArrayAdapter<String> setBookNameAdapter =
                new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,availBookNames);
        setBookNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setBookName.setAdapter(setBookNameAdapter);

        setBookName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemsOnChapterNumSpinner(version, availBookNames.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userBibleBooks = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).
                child(version);

        userBibleBooks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "userBibleBooks onDataChange Entered");
                for(String bookName: allBookNames) {
                    Integer memoryStatus = null;
                    if(dataSnapshot.child(bookName).child("0").exists()) {
                        memoryStatus = dataSnapshot.child(bookName).child("0").getValue(Integer.class);
                    }
                    if(memoryStatus == null || memoryStatus == DBConstants.CODE_NOT_ADDED) {
                        availBookNames.add(bookName);
                    }
                }
                setBookNameAdapter.notifyDataSetChanged();
                Log.d(TAG, "userBibleBooks onDataChange Left");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }

    public void addItemsOnChapterNumSpinner(final String version, final String bookName) {
        setChapNum = findViewById(R.id.setChapNum);
        availChapNumbers = new ArrayList<Integer>();

        DBHandler dbHandler = new DBHandler(this);
        final int totalNumOfChapters = dbHandler.getNumOfChap(version, bookName);

        availChapNumbers = new ArrayList<>();

        final ArrayAdapter<Integer> setChapterNumAdapter =
                new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item, availChapNumbers);
        setChapterNumAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setChapNum.setAdapter(setChapterNumAdapter);
        setChapNum.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                addItemsOnStartVerseSpinner(version, bookName, availChapNumbers.get(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        userBibleChapters = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).
                child(version).
                child(bookName);

        userBibleChapters.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i=1;i<=totalNumOfChapters;i++) {
                    Integer memoryStatus = null;
                    if(dataSnapshot.child(Integer.toString(i)).child("0").exists()) {
                        memoryStatus = dataSnapshot.child(Integer.toString(i)).child("0").getValue(Integer.class);
                    }
                    if(memoryStatus == null || memoryStatus == DBConstants.CODE_NOT_ADDED) {
                        Log.d(TAG, i + " memoryStatus=" + memoryStatus);
                        availChapNumbers.add(i);
                    }
                }
                setChapterNumAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

    }

    public void addItemsOnStartVerseSpinner(final String version, final String bookName, final int chapNum) {
        setStartVerse = findViewById(R.id.setStartVerse);

        DBHandler dbHandler = new DBHandler(this);
        final int totalNumOfVerses = dbHandler.getNumOfVerse(version, bookName, chapNum);

        ArrayList<Integer> allVerses = new ArrayList<>();

        availStartVerses = new ArrayList<>();

        final ArrayAdapter<Integer> setStartVerseAdapter =
                new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,availStartVerses);
        setStartVerseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setStartVerse.setAdapter(setStartVerseAdapter);
        setStartVerse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                addItemsOnEndVerseSpinner(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView){
            }
        });

        userBibleVerses = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).
                child(version).
                child(bookName).
                child(Integer.toString(chapNum));

        userBibleVerses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(int i=1;i<=totalNumOfVerses;i++) {
                    Integer memoryStatus = null;
                    if(dataSnapshot.child(Integer.toString(i)).exists()) {
                        memoryStatus = dataSnapshot.child(Integer.toString(i)).child(DBConstants.FIREBASE_UB_KEY_MEMORY).getValue(Integer.class);
                    }
                    if(memoryStatus == null || memoryStatus == DBConstants.CODE_NOT_ADDED) {
                        availStartVerses.add(i);
                    }
                }
                setStartVerseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        if(availStartVerses.size() == 0){
            addItemsOnEndVerseSpinner(0);
        }

    }

    public void addItemsOnEndVerseSpinner(int startVersePos) {
        setEndVerse = findViewById(R.id.setEndVerse);

        availEndVerses = new ArrayList<>();

        for(int i=startVersePos;i<availStartVerses.size();i++){
            if(i==availStartVerses.size()-1){
                availEndVerses.add(availStartVerses.get(i));
            }
            else if(availStartVerses.get(i)+1 == availStartVerses.get(i+1)){
                availEndVerses.add(availStartVerses.get(i));
            }
            else{
                availEndVerses.add(availStartVerses.get(i));
                break;
            }
        }
        ArrayAdapter<Integer> IntegerAdapter =
                new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_item,availEndVerses);
        IntegerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setEndVerse.setAdapter(IntegerAdapter);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });
    }

    public void submit(){

        selectedVersion = allVersions.get(setVersion.getSelectedItemPosition());
        selectedBookName = setBookName.getSelectedItem().toString();
        selectedChapterNum = Integer.parseInt(setChapNum.getSelectedItem().toString());
        selectedStartVerse = Integer.parseInt(setStartVerse.getSelectedItem().toString());
        selectedEndVerse = Integer.parseInt(setEndVerse.getSelectedItem().toString());

        sectionsReference = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_SECTIONS).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String newSectionId = sectionsReference.push().getKey();
        Section section =
                new Section(newSectionId, selectedVersion.get_id(),
                        selectedBookName, selectedChapterNum, selectedStartVerse, selectedEndVerse);

        if(newSectionId != null){
            sectionsReference.child(newSectionId).setValue(section);
            Log.d(TAG, "Section added");
        } else {
            Log.d(TAG, "Section not added");
        }

        systemPreferences = getSharedPreferences(DBConstants.SETTINGS_PREF, 0);
        chunkSize = systemPreferences.getInt(DBConstants.SET_CHUNK_SIZE, 3);

        List<Chunk> chunkList = chunkize(section,chunkSize);
        chunksReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_CHUNKS).
                child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        for(int i=0;i<chunkList.size();i++){

            String newChunkId = chunksReference.push().getKey();

            Chunk newChunk = chunkList.get(i);
            newChunk.setChunkID(newChunkId);
            chunkList.set(i, newChunk);

            if(newChunkId != null) {
                chunksReference.child(newChunkId).setValue(chunkList.get(i));
                Log.d(TAG,chunkList.get(i).toString() + " added");
            } else {
                Log.d(TAG,chunkList.get(i).toString() + " not added");
            }
        }

        userBibleVerses = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).
                child(selectedVersion.get_id()).
                child(selectedBookName).
                child(Integer.toString(selectedChapterNum));

        //Set Verses to Added
        for(int i=section.getStartVerseNum();i<=section.getEndVerseNum();i++){
            userBibleVerses.child(Integer.toString(i)).child(DBConstants.FIREBASE_UB_KEY_MEMORY).
                    setValue(DBConstants.CODE_ADDED);
        }

        userBibleChapters = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).
                child(selectedVersion.get_id()).
                child(selectedBookName).
                child(Integer.toString(selectedChapterNum));

        userBibleBooks = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).
                child(selectedVersion.get_id()).
                child(selectedBookName);

        Log.d(TAG, "availStartVerses size " + availStartVerses.size());
        Log.d(TAG, "versesAdded " + Integer.toString(selectedEndVerse - selectedStartVerse + 1));
        Log.d(TAG, "availChapNumbers size " + availChapNumbers.size());

        //Set Chapter to Added
        if(availStartVerses.size() - (selectedEndVerse - selectedStartVerse + 1) == 0) {
            userBibleChapters.child("0").setValue(DBConstants.CODE_ADDED);

            if(availChapNumbers.size() == 1) {
                userBibleBooks.child("0").setValue(DBConstants.CODE_ADDED);
            }
        }

        usersReference.child(DBConstants.FIREBASE_U_KEY_VERSES_ADDED).setValue(currentVersesAdded + (selectedEndVerse - selectedStartVerse + 1));

        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        usersReference.child(DBConstants.FIREBASE_U_KEY_LAST_UPDATED_BY).setValue(timestamp.toString());

        Toast.makeText(AddSectionActivity.this, section.getBookName() + " "
                        + section.getChapterNum() + " "
                        + section.getStartVerseNum() + "-"
                        + section.getEndVerseNum() + " ("
                        + section.getVersionID() + ")" + " Added", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(AddSectionActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    public List<Chunk> chunkize(Section section,int chunkSize){
        List<Chunk> chunks = new ArrayList<Chunk>();
        int min = section.getStartVerseNum();
        int max = section.getEndVerseNum();
        int seq = 1;
        SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT, Locale.US);
        Calendar ca = Calendar.getInstance();
        String currDate = df.format(ca.getTime());

        while(max-min>=chunkSize) {
            Chunk subChunk = new Chunk();
            subChunk.setBookName(section
                    .getBookName());
            subChunk.setChapterNum(section.getChapterNum());
            subChunk.setSequenceNum(seq);
            subChunk.setSpace(1);
            subChunk.setStartVerseNum(min);
            subChunk.setEndVerseNum(min+chunkSize-1);
            subChunk.setSectionID(section.getSectionID());
            if(seq==1){
                subChunk.setNextDateOfReview(currDate);
            }
            else{
                subChunk.setNextDateOfReview("NA");
            }
            subChunk.setMastered(false);
            subChunk.setVersionID(section.getVersionID());
            chunks.add(subChunk);
            seq++;
            min = min+chunkSize;
        }
        if(max-min>=0){
            Chunk subChunk = new Chunk();
            subChunk.setBookName(section.getBookName());
            subChunk.setChapterNum(section.getChapterNum());
            subChunk.setSequenceNum(seq);
            subChunk.setSpace(1);
            subChunk.setStartVerseNum(min);
            subChunk.setEndVerseNum(max);
            subChunk.setSectionID(section.getSectionID());
            if(seq==1){
                subChunk.setNextDateOfReview(currDate);
            }
            else{
                subChunk.setNextDateOfReview("NA");
            }
            subChunk.setMastered(false);
            subChunk.setVersionID(section.getVersionID());
            chunks.add(subChunk);
            seq++;
            min = min+chunkSize;
        }
        return chunks;
    }
}
