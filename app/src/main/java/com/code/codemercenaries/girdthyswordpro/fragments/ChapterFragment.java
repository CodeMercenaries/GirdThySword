package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChapterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChapterFragment extends Fragment{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "version";
    private static final String ARG_PARAM2 = "book_name";
    private static final String ARG_PARAM3 = "chap_num";

    Activity mActivity;
    RecyclerView verseList;
    VerseRecycleListAdapter verseRecycleListAdapter;
    TextView chapterTitle;
    FloatingActionButton fab;
    FloatingActionButton fabNot;
    List<Verse> verses;
    DisplayVerses task1;
    private String version;
    private String bookName;
    private int chapNum;
    private int numOfVerse;
    private OnFragmentInteractionListener mListener;

    public ChapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @param param3 Parameter 3.
     * @return A new instance of fragment ChapterFragment.
     */
    public static ChapterFragment newInstance(String param1, String param2, int param3) {
        ChapterFragment fragment = new ChapterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            version = getArguments().getString(ARG_PARAM1);
            bookName = getArguments().getString(ARG_PARAM2);
            chapNum = getArguments().getInt(ARG_PARAM3);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chapter, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();

        chapterTitle = view.findViewById(R.id.chapterTitle);
        verseList = view.findViewById(R.id.verseList);
        fab = view.findViewById(R.id.fab);
        fabNot = view.findViewById(R.id.fabNot);

        DBHandler dbHandler = new DBHandler(mActivity);
        if(dbHandler.isReadChapter(version,bookName,chapNum)) {
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
                dbHandler.setReadChapter(version,bookName,chapNum);
            }
        });

        fabNot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFab();
                Snackbar.make(v, "Marked Chapter as Unread", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                DBHandler dbHandler = new DBHandler(mActivity);
                dbHandler.setNotReadChapter(version,bookName,chapNum);
            }
        });


        verses = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        builder.append(bookName);
        builder.append(" ");
        builder.append(Integer.toString(chapNum));
        chapterTitle.setText(builder.toString());

        numOfVerse = dbHandler.getNumOfVerse(version,bookName,chapNum);

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

        verseRecycleListAdapter = new VerseRecycleListAdapter(verses);

        verseList.setLayoutManager(new LinearLayoutManager(mActivity));
        verseList.setHasFixedSize(true);
        verseList.setAdapter(verseRecycleListAdapter);

        task1 = new DisplayVerses();
        task1.execute();

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

    private class DisplayVerses extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DisplayVerses:","onPreExecute entered");
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Chapter");
            progressDialog.show();
            Log.d("DisplayVerses:","onPreExecute left");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("DisplayVerses:","doInBackground entered");
            Log.d("DisplayVerses:",version + " " + bookName + " " + chapNum);
            DBHandler dbHandler = new DBHandler(mActivity);
            ArrayList<String> versesText = dbHandler.getAllVersesOfChap(version,bookName,chapNum);
            for(int i=1;i<=numOfVerse;i++){
                verses.add(new Verse(version,bookName,chapNum,i,versesText.get(i-1)));
            }
            Log.d("DisplayVerses:","doInBackground left");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("DisplayVerses:","onPostExecute entered");

            verseRecycleListAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
            Log.d("DisplayVerses:","onPostExecute left");
        }
    }


}
