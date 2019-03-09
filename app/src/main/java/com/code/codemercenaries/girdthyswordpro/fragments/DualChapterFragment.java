package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.VerseRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.local.Verse;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DualChapterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DualChapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DualChapterFragment extends Fragment {

    private static final String TAG = "DualChapterFragment";

    private static final String ARG_PARAM1 = "version1";
    private static final String ARG_PARAM2 = "version2";
    private static final String ARG_PARAM3 = "bookName1";
    private static final String ARG_PARAM4 = "bookName2";
    private static final String ARG_PARAM5 = "chapNum";

    private String version1;
    private String version2;
    private String bookName1;
    private String bookName2;
    private int chapNum;

    private Activity mActivity;
    private TextView chapterTitle;
    private RecyclerView verseList;
    private VerseRecycleListAdapter verseRecycleListAdapter;
    private ProgressDialog progressDialog;
    private FloatingActionButton fab;
    private FloatingActionButton fabNot;
    private UpdatePrimaryVerseTexts task1;
    private UpdateSecondaryVerseTexts task2;
    private List<Verse> verses1;
    private List<Verse> verses2;
    private int numOfVerse;

    private DatabaseReference userBibleReference1;
    private DatabaseReference userBibleReference2;
    private FirebaseAuth mAuth;

    private OnFragmentInteractionListener mListener;

    public DualChapterFragment() {
        // Required empty public constructor
    }

    public static DualChapterFragment newInstance(String param1, String param2, String param3, String param4, int param5) {
        DualChapterFragment fragment = new DualChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putInt(ARG_PARAM5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            version1 = getArguments().getString(ARG_PARAM1);
            version2 = getArguments().getString(ARG_PARAM2);
            bookName1 = getArguments().getString(ARG_PARAM3);
            bookName2 = getArguments().getString(ARG_PARAM4);
            Log.d("DualChapterFragment:","bookName1:" + bookName1);
            Log.d("DualChapterFragment:","bookName2:" + bookName2);
            chapNum = getArguments().getInt(ARG_PARAM5);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dual_chapter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();

        chapterTitle = view.findViewById(R.id.chapterTitle);
        verseList = view.findViewById(R.id.verseList);
        fab = view.findViewById(R.id.fab);
        fabNot = view.findViewById(R.id.fabNot);

        DBHandler dbHandler = new DBHandler(mActivity);
        if(dbHandler.isReadChapter(version1,bookName1,chapNum)) {
            fab.setVisibility(View.INVISIBLE);
            fabNot.setVisibility(View.VISIBLE);
        } else {
            fab.setVisibility(View.VISIBLE);
            fabNot.setVisibility(View.INVISIBLE);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleFab();
                Snackbar.make(view, "Marked Chapter as Read", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                DBHandler dbHandler = new DBHandler(mActivity);
                dbHandler.setReadChapter(version1,bookName1,chapNum);
            }
        });

        fabNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
                Snackbar.make(v, "Marked Chapter as Unread", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                DBHandler dbHandler = new DBHandler(mActivity);
                dbHandler.setNotReadChapter(version1,bookName1,chapNum);
            }
        });

        verses1 = new ArrayList<>();
        verses2 = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        builder.append(bookName1);
        builder.append(" ");
        builder.append(Integer.toString(chapNum));
        chapterTitle.setText(builder.toString());

        numOfVerse = dbHandler.getNumOfVerse(version1,bookName1,chapNum);

        setupList();
    }

    private void toggleFab() {
        if(fab.getVisibility() == View.VISIBLE)
            fab.setVisibility(View.INVISIBLE);
        else
            fab.setVisibility(View.VISIBLE);

        if(fabNot.getVisibility() == View.VISIBLE)
            fabNot.setVisibility(View.INVISIBLE);
        else
            fabNot.setVisibility(View.VISIBLE);
    }

    private void setupList() {
        Log.d("setupList:","Entered");

        for (int i = 1; i <= numOfVerse; i++) {
            verses1.add(new Verse(version1, bookName1, chapNum, i));
            verses2.add(new Verse(version2, bookName2, chapNum, i));
        }

        verseRecycleListAdapter = new VerseRecycleListAdapter(mActivity, verses1, verses2);

        verseList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                verseList.getContext(), linearLayoutManager.getOrientation());
        verseList.addItemDecoration(dividerItemDecoration);
        verseList.setLayoutManager(linearLayoutManager);

        verseList.setLayoutManager(new LinearLayoutManager(mActivity));
        verseList.setHasFixedSize(true);
        verseList.setAdapter(verseRecycleListAdapter);

        task1 = new UpdatePrimaryVerseTexts();
        task1.execute();

        userBibleReference1 = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).child(version1).child(bookName1).
                child(String.format(Locale.getDefault(), "%d", chapNum));
        userBibleReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int verseNum = Integer.parseInt(snapshot.getKey());
                    Integer memoryCode = snapshot.child(DBConstants.FIREBASE_UB_KEY_MEMORY).getValue(int.class);
                    if (memoryCode != null) {
                        Verse verse = verses1.get(verseNum - 1);
                        verse.set_memory(memoryCode);
                        verses1.set(verseNum - 1, verse);
                    }
                }
                verseRecycleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });

        userBibleReference2 = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_USER_BIBLE).
                child(mAuth.getCurrentUser().getUid()).child(version2).child(bookName2).
                child(String.format(Locale.getDefault(), "%d", chapNum));
        userBibleReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    int verseNum = Integer.parseInt(snapshot.getKey());
                    Integer memoryCode = snapshot.child(DBConstants.FIREBASE_UB_KEY_MEMORY).getValue(int.class);
                    if (memoryCode != null) {
                        Verse verse = verses2.get(verseNum - 1);
                        verse.set_memory(memoryCode);
                        verses2.set(verseNum - 1, verse);
                    }
                }
                verseRecycleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.toString());
            }
        });

        Log.d("setupList:","Left");
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class UpdatePrimaryVerseTexts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("UpdatePrimaryVTexts:", "onPreExecute entered");
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Chapter");
            progressDialog.show();
            Log.d("UpdatePrimaryVTexts:", "onPreExecute left");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("UpdatePrimaryVTexts:", "doInBackground entered");
            Log.d("UpdatePrimaryVTexts:", version1 + " " + bookName1 + " " + chapNum);
            DBHandler dbHandler = new DBHandler(mActivity);

            ArrayList<String> versesText1 = dbHandler.getAllVersesOfChap(version1,bookName1,chapNum);
            for (int i = 0; i < verses1.size(); i++) {
                Verse verse = verses1.get(i);
                verse.set_verse_text(versesText1.get(i));
                verses1.set(i, verse);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("UpdatePrimaryVTexts:", "onPostExecute entered");
            task2 = new UpdateSecondaryVerseTexts();
            task2.execute();
            Log.d("UpdatePrimaryVTexts:", "onPostExecute left");
        }
    }

    public class UpdateSecondaryVerseTexts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("UpdateSecondaryVTexts:", "onPreExecute entered");

            Log.d("UpdateSecondaryVTexts:", "onPreExecute left");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("UpdateSecondaryVTexts:", "doInBackground entered");
            Log.d("UpdateSecondaryVTexts:", version2 + " " + bookName2 + " " + chapNum);

            DBHandler dbHandler = new DBHandler(mActivity);

            Log.d("UpdateSecondaryVTexts:", version2 + " " + bookName2 + " " + chapNum);
            ArrayList<String> versesText2 = dbHandler.getAllVersesOfChap(version2,bookName2,chapNum);
            for (int i = 0; i < verses2.size(); i++) {
                Verse verse = verses2.get(i);
                verse.set_verse_text(versesText2.get(i));
                verses2.set(i, verse);
            }
            Log.d("UpdateSecondaryVTexts:", "doInBackground left");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("UpdateSecondaryVTexts:", "onPostExecute entered");
            progressDialog.dismiss();
            verseRecycleListAdapter.notifyDataSetChanged();
            Log.d("UpdateSecondaryVTexts:", "onPostExecute left");
        }
    }
}
