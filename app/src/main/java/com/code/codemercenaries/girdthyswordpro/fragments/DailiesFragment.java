package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.ChunkRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DailiesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DailiesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DailiesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "DailiesFragment";
    Activity mActivity;
    FirebaseAuth mAuth;
    RecyclerView chunkList;
    ChunkRecycleListAdapter chunkRecycleListAdapter;
    Query chunksQuery;
    DatabaseReference sectionsReference;
    ArrayList<Chunk> chunksForToday;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DailiesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DailiesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DailiesFragment newInstance(String param1, String param2) {
        DailiesFragment fragment = new DailiesFragment();
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
        return inflater.inflate(R.layout.fragment_dailies, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();

        SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT, Locale.US);
        Date currDate = new Date();
        String currDateString = df.format(currDate);

        chunksForToday = new ArrayList<>();

        chunkRecycleListAdapter = new ChunkRecycleListAdapter(mActivity,chunksForToday);

        mAuth = FirebaseAuth.getInstance();

        chunksQuery = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_CHUNKS).child(mAuth.getCurrentUser().getUid()).orderByChild(DBConstants.FIREBASE_C_KEY_NEXT_DATE_OF_REVIEW).endAt(currDateString);
        sectionsReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_SECTIONS).child(mAuth.getCurrentUser().getUid());

        chunkList = view.findViewById(R.id.dailiesList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        chunkList.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(chunkList.getContext(),linearLayoutManager.getOrientation());
        chunkList.addItemDecoration(dividerItemDecoration);
        chunkList.setHasFixedSize(true);
        chunkList.setAdapter(chunkRecycleListAdapter);

        chunksQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chunksForToday.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Chunk chunk = snapshot.getValue(Chunk.class);
                    chunksForToday.add(chunk);
                    Log.d(TAG, "Downloaded chunk " + chunk.getChunkID());
                }
                chunkRecycleListAdapter.notifyDataSetChanged();
                Log.d(TAG,"chunksQuery done");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG,"chunksQuery cancelled");
            }
        });
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
