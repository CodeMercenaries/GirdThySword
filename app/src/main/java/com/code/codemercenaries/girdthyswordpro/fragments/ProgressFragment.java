package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Spinner selectVersion;
    RecyclerView bookList;
    BookRecycleListAdapter bookRecycleListAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    Activity mActivity;
    ArrayList<BookWithStats> bookWithStatsList;
    List<Version> versions;
    int currVersionPos;
    DisplayBooks task1;
    FloatingActionButton restore;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ProgressFragment() {
        // Required empty public constructor
    }

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
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        restore = view.findViewById(R.id.restore);

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

        bookRecycleListAdapter = new BookRecycleListAdapter(mActivity,bookWithStatsList);

        bookList.setLayoutManager(new LinearLayoutManager(mActivity));
        bookList.setHasFixedSize(true);
        bookList.setAdapter(bookRecycleListAdapter);

        selectVersion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                bookWithStatsList.clear();
                currVersionPos = position;
                restore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Dialog_Alert);
                        builder.setTitle("Restore Read Progress")
                                .setMessage(String.format(
                                        "Are you sure you want to restore progress in (%s)",
                                        versions.get(currVersionPos).get_name()))
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dbHandler.setNotReadVersion(versions.get(currVersionPos).get_id());
                                        Toast.makeText(
                                                mActivity,
                                                String.format(
                                                        "Progress Restored (%s)",
                                                        versions.get(currVersionPos).get_name()),
                                                Toast.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Do Nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });

                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);

                        task1 = new DisplayBooks();
                        task1.execute();

                        bookRecycleListAdapter.notifyDataSetChanged();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                bookWithStatsList.clear();
                bookRecycleListAdapter.notifyDataSetChanged();
                task1 = new DisplayBooks();
                task1.execute();
                bookRecycleListAdapter.notifyDataSetChanged();
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class DisplayBooks extends AsyncTask<Void, Void, Void> {

        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*progressDialog = new ProgressDialog(mActivity);
            progressDialog.setMessage("Loading Stats");
            progressDialog.show();*/
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            DBHandler dbHandler = new DBHandler(mActivity);
            List<String> bookNames = dbHandler.getBookNames(versions.get(currVersionPos).get_id());
            bookWithStatsList.clear();
            for(int i=0;i<bookNames.size();i++) {
                int totalChapters = dbHandler.getNumOfChap(
                        versions.get(currVersionPos).get_id(), bookNames.get(i));
                int readChapters = dbHandler.getTotalChaptersReadInBook(
                        versions.get(currVersionPos).get_id(), bookNames.get(i));
                BookWithStats bookWithStats = new BookWithStats(
                        bookNames.get(i), readChapters * 100 / totalChapters, readChapters, totalChapters);
                bookWithStatsList.add(bookWithStats);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            bookRecycleListAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
            /*progressDialog.dismiss();*/
        }
    }
}
