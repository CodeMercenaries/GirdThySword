package com.code.codemercenaries.girdthyswordpro.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.SectionListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Section;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.utilities.FontHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class RemoveSectionActivity extends AppCompatActivity {

    private final String TAG = "RemoveSectionActivity";
    FontHelper fontHelper;

    FirebaseAuth mAuth;
    Query sectionsReference;
    DatabaseReference sectionsChange;
    DatabaseReference chunksChange;
    DatabaseReference userBible;
    DatabaseReference usersReference;

    ListView sectionList;
    SectionListAdapter sectionListAdapter;

    ArrayList<Section> sections;
    ArrayList<Chunk> chunks;
    Integer currAddedVerses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fontHelper = new FontHelper();
        fontHelper.initialize(this);

        setContentView(R.layout.activity_remove_section);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        chunks = new ArrayList<>();
        sections = new ArrayList<>();
        currAddedVerses = 0;

        sectionListAdapter = new SectionListAdapter(this,R.layout.custom_section_list,sections);
        sectionList = findViewById(R.id.sectionList);
        sectionList.setAdapter(sectionListAdapter);

        sectionsChange = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_SECTIONS).
                child(mAuth.getCurrentUser().getUid());
        chunksChange = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_CHUNKS).
                child(mAuth.getCurrentUser().getUid());
        userBible = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid());
        usersReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).
                child(mAuth.getCurrentUser().getUid()).child(DBConstants.FIREBASE_U_KEY_VERSES_ADDED);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currAddedVerses = dataSnapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        chunksChange.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chunks.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Chunk chunk = snapshot.getValue(Chunk.class);
                    chunks.add(chunk);
                    Log.d(TAG, "Downloaded chunk " + chunk.getChunkID());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        sectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(sections.get(position).getBookName());
                stringBuilder.append(" ");
                stringBuilder.append(sections.get(position).getChapterNum());
                stringBuilder.append(" ");
                stringBuilder.append(sections.get(position).getStartVerseNum());
                stringBuilder.append("-");
                stringBuilder.append(sections.get(position).getEndVerseNum());
                stringBuilder.append(" (");
                stringBuilder.append(sections.get(position).getVersionID().toUpperCase());
                stringBuilder.append(")");
                stringBuilder.append("\n\n");
                stringBuilder.append("Are you sure you want to delete this section? All your progress for this section will be lost!");
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(RemoveSectionActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("Delete Section")
                        .setMessage(stringBuilder.toString())
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sectionsChange.child(sections.get(position).getSectionID()).setValue(null);
                                userBible.child(sections.get(position).getVersionID()).
                                        child(sections.get(position).getBookName()).
                                        child("0").setValue(DBConstants.CODE_NOT_ADDED);
                                userBible.child(sections.get(position).getVersionID()).
                                        child(sections.get(position).getBookName()).
                                        child(String.format(Locale.getDefault(),"%d",sections.get(position).getChapterNum())).
                                        child("0").setValue(DBConstants.CODE_NOT_ADDED);
                                for(int i=sections.get(position).getStartVerseNum(); i<=sections.get(position).getEndVerseNum(); i++) {
                                    userBible.child(sections.get(position).getVersionID()).
                                            child(sections.get(position).getBookName()).
                                            child(String.format(Locale.getDefault(),"%d",sections.get(position).getChapterNum())).
                                            child(String.format(Locale.getDefault(),"%d",i)).
                                            child(DBConstants.FIREBASE_UB_KEY_MEMORY).setValue(DBConstants.CODE_NOT_ADDED);
                                }
                                for(Chunk chunk: chunks) {
                                    if(chunk.getSectionID().equals(sections.get(position).getSectionID())) {
                                        chunksChange.child(chunk.getChunkID()).setValue(null);
                                    }
                                }
                                usersReference.setValue(currAddedVerses - (sections.get(position).getEndVerseNum() - sections.get(position).getStartVerseNum() + 1));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do Nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        sectionsReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_SECTIONS).child(mAuth.getCurrentUser().getUid()).orderByChild(DBConstants.FIREBASE_S_KEY_VERSION_ID);
        sectionsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sections.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Section section = snapshot.getValue(Section.class);
                    sections.add(section);
                    Log.d(TAG, "Downloaded section " + section.getSectionID());
                }

                sectionListAdapter.notifyDataSetChanged();
                Log.d(TAG,"sectionsReference done");
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
