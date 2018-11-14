package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.activities.BibleActivity;
import com.code.codemercenaries.girdthyswordpro.beans.local.Version;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReaderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReaderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReaderFragment extends Fragment {

    Spinner primaryVersion;
    Spinner secondaryVersion;
    Button read;

    Activity mActivity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ReaderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReaderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReaderFragment newInstance(String param1, String param2) {
        ReaderFragment fragment = new ReaderFragment();
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
        return inflater.inflate(R.layout.fragment_reader, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();

        primaryVersion = view.findViewById(R.id.primary_version);
        secondaryVersion = view.findViewById(R.id.secondary_version);
        read = view.findViewById(R.id.read);

        DBHandler dbHandler = new DBHandler(mActivity);

        final List<Version> versions = dbHandler.getAllVersions();
        List<String> versionsList = new ArrayList<>();
        for(Version version: versions) {
            versionsList.add(version.get_name() + " (" +  version.get_lang() + ")");
        }

        ArrayAdapter<String> primaryAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,versionsList);
        primaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        primaryVersion.setAdapter(primaryAdapter);


        List<String> secondaryVersionList = new ArrayList<>(versionsList);
        secondaryVersionList.add(0,"None");

        ArrayAdapter<String> secondaryAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,secondaryVersionList);
        primaryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        secondaryVersion.setAdapter(secondaryAdapter);

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, BibleActivity.class);
                intent.putExtra("primary",primaryVersion.getSelectedItemPosition());
                intent.putExtra("secondary",secondaryVersion.getSelectedItemPosition()-1);
                startActivity(intent);
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
