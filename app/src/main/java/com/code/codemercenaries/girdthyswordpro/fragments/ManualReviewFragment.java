package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.activities.ResultActivity;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ManualReviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ManualReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManualReviewFragment extends Fragment {

    private static final String TAG = "ManualReviewFragment";
    private static final int HARD_CODE = 0;
    private static final int EASY_CODE = 1;
    private static final int MASTERED_CODE = 2;
    private static final int MANUAL_HARD_SCORE = 40;
    private static final int MANUAL_EASY_SCORE = 75;
    private static final int MANUAL_MASTERED_SCORE = 100;
    private static final String ARG_PARAM1 = "param1";
    Activity mActivity;
    FirebaseAuth mAuth;
    DatabaseReference chunkReference;
    TextView versesCompleted;
    TextView chunkVerseTitle;
    TextView chunkVerseText;
    TextView responseMessage;
    Button hard;
    Button easy;
    Button mastered;
    Button continueButton;
    int currentVersePos = 0;
    int totalScore = 0;
    int finalScore = 0;
    Chunk chunk;
    ArrayList<String> versesOfChunk;
    private String chunkID;

    private OnFragmentInteractionListener mListener;

    public ManualReviewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ManualReviewFragment newInstance(String chunkID) {
        ManualReviewFragment fragment = new ManualReviewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, chunkID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chunkID = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manual_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        versesCompleted = view.findViewById(R.id.verses_completed);
        chunkVerseTitle = view.findViewById(R.id.chunk_verse_title);
        chunkVerseText = view.findViewById(R.id.chunk_verse_text);
        responseMessage = view.findViewById(R.id.responseMessage);
        hard = view.findViewById(R.id.hard);
        easy = view.findViewById(R.id.easy);
        mastered = view.findViewById(R.id.mastered);
        continueButton = view.findViewById(R.id.continue_button);

        mActivity = getActivity();
        mAuth = FirebaseAuth.getInstance();

        versesOfChunk = new ArrayList<>();

        Log.d(TAG, chunkID);

        StringBuilder builder = new StringBuilder();
        builder.append(0);
        builder.append("/");
        builder.append(0);
        versesCompleted.setText(builder.toString());

        chunkReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_CHUNKS).child(mAuth.getCurrentUser().getUid()).child(chunkID);
        chunkReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chunk = dataSnapshot.getValue(Chunk.class);
                versesOfChunk.clear();
                if(chunk != null) {
                    DBHandler dbHandler = new DBHandler(mActivity);
                    for(int i=chunk.getStartVerseNum();i<=chunk.getEndVerseNum();i++) {
                        versesOfChunk.add(dbHandler.getVerse(chunk.getVersionID(),chunk.getBookName(),chunk.getChapterNum(),i));
                    }
                    StringBuilder builder = new StringBuilder();
                    builder.append(0);
                    builder.append("/");
                    builder.append(versesOfChunk.size());
                    versesCompleted.setText(builder.toString());
                    review();
                } else {
                    Log.d(TAG, "Something is wrong with the chunk");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        showResponseButtons();
        hideContinueButtonAndResponseMessage();

    }

    private void review(){
        if(currentVersePos < versesOfChunk.size()) {
            StringBuilder builder = new StringBuilder();
            builder.append(chunk.getBookName());
            builder.append(" ");
            builder.append(chunk.getChapterNum());
            builder.append(":");
            builder.append(chunk.getStartVerseNum() + currentVersePos);
            chunkVerseTitle.setText(builder.toString());


            DBHandler dbHandler = new DBHandler(mActivity);
            chunkVerseText.setText(dbHandler.getVerse(chunk.getVersionID(),chunk.getBookName(),chunk.getChapterNum(),chunk.getStartVerseNum()+currentVersePos));

            hard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentVersePos++;
                    StringBuilder builder = new StringBuilder();
                    builder.append(currentVersePos);
                    builder.append("/");
                    builder.append(versesOfChunk.size());
                    versesCompleted.setText(builder.toString());

                    calculate(HARD_CODE);

                    hideResponseButtons();
                    showContinueButtonAndResponseMessage();
                }
            });

            easy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentVersePos++;
                    StringBuilder builder = new StringBuilder();
                    builder.append(currentVersePos);
                    builder.append("/");
                    builder.append(versesOfChunk.size());
                    versesCompleted.setText(builder.toString());

                    calculate(EASY_CODE);

                    hideResponseButtons();
                    showContinueButtonAndResponseMessage();
                }
            });

            mastered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentVersePos++;
                    StringBuilder builder = new StringBuilder();
                    builder.append(currentVersePos);
                    builder.append("/");
                    builder.append(versesOfChunk.size());
                    versesCompleted.setText(builder.toString());

                    calculate(MASTERED_CODE);

                    hideResponseButtons();
                    showContinueButtonAndResponseMessage();
                }
            });

            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideContinueButtonAndResponseMessage();
                    showResponseButtons();
                    review();
                }
            });

        } else {
            Intent intent = new Intent(mActivity, ResultActivity.class);
            finalScore = totalScore/versesOfChunk.size();
            intent.putExtra(DBConstants.REVIEW_FINAL_SCORE, finalScore);
            intent.putExtra(DBConstants.REVIEW_CHUNK_ID, chunkID);
            startActivity(intent);
        }
    }

    private void calculate(int code) {
        switch(code) {
            case HARD_CODE:
                totalScore += MANUAL_HARD_SCORE;
                responseMessage.setText("That's Alright! Try Again Next Time");
                break;
            case EASY_CODE:
                totalScore += MANUAL_EASY_SCORE;
                responseMessage.setText("Good Job Soldier!");
                break;
            case MASTERED_CODE:
                totalScore += MANUAL_MASTERED_SCORE;
                responseMessage.setText("Way to Go Soldier!!");
                break;
            default:
                break;
        }
    }

    private void hideResponseButtons() {
        hard.setVisibility(View.GONE);
        easy.setVisibility(View.GONE);
        mastered.setVisibility(View.GONE);
    }

    private void showResponseButtons() {
        hard.setVisibility(View.VISIBLE);
        easy.setVisibility(View.VISIBLE);
        mastered.setVisibility(View.VISIBLE);
    }

    private void hideContinueButtonAndResponseMessage() {
        continueButton.setVisibility(View.GONE);
        responseMessage.setVisibility(View.GONE);
    }

    private void showContinueButtonAndResponseMessage() {
        continueButton.setVisibility(View.VISIBLE);
        responseMessage.setVisibility(View.VISIBLE);
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
