package com.code.codemercenaries.girdthyswordpro.adapters;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.code.codemercenaries.girdthyswordpro.R;
import com.code.codemercenaries.girdthyswordpro.beans.remote.GlobalChatMessage;
import com.code.codemercenaries.girdthyswordpro.beans.remote.GlobalReport;
import com.code.codemercenaries.girdthyswordpro.persistence.DBConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by Joel Kingsley on 09-12-2018.
 */

public class GlobalChatRecycleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int TYPE_SENT_MESSAGE = 0;
    private static final int TYPE_RECEIVED_MESSAGE = 1;
    DatabaseReference globalReportsReference;
    private Activity activity;
    private ArrayList<GlobalChatMessage> chatMessages;
    private int sentMessageResource;
    private int receivedMessageResource;
    private String currentUserID;

    public GlobalChatRecycleListAdapter(Activity activity, ArrayList<GlobalChatMessage> chatMessages, String currentUserID) {
        this.activity = activity;
        this.chatMessages = chatMessages;
        this.sentMessageResource = R.layout.custom_right_message_bubble;
        this.receivedMessageResource = R.layout.custom_left_message_bubble;
        this.currentUserID = currentUserID;
        this.globalReportsReference = FirebaseDatabase.getInstance().getReference(DBConstants.FIREBASE_TABLE_GLOBAL_REPORTS);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_SENT_MESSAGE) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(sentMessageResource,parent,false);
            return new SentMessageHolder(rootView);
        } else if(viewType == TYPE_RECEIVED_MESSAGE) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(receivedMessageResource,parent,false);
            return new ReceivedMessageHolder(rootView);
        }
        throw new RuntimeException("There is no type that matches the type" + viewType + " make sure you're using types correctly");
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof SentMessageHolder){
            ((SentMessageHolder) holder).textMessage.setText(chatMessages.get(position).getText());
            ((SentMessageHolder) holder).senderName.setText(chatMessages.get(position).getSourceUserID());
        } else if (holder instanceof ReceivedMessageHolder) {
            ((ReceivedMessageHolder) holder).textMessage.setText(chatMessages.get(position).getText());
            ((ReceivedMessageHolder) holder).senderName.setText(chatMessages.get(position).getSourceUserID());
            ((ReceivedMessageHolder) holder).textMessage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
                    builder.setTitle("Report Message")
                            .setMessage("Are you sure you want to report this message?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    String newGlobalReportID = globalReportsReference.push().getKey();
                                    GlobalReport globalReport = new GlobalReport(newGlobalReportID,currentUserID,chatMessages.get(holder.getAdapterPosition()).getSourceUserID());
                                    if (newGlobalReportID != null)
                                        globalReportsReference.child(newGlobalReportID).setValue(globalReport);
                                    Toast.makeText(activity, "Message Reported",Toast.LENGTH_LONG).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // do nothing
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(isPositionSentMessage(position)) {
            return TYPE_SENT_MESSAGE;
        }
        return TYPE_RECEIVED_MESSAGE;
    }

    private boolean isPositionSentMessage(int position) {
        return chatMessages.get(position).getSourceUserID().equals(this.currentUserID);
    }

    class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView textMessage;
        TextView senderName;

        SentMessageHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            senderName = itemView.findViewById(R.id.senderName);
        }
    }

    class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView textMessage;
        TextView senderName;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            textMessage = itemView.findViewById(R.id.textMessage);
            senderName = itemView.findViewById(R.id.senderName);
        }
    }

}
