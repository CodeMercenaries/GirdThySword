package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 20-11-2018.
 */

public class OverviewRecycleListAdapter extends RecyclerView.Adapter<OverviewRecycleListAdapter.ViewHolder>{

    ArrayList<Chunk> chunks;
    Activity activity;
    private int itemResource;


    public OverviewRecycleListAdapter(Activity activity, ArrayList<Chunk> chunks) {
        this.activity = activity;
        this.chunks = chunks;
        this.itemResource = R.layout.custom_chunk_overview_list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(itemResource,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        StringBuilder builder1 = new StringBuilder();
        builder1.append(chunks.get(position).getBookName());
        builder1.append(" ");
        builder1.append(chunks.get(position).getChapterNum());
        builder1.append(":");
        builder1.append(chunks.get(position).getStartVerseNum());
        builder1.append("-");
        builder1.append(chunks.get(position).getEndVerseNum());
        holder.chunkTitle.setText(builder1.toString());
        StringBuilder builder2 = new StringBuilder();
        builder2.append("(");
        builder2.append(chunks.get(position).getBookName());
        builder2.append(" ");
        builder2.append(chunks.get(position).getChapterNum());
        builder2.append(":");
        builder2.append(chunks.get(position).getStartVerseNum());
        builder2.append("-");
        builder2.append(chunks.get(position).getEndVerseNum());
        builder2.append(")");
        holder.sectionTitle.setText(builder2.toString());
        holder.nextDateOfReview.setText(chunks.get(position).getNextDateOfReview());
    }

    @Override
    public int getItemCount() {
        return chunks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView chunkTitle;
        TextView sectionTitle;
        TextView nextDateOfReview;

        public ViewHolder(View itemView) {
            super(itemView);
            chunkTitle = itemView.findViewById(R.id.chunk_title);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            nextDateOfReview = itemView.findViewById(R.id.nextDateOfReview);
        }
    }
}
