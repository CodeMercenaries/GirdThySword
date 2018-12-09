package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.AdminChatMessage;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 09-12-2018.
 */

public class AdminChatRecycleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ADMIN_MESSAGE = 0;
    private static final int TYPE_USER_MESSAGE = 1;

    Activity activity;
    ArrayList<AdminChatMessage> chatMessages;
    int adminMessageResource;
    int userMessageResource;

    public AdminChatRecycleListAdapter(Activity activity, ArrayList<AdminChatMessage> chatMessages) {
        this.activity = activity;
        this.chatMessages = chatMessages;
        this.adminMessageResource = R.layout.custom_left_message_wo_name_bubble;
        this.userMessageResource = R.layout.custom_right_message_wo_name_bubble;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_ADMIN_MESSAGE) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(adminMessageResource,parent,false);
            return new AdminMessageHolder(rootView);
        } else if(viewType == TYPE_USER_MESSAGE) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(userMessageResource,parent,false);
            return new UserMessageHolder(rootView);
        }
        throw new RuntimeException("There is no type that matches the type" + viewType + " make sure you're using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof AdminMessageHolder){
            ((AdminMessageHolder) holder).textMessage.setText(chatMessages.get(position).getText());
        } else if (holder instanceof UserMessageHolder) {
            ((UserMessageHolder) holder).textMessage.setText(chatMessages.get(position).getText());
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionAdminMessage(position)) {
            return TYPE_ADMIN_MESSAGE;
        }
        return TYPE_USER_MESSAGE;
    }

    private boolean isPositionAdminMessage(int position) {
        return chatMessages.get(position).isAdmin();
    }

    class AdminMessageHolder extends RecyclerView.ViewHolder {

        TextView textMessage;

        AdminMessageHolder(View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

    class UserMessageHolder extends RecyclerView.ViewHolder {

        TextView textMessage;

        UserMessageHolder(View itemView) {
            super(itemView);

            textMessage = itemView.findViewById(R.id.textMessage);
        }
    }

}
