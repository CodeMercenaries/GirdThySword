package com.code.codemercenaries.girdthyswordpro.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Sword;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForgeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ForgeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ForgeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Activity mActivity;
    FirebaseAuth mAuth;
    DatabaseReference usersReference;
    CardView sword1Card;
    CardView sword2Card;
    CardView sword3Card;
    View rootView;
    ImageView equippedSword;
    TextView equippedSwordTitle;
    ImageView sword1;
    ImageView sword2;
    ImageView sword3;
    ImageView sword4;
    ImageView sword5;
    ImageView sword6;
    TextView sword1Status;
    LinearLayout sword1CardFooter;
    TextView sword2Status;
    LinearLayout sword2CardFooter;
    TextView sword3Status;
    LinearLayout sword3CardFooter;
    TextView sword4Status;
    LinearLayout sword4CardFooter;
    TextView sword5Status;
    LinearLayout sword5CardFooter;
    TextView sword6Status;
    LinearLayout sword6CardFooter;
    TextView versesMemorizedTextView;
    TextView sword1VerseCost;
    TextView sword2VerseCost;
    TextView sword3VerseCost;
    TextView sword4VerseCost;
    TextView sword5VerseCost;
    TextView sword6VerseCost;
    ArrayList<Sword> swords;
    Long versesMemorized;
    private String TAG = "ForgeFragment";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ForgeFragment() {
        // Required empty public constructor
    }

    public static ForgeFragment newInstance(String param1, String param2) {
        ForgeFragment fragment = new ForgeFragment();
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
        return inflater.inflate(R.layout.fragment_forge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActivity = getActivity();
        rootView = view;

        mAuth = FirebaseAuth.getInstance();

        equippedSword = view.findViewById(R.id.equippedSword);
        equippedSwordTitle =  view.findViewById(R.id.equippedSwordTitle);
        sword1 = view.findViewById(R.id.sword1);
        sword2 = view.findViewById(R.id.sword2);
        sword3 = view.findViewById(R.id.sword3);
        sword4 = view.findViewById(R.id.sword4);
        sword5 = view.findViewById(R.id.sword5);
        sword6 = view.findViewById(R.id.sword6);
        versesMemorizedTextView = view.findViewById(R.id.memorizedVerses);

        sword1Status = view.findViewById(R.id.sword1Status);
        sword1CardFooter = view.findViewById(R.id.sword1CardFooter);
        sword2Status = view.findViewById(R.id.sword2Status);
        sword2CardFooter = view.findViewById(R.id.sword2CardFooter);
        sword3Status = view.findViewById(R.id.sword3Status);
        sword3CardFooter = view.findViewById(R.id.sword3CardFooter);
        sword4Status = view.findViewById(R.id.sword4Status);
        sword4CardFooter = view.findViewById(R.id.sword4CardFooter);
        sword5Status = view.findViewById(R.id.sword5Status);
        sword5CardFooter = view.findViewById(R.id.sword5CardFooter);
        sword6Status = view.findViewById(R.id.sword6Status);
        sword6CardFooter = view.findViewById(R.id.sword6CardFooter);

        sword1VerseCost = view.findViewById(R.id.sword1VerseCost);
        sword2VerseCost = view.findViewById(R.id.sword2VerseCost);
        sword3VerseCost = view.findViewById(R.id.sword3VerseCost);
        sword4VerseCost = view.findViewById(R.id.sword4VerseCost);
        sword5VerseCost = view.findViewById(R.id.sword5VerseCost);
        sword6VerseCost = view.findViewById(R.id.sword6VerseCost);

        swords = new ArrayList<>();
        swords.add(new Sword("bronze_sword","Bronze Sword",0,"images/swords/bronze_sword.png",R.id.sword1Status,R.id.sword1CardFooter,R.id.sword1Card, true));
        swords.add(new Sword("soldier_sword","Soldier Sword",20,"images/swords/soldier_sword.png",R.id.sword2Status,R.id.sword2CardFooter, R.id.sword2Card));
        swords.add(new Sword("pirate_sword","Pirate Sword",40,"images/swords/pirate_sword.png",R.id.sword3Status,R.id.sword3CardFooter, R.id.sword3Card));
        swords.add(new Sword("glass_sword","Glass Sword",70,"images/swords/glass_sword.png",R.id.sword4Status,R.id.sword4CardFooter, R.id.sword4Card));
        swords.add(new Sword("gold_sword","Gold Sword",100,"images/swords/gold_sword.png",R.id.sword5Status,R.id.sword5CardFooter, R.id.sword5Card));
        swords.add(new Sword("diamond_sword","Diamond Sword",150,"images/swords/diamond_sword.png",R.id.sword6Status,R.id.sword6CardFooter, R.id.sword6Card));

        try {
            //Equipped Sword
            InputStream inputStream = mActivity.getAssets().open("images/swords/bronze_sword.png");
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            equippedSword.setImageDrawable(drawable);
            inputStream.close();

            inputStream = mActivity.getAssets().open(swords.get(0).getPath());
            drawable = Drawable.createFromStream(inputStream,null);
            sword1.setImageDrawable(drawable);
            sword1VerseCost.setText(String.format(Locale.getDefault(),"%d",swords.get(0).getCost()));
            inputStream.close();

            inputStream = mActivity.getAssets().open(swords.get(1).getPath());
            drawable = Drawable.createFromStream(inputStream,null);
            sword2.setImageDrawable(drawable);
            sword2VerseCost.setText(String.format(Locale.getDefault(),"%d",swords.get(1).getCost()));
            inputStream.close();

            inputStream = mActivity.getAssets().open(swords.get(2).getPath());
            drawable = Drawable.createFromStream(inputStream,null);
            sword3.setImageDrawable(drawable);
            sword3VerseCost.setText(String.format(Locale.getDefault(),"%d",swords.get(2).getCost()));
            inputStream.close();

            inputStream = mActivity.getAssets().open(swords.get(3).getPath());
            drawable = Drawable.createFromStream(inputStream,null);
            sword4.setImageDrawable(drawable);
            sword4VerseCost.setText(String.format(Locale.getDefault(),"%d",swords.get(3).getCost()));
            inputStream.close();

            inputStream = mActivity.getAssets().open(swords.get(4).getPath());
            drawable = Drawable.createFromStream(inputStream,null);
            sword5.setImageDrawable(drawable);
            sword5VerseCost.setText(String.format(Locale.getDefault(),"%d",swords.get(4).getCost()));
            inputStream.close();

            inputStream = mActivity.getAssets().open(swords.get(5).getPath());
            drawable = Drawable.createFromStream(inputStream,null);
            sword6.setImageDrawable(drawable);
            sword6VerseCost.setText(String.format(Locale.getDefault(),"%d",swords.get(5).getCost()));
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        usersReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(mAuth.getCurrentUser().getUid());
        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String equippedSwordId = dataSnapshot.child(DBConstants.FIREBASE_U_KEY_EQUIPPED_SWORD).getValue(String.class);
                Long versesMemorized = dataSnapshot.child(DBConstants.FIREBASE_U_KEY_VERSES_MEMORIZED).getValue(Long.class);
                versesMemorizedTextView.setText(String.format(Locale.getDefault(),"%d",versesMemorized));

                if(equippedSwordId != null && versesMemorized != null) {
                    for(int i=0;i<swords.size();i++) {
                        if(equippedSwordId.equals(swords.get(i).getId())) {
                            setEquippedSword(swords.get(i));
                            setEquipListener(swords.get(i));
                            showEquipStatusTextView(swords.get(i).getStatusResourceID(), swords.get(i).getCostResourceID(), true);
                        } else if(swords.get(i).getCost() <= versesMemorized) {
                            showEquipStatusTextView(swords.get(i).getStatusResourceID(), swords.get(i).getCostResourceID(), false);
                            setEquipListener(swords.get(i));
                        } else {
                            showLockedLayout(swords.get(i).getStatusResourceID(),swords.get(i).getCostResourceID());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });

    }

    private void setEquipListener(final Sword sword) {
        CardView cardView = rootView.findViewById(sword.getCardViewResourceID());
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEquippedSword(sword);
                showEquipStatusTextView(sword.getStatusResourceID(), sword.getCostResourceID(), true);
            }
        });
    }

    private void setEquippedSword(Sword sword) {
        InputStream inputStream = null;
        try {

            inputStream = mActivity.getAssets().open(sword.getPath());
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            equippedSword.setImageDrawable(drawable);
            inputStream.close();

            equippedSwordTitle.setText(sword.getName());

            usersReference.child(DBConstants.FIREBASE_U_KEY_EQUIPPED_SWORD).setValue(sword.getId());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEquipStatusTextView(int equippedStatus, int cardFooter, boolean equipped) {
        TextView equippedStatusTextView = rootView.findViewById(equippedStatus);
        equippedStatusTextView.setVisibility(View.VISIBLE);
        if(equipped) {
            equippedStatusTextView.setText(mActivity.getString(R.string.equipped));
            setRestToEquip(equippedStatus);
        }
        else {
            equippedStatusTextView.setText(mActivity.getString(R.string.equip));
        }

        LinearLayout cardFooterLayout = rootView.findViewById(cardFooter);
        cardFooterLayout.setVisibility(View.GONE);
    }

    private void showLockedLayout(int equippedStatus, int cardFooter) {
        TextView equippedStatusTextView = rootView.findViewById(equippedStatus);
        equippedStatusTextView.setVisibility(View.GONE);

        LinearLayout cardFooterLayout = rootView.findViewById(cardFooter);
        cardFooterLayout.setVisibility(View.VISIBLE);
    }

    private void setRestToEquip(int equippedStatus) {
        for(int i=0;i<swords.size();i++) {
            if(equippedStatus != swords.get(i).getStatusResourceID()) {
                TextView equippedStatusTextView = rootView.findViewById(swords.get(i).getStatusResourceID());
                equippedStatusTextView.setText(getString(R.string.equip));
            }
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
