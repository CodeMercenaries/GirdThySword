package com.code.codemercenaries.girdthyswordpro.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.Section;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 03-12-2018.
 */

public class SectionListAdapter extends ArrayAdapter<Section>{

    private Context context;
    private ArrayList<Section> sections;
    private int resource;


    public SectionListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Section> objects) {
        super(context, resource, objects);
        this.context = context;
        this.sections = objects;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(resource, null);
            holder = new ViewHolder();
            holder.sectionTitle = convertView.findViewById(R.id.sectionTitle);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        StringBuilder builder1 = new StringBuilder();
        builder1.append(sections.get(position).getBookName());
        builder1.append(" ");
        builder1.append(sections.get(position).getChapterNum());
        builder1.append(":");
        builder1.append(sections.get(position).getStartVerseNum());
        builder1.append("-");
        builder1.append(sections.get(position).getEndVerseNum());
        builder1.append(" (");
        builder1.append(sections.get(position).getVersionID().toUpperCase());
        builder1.append(")");
        holder.sectionTitle.setText(builder1.toString());

        return convertView;
    }

    static class ViewHolder {
        private TextView sectionTitle;

    }
}
