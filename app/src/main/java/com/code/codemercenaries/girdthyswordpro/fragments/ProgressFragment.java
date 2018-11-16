package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.BookRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.local.BookWithStats;
import com.code.codemercenaries.girdthyswordpro.beans.local.Version;
import com.code.codemercenaries.girdthyswordpro.persistence.DBHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProgressFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProgressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProgressFragment extends Fragment {

    Spinner selectVersion;
    RecyclerView bookList;
    BookRecycleListAdapter bookRecycleListAdapter;

    Activity mActivity;

    ArrayList<BookWithStats> bookWithStatsList;
    List<Version> versions;
    int currVersionPos;
    DisplayBooks task1;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProgressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProgressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProgressFragment newInstance(String param1, String param2) {
        ProgressFragment fragment = new ProgressFragment();
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
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();

        selectVersion = view.findViewById(R.id.selectVersion);
        bookList = view.findViewById(R.id.bookList);

        final DBHandler dbHandler = new DBHandler(mActivity);

        versions = dbHandler.getAllVersions();
        List<String> versionsList = new ArrayList<>();
        for(Version version: versions) {
            versionsList.add(version.get_name() + " (" +  version.get_lang() + ")");
        }

        ArrayAdapter<String> versionAdapter = new ArrayAdapter<String>(mActivity,android.R.layout.simple_spinner_item,versionsList);
        versionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectVersion.setAdapter(versionAdapter);

        bookWithStatsList = new ArrayList<>();

        bookRecycleListAdapter = new BookRecycleListAdapter(bookWithStatsList);

        bookList.setLayoutManager(new LinearLayoutManager(mActivity));
        bookList.setHasFixedSize(true);
        bookList.setAdapter(bookRecycleListAdapter);

        selectVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookWithStatsList.clear();
                currVersionPos = position;
                task1 = new DisplayBooks();
                task1.execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class DisplayBooks extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Loading Stats");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DBHandler dbHandler = new DBHandler(mActivity);
            List<String> bookNames = dbHandler.getBookNames(versions.get(currVersionPos).get_id());
            for(int i=0;i<bookNames.size();i++) {
                bookWithStatsList.add(new BookWithStats(bookNames.get(i),dbHandler.getNumOfChap(versions.get(currVersionPos).get_id(),bookNames.get(i))));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bookRecycleListAdapter.notifyDataSetChanged();
            progressDialog.dismiss();
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
