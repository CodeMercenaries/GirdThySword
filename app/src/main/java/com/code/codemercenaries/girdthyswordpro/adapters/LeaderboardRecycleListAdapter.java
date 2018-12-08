package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.User;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Joel Kingsley on 08-12-2018.
 */

public class LeaderboardRecycleListAdapter extends RecyclerView.Adapter<LeaderboardRecycleListAdapter.ViewHolder> {

    Activity activity;
    ArrayList<User> users;
    int itemResource;
    FirebaseAuth mAuth;

    public LeaderboardRecycleListAdapter(Activity activity, ArrayList<User> users) {
        this.activity = activity;
        this.users = users;
        this.itemResource = R.layout.custom_leaderboard_list;
        this.mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(itemResource,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.rank.setText(String.format(Locale.getDefault(),"%d",position+1));
        holder.name.setText(String.format(Locale.getDefault(),"%s",users.get(position).getDisplayName()));
        Glide.with(activity).load(users.get(position).getPhotoURL()).into(holder.image);

        String swordPath;

        switch(users.get(position).getEquippedSword()) {
            case DBConstants.BRONZE_SWORD:
                swordPath = "images/swords/bronze_sword.png";
                break;
            case DBConstants.SOLDIER_SWORD:
                swordPath = "images/swords/soldier_sword.png";
                break;
            case DBConstants.PIRATE_SWORD:
                swordPath = "images/swords/pirate_sword.png";
                break;
            case DBConstants.GLASS_SWORD:
                swordPath = "images/swords/glass_sword.png";
                break;
            case DBConstants.GOLD_SWORD:
                swordPath = "images/swords/gold_sword.png";
                break;
            case DBConstants.DIAMOND_SWORD:
                swordPath = "images/swords/diamond_sword.png";
                break;
            default:
                swordPath = "images/swords/bronze_sword.png";
        }

        InputStream inputStream = null;
        try {
            inputStream = activity.getAssets().open(swordPath);
            Drawable drawable = Drawable.createFromStream(inputStream,null);
            holder.sword.setImageDrawable(drawable);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.versesMemorized.setText(String.format(Locale.getDefault(),"%d",users.get(position).getVersesMemorized()));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView rank;
        ImageView image;
        TextView name;
        ImageView sword;
        TextView versesMemorized;

         ViewHolder(View itemView) {
            super(itemView);

            rank = itemView.findViewById(R.id.rank);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            sword = itemView.findViewById(R.id.sword);
            versesMemorized = itemView.findViewById(R.id.versesMemorized);
        }
    }
}
