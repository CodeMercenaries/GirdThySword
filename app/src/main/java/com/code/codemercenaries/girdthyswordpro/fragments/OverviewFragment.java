package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.adapters.OverviewRecycleListAdapter;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OverviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OverviewFragment extends Fragment {

    private static final String TAG = "OverviewFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity mActivity;
    Query chunksQuery;
    FirebaseAuth mAuth;
    RecyclerView list;
    CompactCalendarView compactCalendarView;
    RadioGroup radioGroup;
    TextView date;
    OverviewRecycleListAdapter overviewRecycleListAdapter;
    NestedScrollView nestedScrollView;
    ArrayList<Chunk> chunks;
    ArrayList<Chunk> allChunks;
    ArrayList<Chunk> chunksOfSelectedDate;
    ArrayList<String> months;
    Date selectedDate;
    SimpleDateFormat df;
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public OverviewFragment() {
        // Required empty public constructor
    }

    public static OverviewFragment newInstance(String param1, String param2) {
        OverviewFragment fragment = new OverviewFragment();
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
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        mActivity = getActivity();

        chunks = new ArrayList<>();
        allChunks = new ArrayList<>();
        chunksOfSelectedDate = new ArrayList<>();
        df = new SimpleDateFormat(DBConstants.DATE_FORMAT, Locale.US);

        months = new ArrayList<>(Arrays.asList(getString(R.string.january),
                getString(R.string.february),
                getString(R.string.march),
                getString(R.string.april),
                getString(R.string.may),
                getString(R.string.june),
                getString(R.string.july),
                getString(R.string.august),
                getString(R.string.september),
                getString(R.string.october),
                getString(R.string.november),
                getString(R.string.december)));

        compactCalendarView = view.findViewById(R.id.calendar);
        date = view.findViewById(R.id.date);
        radioGroup = view.findViewById(R.id.radioGroup);
        list = view.findViewById(R.id.list);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        final Date currDate = new Date();

        list.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(list.getContext(),linearLayoutManager.getOrientation());
        list.addItemDecoration(dividerItemDecoration);
        list.setLayoutManager(linearLayoutManager);
        overviewRecycleListAdapter = new OverviewRecycleListAdapter(mActivity,chunks);
        list.setNestedScrollingEnabled(false);
        list.setAdapter(overviewRecycleListAdapter);

        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                date.setText(getString(R.string.date,dateClicked.getDate(),months.get(dateClicked.getMonth()),dateClicked.getYear()+1900));
                chunksOfSelectedDate.clear();
                for(Chunk chunk: allChunks) {
                    if(chunk.getNextDateOfReview().equals(df.format(dateClicked))) {
                        chunksOfSelectedDate.add(chunk);
                    }
                }
                radioGroup.check(R.id.showAll);
                radioGroup.check(R.id.showOfDate);
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                date.setText(getString(R.string.date, firstDayOfNewMonth.getDate(),
                        months.get(firstDayOfNewMonth.getMonth()), firstDayOfNewMonth.getYear() + 1900));
                chunksOfSelectedDate.clear();
                for(Chunk chunk: allChunks) {
                    if(chunk.getNextDateOfReview().equals(df.format(firstDayOfNewMonth))) {
                        chunksOfSelectedDate.add(chunk);
                    }
                }
                radioGroup.check(R.id.showAll);
                radioGroup.check(R.id.showOfDate);
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.showOfDate:
                        chunks.clear();
                        chunks.addAll(chunksOfSelectedDate);
                        overviewRecycleListAdapter.notifyDataSetChanged();
                        break;
                    case R.id.showAll:
                        chunks.clear();
                        chunks.addAll(allChunks);
                        overviewRecycleListAdapter.notifyDataSetChanged();
                        break;
                }
            }
        });

        chunksQuery = FirebaseDatabase.getInstance().
                getReference(DBConstants.FIREBASE_TABLE_CHUNKS).child(mAuth.getCurrentUser().getUid()).
                orderByChild(DBConstants.FIREBASE_C_KEY_NEXT_DATE_OF_REVIEW);

        chunksQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allChunks.clear();
                chunks.clear();
                compactCalendarView.removeAllEvents();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Chunk chunk = snapshot.getValue(Chunk.class);
                    allChunks.add(chunk);

                    try {
                        if(chunk != null) {
                            compactCalendarView.addEvent(
                                    new Event(Color.BLUE, df.parse(chunk.getNextDateOfReview()).getTime()));
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if(chunk != null && chunk.getNextDateOfReview().equals(df.format(currDate))) {
                        chunks.add(chunk);
                    }
                }
                overviewRecycleListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

        compactCalendarView.setCurrentDate(currDate);
        date.setText(getString(R.string.date, currDate.getDate(),
                months.get(currDate.getMonth()), currDate.getYear() + 1900));
        radioGroup.check(R.id.showOfDate);

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