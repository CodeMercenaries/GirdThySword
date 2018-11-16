package com.code.codemercenaries.girdthyswordpro.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.local.Verse;

import java.util.List;
import java.util.Locale;

/**
 * Created by Joel Kingsley on 15-11-2018.
 */

public class VerseRecycleListAdapter extends RecyclerView.Adapter<VerseRecycleListAdapter.ViewHolder> {

    private List<Verse> verses1;
    private List<Verse> verses2;
    int resource;

    public VerseRecycleListAdapter(List<Verse> verses1) {
        this.verses1 = verses1;
        this.verses2 = null;
        this.resource = R.layout.custom_verse_list;
    }

    public VerseRecycleListAdapter(List<Verse> verses1, List<Verse> verses2) {
        this.verses1 = verses1;
        this.verses2 = verses2;
        this.resource = R.layout.custom_dual_verse_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(resource,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if(resource == R.layout.custom_verse_list) {
            holder.verseNum.setText(String.format(Locale.getDefault(),"%d", verses1.get(position).get_verse_num()));
            holder.verseText1.setText(verses1.get(position).get_verse_text());
        } else if(resource == R.layout.custom_dual_verse_list) {
            holder.verseNum.setText(String.format(Locale.getDefault(),"%d", verses1.get(position).get_verse_num()));
            Log.d("VRecycleListAdapter:","verses1 size:" + verses1.size());
            Log.d("VRecycleListAdapter:","verses2 size:" + verses2.size());
            holder.verseText1.setText(verses1.get(position).get_verse_text());
            holder.verseText2.setText(verses2.get(position).get_verse_text());
        }
    }

    @Override
    public int getItemCount() {
        return verses1.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView verseNum;
        TextView verseText1;
        TextView verseText2;

        private ViewHolder(View itemView) {
            super(itemView);

            verseNum = itemView.findViewById(R.id.verseNum);
            verseText1 = itemView.findViewById(R.id.verseText1);
            verseText2 = itemView.findViewById(R.id.verseText2);
        }
    }
}
