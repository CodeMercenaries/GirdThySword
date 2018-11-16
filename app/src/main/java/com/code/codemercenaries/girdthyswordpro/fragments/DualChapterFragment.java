package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
 * {@link DualChapterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DualChapterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DualChapterFragment extends Fragment {

    Activity mActivity;
    TextView chapterTitle;
    RecyclerView verseList;
    VerseRecycleListAdapter verseRecycleListAdapter;
    ProgressDialog progressDialog;

    DisplayPrimaryVerses task1;
    DisplaySecondaryVerses task2;

    List<Verse> verses1;
    List<Verse> verses2;

    int numOfVerse;

    private static final String ARG_PARAM1 = "version1";
    private static final String ARG_PARAM2 = "version2";
    private static final String ARG_PARAM3 = "bookName1";
    private static final String ARG_PARAM4 = "bookName2";
    private static final String ARG_PARAM5 = "chapNum";

    // TODO: Rename and change types of parameters
    private String version1;
    private String version2;
    private String bookName1;
    private String bookName2;
    private int chapNum;

    private OnFragmentInteractionListener mListener;

    public DualChapterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DualChapterFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        chapterTitle = view.findViewById(R.id.chapterTitle);
        verseList = view.findViewById(R.id.verseList);

        verses1 = new ArrayList<>();
        verses2 = new ArrayList<>();

        StringBuilder builder = new StringBuilder();
        builder.append(bookName1);
        builder.append(" ");
        builder.append(Integer.toString(chapNum));
        chapterTitle.setText(builder.toString());

        DBHandler dbHandler = new DBHandler(mActivity);
        numOfVerse = dbHandler.getNumOfVerse(version1,bookName1,chapNum);

        setupList();
    }

    private void setupList() {
        Log.d("setupList:","Entered");

        task1 = new DisplayPrimaryVerses();
        task1.execute();

        Log.d("setupList:","Left");
    }

    private class DisplayPrimaryVerses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DisplayPrimaryVerses:","onPreExecute entered");
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage("Loading Chapter");
            progressDialog.show();
            Log.d("DisplayPrimaryVerses:","onPreExecute left");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("DisplayPrimaryVerses:","doInBackground entered");
            Log.d("DisplayPrimaryVerses:",version1 + " " + bookName1 + " " + chapNum);
            DBHandler dbHandler = new DBHandler(mActivity);

            ArrayList<String> versesText1 = dbHandler.getAllVersesOfChap(version1,bookName1,chapNum);
            for(int i=1;i<=numOfVerse;i++){
                verses1.add(new Verse(version1,bookName1,chapNum,i,versesText1.get(i-1)));
                Log.d("DisplayPrimaryVerses:",verses1.get(i-1).get_verse_text());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("DisplayPrimaryVerses:","onPostExecute entered");
            task2 = new DisplaySecondaryVerses();
            task2.execute();
            Log.d("DisplayPrimaryVerses:","onPostExecute left");
        }
    }

    public class DisplaySecondaryVerses extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("DisplaySecondaryVerses:","onPreExecute entered");

            Log.d("DisplaySecondaryVerses:","onPreExecute left");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("DisplaySecondaryVerses:","doInBackground entered");
            Log.d("DisplaySecondaryVerses:",version2 + " " + bookName2 + " " + chapNum);

            DBHandler dbHandler = new DBHandler(mActivity);

            Log.d("DisplaySecondaryVerses:",version2 + " " + bookName2 + " " + chapNum);
            ArrayList<String> versesText2 = dbHandler.getAllVersesOfChap(version2,bookName2,chapNum);
            for(int i=1;i<=numOfVerse;i++){
                verses2.add(new Verse(version2,bookName2,chapNum,i,versesText2.get(i-1)));
                Log.d("DisplaySecondaryVerses:",verses2.get(i-1).get_verse_text());
            }
            Log.d("DisplaySecondaryVerses:","doInBackground left");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("DisplaySecondaryVerses:","onPostExecute entered");
            progressDialog.dismiss();

            verseRecycleListAdapter = new VerseRecycleListAdapter(verses1,verses2);

            verseList.setLayoutManager(new LinearLayoutManager(mActivity));
            verseList.setHasFixedSize(true);
            verseList.setAdapter(verseRecycleListAdapter);

            Log.d("DisplaySecondaryVerses:","onPostExecute left");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
