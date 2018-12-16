package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.local.SettingsItem;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 16-12-2018.
 */

public class SettingsRecycleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SWITCH = 0;
    private Activity activity;
    private ArrayList<SettingsItem> settingsItems;
    private int switchResource;

    public SettingsRecycleListAdapter(Activity activity, ArrayList<SettingsItem> settingsItems) {
        this.activity = activity;
        this.settingsItems = settingsItems;
        this.switchResource = R.layout.custom_switch_settings_list;
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
