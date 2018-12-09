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
import com.code.codemercenaries.girdthyswordpro.adapters.AdminChatRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.remote.AdminChatMessage;
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

public class AdminFragment extends Fragment {

    private static final String TAG = "AdminFragment";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity mActivity;
    RecyclerView list;
    AdminChatRecycleListAdapter adapter;
    FirebaseAuth mAuth;
    FloatingActionButton sendButton;
    EditText editText;
    DatabaseReference adminMessagesReference;
    ArrayList<AdminChatMessage> chatMessages;
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AdminFragment() {
        // Required empty public constructor
    }

    public static AdminFragment newInstance(String param1, String param2) {
        AdminFragment fragment = new AdminFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        mActivity = getActivity();

        chatMessages = new ArrayList<>();

        adapter = new AdminChatRecycleListAdapter(mActivity,chatMessages);

        list = view.findViewById(R.id.list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        list.setLayoutManager(linearLayoutManager);
        list.setHasFixedSize(true);
        list.setAdapter(adapter);

        adminMessagesReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_ADMIN_MESSAGES).child(mAuth.getCurrentUser().getUid());

        adminMessagesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessages.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    AdminChatMessage adminChatMessage = snapshot.getValue(AdminChatMessage.class);
                    if(adminChatMessage != null) {
                        chatMessages.add(adminChatMessage);
                        Log.d(TAG, "Message downloaded " + adminChatMessage.getMessageID());
                    }
                }
                adapter.notifyDataSetChanged();
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

                    String newMessageID = adminMessagesReference.push().getKey();
                    AdminChatMessage adminChatMessage =
                            new AdminChatMessage(newMessageID,false,typedText,timestamp.toString());

                    if(newMessageID != null)
                        adminMessagesReference.child(newMessageID).setValue(adminChatMessage);

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
