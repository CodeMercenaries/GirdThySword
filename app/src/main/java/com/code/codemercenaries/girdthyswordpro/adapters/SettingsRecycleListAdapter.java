package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.local.SettingsItem;
import com.code.codemercenaries.girdthyswordpro.beans.remote.User;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 16-12-2018.
 */

public class SettingsRecycleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SWITCH = 0;
    User user;
    DatabaseReference userReference;
    private Activity activity;
    private ArrayList<SettingsItem> settingsItems;
    private int switchResource;

    public SettingsRecycleListAdapter(Activity activity, ArrayList<SettingsItem> settingsItems, User user) {
        this.activity = activity;
        this.settingsItems = settingsItems;
        this.switchResource = R.layout.custom_switch_settings_list;
        this.user = user;

        userReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_USERS).child(user.getUuid());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_SWITCH) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(switchResource,parent,false);
            return new VHSwitch(rootView);
        }
        throw new RuntimeException("There is no type that matches the type" + viewType + " make sure you're using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof VHSwitch) {
            ((VHSwitch) holder).switchView.setChecked(user.isOptOutOfLB());
            ((VHSwitch) holder).switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    userReference.child(DBConstants.FIREBASE_U_KEY_OPT_OUT_OF_LB).setValue(isChecked);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return settingsItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch(position) {
            case DBConstants.OPT_OUT_OF_LEADERBOARD:
                return TYPE_SWITCH;
            default:
                return super.getItemViewType(position);
        }
    }

    public static class VHSwitch extends RecyclerView.ViewHolder {

        TextView heading;
        TextView subHeading;
        Switch switchView;

        public VHSwitch(View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.heading);
            subHeading = itemView.findViewById(R.id.subHeading);
            switchView = itemView.findViewById(R.id.switchButton);
        }
    }
}
