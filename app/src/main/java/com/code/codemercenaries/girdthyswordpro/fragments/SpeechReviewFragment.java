package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.activities.ResultActivity;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;
import com.code.codemercenaries.girdthyswordpro.utilities.Algorithms;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class SpeechReviewFragment extends Fragment {

    private static final String TAG = "SpeechReviewFragment";
    private static final int REQ_CODE_CONTINUE = 200;
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static final int SPEECH_HARD_SCORE = 40;
    private static final int SPEECH_EASY_SCORE = 75;
    private static final int SPEECH_MASTERED_SCORE = 100;

    private static final String ARG_PARAM1 = "param1";
    TextView versesCompleted;
    FloatingActionButton record;
    Button continueButton;
    TextView accuracy;
    TextView responseMessage;
    TextView pressAndSpeak;
    TextView yourInput;
    TextView yourInputText;
    TextView chunkVerseText;
    TextView chunkVerseTitle;
    Activity mActivity;
    FirebaseAuth mAuth;
    DatabaseReference chunkReference;
    ArrayList<String> versesOfChunk;
    Chunk chunk;
    int currentVersePos = 0;
    int totalScore = 0;
    int finalScore = 0;
    private String chunkID;
    private OnFragmentInteractionListener mListener;

    public SpeechReviewFragment() {
        // Required empty public constructor
    }

    public static SpeechReviewFragment newInstance(String chunkID) {
        SpeechReviewFragment fragment = new SpeechReviewFragment();
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
        return inflater.inflate(R.layout.fragment_speech_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        continueButton = view.findViewById(R.id.continue_button);
        responseMessage = view.findViewById(R.id.responseMessage);
        record = view.findViewById(R.id.record);
        pressAndSpeak = view.findViewById(R.id.pressAndSpeak);
        versesCompleted = view.findViewById(R.id.verses_completed);
        yourInput = view.findViewById(R.id.yourInput);
        yourInputText = view.findViewById(R.id.your_input_text);
        chunkVerseTitle = view.findViewById(R.id.chunk_verse_title);
        chunkVerseText = view.findViewById(R.id.chunk_verse_text);
        accuracy = view.findViewById(R.id.accuracy);

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
                if (chunk != null) {
                    DBHandler dbHandler = new DBHandler(mActivity);
                    for (int i = chunk.getStartVerseNum(); i <= chunk.getEndVerseNum(); i++) {
                        versesOfChunk.add(dbHandler.getVerse(chunk.getVersionID(), chunk.getBookName(), chunk.getChapterNum(), i));
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

    private void review() {
        if (currentVersePos < versesOfChunk.size()) {
            StringBuilder builder = new StringBuilder();
            builder.append(chunk.getBookName());
            builder.append(" ");
            builder.append(chunk.getChapterNum());
            builder.append(":");
            builder.append(chunk.getStartVerseNum() + currentVersePos);
            chunkVerseTitle.setText(builder.toString().toUpperCase());

            StringBuilder builder1 = new StringBuilder();
            builder1.append(currentVersePos);
            builder1.append("/");
            builder1.append(versesOfChunk.size());
            versesCompleted.setText(builder1.toString());

            DBHandler dbHandler = new DBHandler(mActivity);
            chunkVerseText.setText(dbHandler.getVerse(chunk.getVersionID(), chunk.getBookName(), chunk.getChapterNum(), chunk.getStartVerseNum() + currentVersePos));

            record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    chunkVerseText.setVisibility(View.INVISIBLE);
                    promptSpeechInput();
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
            finalScore = totalScore / versesOfChunk.size();
            intent.putExtra(DBConstants.REVIEW_FINAL_SCORE, finalScore);
            intent.putExtra(DBConstants.REVIEW_CHUNK_ID, chunkID);
            startActivity(intent);
        }
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        if (chunk.getVersionID().equals(DBConstants.TABLE_EN_KJV)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en");
        } else if (chunk.getVersionID().equals(DBConstants.TABLE_TAM_ORG)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ta");
        } else if (chunk.getVersionID().equals(DBConstants.TABLE_TELUGU)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "te");
        } else if (chunk.getVersionID().equals(DBConstants.TABLE_ORIYA)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "or");
        } else if (chunk.getVersionID().equals(DBConstants.TABLE_GREEK)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "el");
        } else if (chunk.getVersionID().equals(DBConstants.TABLE_SPANISH)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es");
        } else if (chunk.getVersionID().equals(DBConstants.TABLE_FRENCH)) {
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr");
        }
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            hideResponseButtons();
        } catch (ActivityNotFoundException a) {
            Toast.makeText(mActivity,
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void calculate() {
        Algorithms algorithms = new Algorithms();

        int length = chunkVerseText.getText().length();
        int levDistance = algorithms.unlimitedCompare(chunkVerseText.getText().toString().toLowerCase().replaceAll(",", ""), yourInputText.getText());

        final float matchPercentage = 100 - ((float) levDistance / (float) length) * 100;
        if (matchPercentage >= SPEECH_MASTERED_SCORE) {
            responseMessage.setText(R.string.mastered_response_message);
        } else if (matchPercentage >= SPEECH_EASY_SCORE && matchPercentage < SPEECH_MASTERED_SCORE) {
            responseMessage.setText(R.string.easy_response_message);
        } else {
            responseMessage.setText(R.string.hard_response_message);
        }
        accuracy.setText(String.format(getString(R.string.accuracy_w_percentage), matchPercentage));
        showContinueButtonAndResponseMessage();
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalScore += matchPercentage;
                currentVersePos++;
                review();
            }
        });
    }

    private void hideResponseButtons() {
        record.setVisibility(View.GONE);
        pressAndSpeak.setVisibility(View.GONE);
    }

    private void showResponseButtons() {
        record.setVisibility(View.VISIBLE);
        pressAndSpeak.setVisibility(View.VISIBLE);
    }

    private void hideContinueButtonAndResponseMessage() {
        continueButton.setVisibility(View.GONE);
        responseMessage.setVisibility(View.GONE);
        accuracy.setVisibility(View.GONE);
    }

    private void showContinueButtonAndResponseMessage() {
        continueButton.setVisibility(View.VISIBLE);
        responseMessage.setVisibility(View.VISIBLE);
        accuracy.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    yourInputText.setText(result.get(0).toLowerCase());
                    calculate();
                    chunkVerseText.setVisibility(View.VISIBLE);
                }
                break;
            case REQ_CODE_CONTINUE:
                break;
        }
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
}
