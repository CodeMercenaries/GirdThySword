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

/**
 * Created by Joel Kingsley on 09-12-2018.
 */

public class SettingsListAdapter extends ArrayAdapter {

    Context context;
    int resource;

    public SettingsListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
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
            holder.heading = convertView.findViewById(R.id.heading);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        View rowView = inflater.inflate(this.resource, parent, false);
        return rowView;
    }

    static class ViewHolder{
        private TextView heading;
        private TextView subHeading;
        private TextView value;
    }
}
