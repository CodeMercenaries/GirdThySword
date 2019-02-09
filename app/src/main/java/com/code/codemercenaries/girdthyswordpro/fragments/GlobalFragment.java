package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.GlobalChatRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.remote.GlobalChatMessage;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

public class GlobalFragment extends Fragment {

    private static final String TAG = "GlobalFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity mActivity;
    RecyclerView list;
    GlobalChatRecycleListAdapter adapter;
    FirebaseAuth mAuth;
    FloatingActionButton sendButton;
    EditText editText;
    DatabaseReference globalMessagesReference;
    ArrayList<GlobalChatMessage> chatMessages;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public GlobalFragment() {
        // Required empty public constructor
    }

    public static GlobalFragment newInstance(String param1, String param2) {
        GlobalFragment fragment = new GlobalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_global, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mActivity = getActivity();

        chatMessages = new ArrayList<>();

        adapter = new GlobalChatRecycleListAdapter(mActivity,chatMessages,mAuth.getCurrentUser().getUid());

        list = view.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setStackFromEnd(true);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);
        list.setAdapter(adapter);

        globalMessagesReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_GLOBAL_MESSAGES);

        globalMessagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    GlobalChatMessage globalChatMessage = snapshot.getValue(GlobalChatMessage.class);
                    if(globalChatMessage != null) {
                        chatMessages.add(globalChatMessage);
                        Log.d(TAG, "Message downloaded " + globalChatMessage.getMessageID());
                    }
                }
                adapter.notifyDataSetChanged();
                list.scrollToPosition(chatMessages.size()-1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        editText = view.findViewById(R.id.editText);

        sendButton = view.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedText = editText.getText().toString();
                if(!typedText.equals("")) {

                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);

                    String newMessageID = globalMessagesReference.push().getKey();
                    GlobalChatMessage globalChatMessage =
                            new GlobalChatMessage(newMessageID,typedText,mAuth.getUid(),timestamp.toString());

                    if(newMessageID != null)
                        globalMessagesReference.child(newMessageID).setValue(globalChatMessage);

                    editText.setText("");
                }
            }
        });
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
