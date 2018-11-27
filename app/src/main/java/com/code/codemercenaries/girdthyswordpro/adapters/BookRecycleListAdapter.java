package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.local.BookWithStats;
import com.github.lzyzsd.circleprogress.DonutProgress;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Joel Kingsley on 16-11-2018.
 */

public class BookRecycleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private ArrayList<BookWithStats> bookWithStatsList;
    private Activity activity;
    private int headerResource;
    private int itemResource;
    private int totalChaptersRead;

    public BookRecycleListAdapter(Activity activity, ArrayList<BookWithStats> bookWithStatsList) {
        this.activity = activity;
        this.bookWithStatsList = bookWithStatsList;
        this.headerResource = R.layout.custom_progress_item;
        this.itemResource = R.layout.custom_book_list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ITEM) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(itemResource,parent,false);
            return new VHItem(rootView);
        } else if(viewType == TYPE_HEADER) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(headerResource,parent,false);
            return new VHHeader(rootView);
        }
        throw new RuntimeException("There is no type that matches the type" + viewType + " make sure you're using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof VHItem){
            ((VHItem) holder).bookName.setText(bookWithStatsList.get(position-1).getBookName());
            StringBuilder builder1 = new StringBuilder();
            builder1.append(String.format(Locale.getDefault(),"%d",bookWithStatsList.get(position-1).getReadOrMemorizedPercentage()));
            builder1.append("%");
            ((VHItem) holder).readPercentage.setText(builder1);
            StringBuilder builder2 = new StringBuilder();
            builder2.append(bookWithStatsList.get(position-1).getReadOrMemorizedChapters());
            builder2.append("/");
            builder2.append(bookWithStatsList.get(position-1).getTotalChapters());
            ((VHItem) holder).readChapters.setText(builder2);
        } else if (holder instanceof VHHeader) {
            this.totalChaptersRead = 0;
            for(BookWithStats bookWithStats: bookWithStatsList) {
                this.totalChaptersRead += bookWithStats.getReadOrMemorizedChapters();
            }

            ((VHHeader) holder).progressBar.setDonut_progress(Integer.toString((totalChaptersRead*100)/1189));
            ((VHHeader) holder).readOrMemorizedChapters.setText(activity.
                    getString(R.string.of_1189_read_chapters,totalChaptersRead));
        }
    }

    @Override
    public int getItemCount() {
        return bookWithStatsList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionHeader(position)) {
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    public static class VHHeader extends RecyclerView.ViewHolder {
        DonutProgress progressBar;
        TextView readOrMemorizedChapters;

        public VHHeader(View itemView) {
            super(itemView);

            progressBar = itemView.findViewById(R.id.totalChaptersProgress);
            readOrMemorizedChapters = itemView.findViewById(R.id.readOrMemorizedChapters);
        }
    }

    public static class VHItem extends RecyclerView.ViewHolder {

        TextView bookName;
        TextView readPercentage;
        TextView readChapters;

        private VHItem(View itemView) {
            super(itemView);

            bookName = itemView.findViewById(R.id.bookName);
            readPercentage = itemView.findViewById(R.id.readOrMemorizedPercentage);
            readChapters = itemView.findViewById(R.id.readOrMemorizedChapters);
        }
    }
}
