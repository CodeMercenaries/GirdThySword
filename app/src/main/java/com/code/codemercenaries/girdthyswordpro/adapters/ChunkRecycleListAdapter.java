package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.activities.SelectReviewTypeActivity;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Chunk;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Joel Kingsley on 17-11-2018.
 */

public class ChunkRecycleListAdapter extends RecyclerView.Adapter<ChunkRecycleListAdapter.ViewHolder> {

    Activity activity;
    ArrayList<Chunk> chunks;
    Date currDate;
    private int resource;

    public ChunkRecycleListAdapter(Activity activity, ArrayList<Chunk> chunks) {
        this.activity = activity;
        this.chunks = chunks;
        this.resource = R.layout.custom_chunk_list;
        this.currDate = new Date();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(resource,parent,false);
        return new ViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
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
        builder2.append(chunks.get(position).getBookName());
        builder2.append(" ");
        builder2.append(chunks.get(position).getChapterNum());
        builder2.append(":");
        builder2.append(chunks.get(position).getStartVerseNum());
        builder2.append("-");
        builder2.append(chunks.get(position).getEndVerseNum());
        holder.sectionTitle.setText(builder2.toString());

        SimpleDateFormat df = new SimpleDateFormat(DBConstants.DATE_FORMAT,Locale.US);
        if(df.format(currDate).equals(chunks.get(position).getNextDateOfReview())) {
            holder.scheduledDate.setText(activity.getString(R.string.today));
        } else {
            holder.scheduledDate.setText(chunks.get(position).getNextDateOfReview());
        }

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, SelectReviewTypeActivity.class);
                intent.putExtra(DBConstants.REVIEW_CHUNK_ID,chunks.get(position).getChunkID());
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chunks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout parentLayout;
        TextView chunkTitle;
        TextView sectionTitle;
        TextView scheduledDate;

        public ViewHolder(View itemView) {
            super(itemView);
            parentLayout = itemView.findViewById(R.id.parentLayout);
            chunkTitle = itemView.findViewById(R.id.chunk_title);
            sectionTitle = itemView.findViewById(R.id.sectionTitle);
            scheduledDate = itemView.findViewById(R.id.scheduledDate);
        }
    }
}
