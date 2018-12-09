package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 09-12-2018.
 */

public class GlobalChatMessage {

    private String messageID;
    private String text;
    private String sourceUserID;
    private String timestamp;

    public GlobalChatMessage(String messageID, String text, String sourceUserID, String timestamp) {
        this.messageID = messageID;
        this.text = text;
        this.sourceUserID = sourceUserID;
        this.timestamp = timestamp;
    }

    public GlobalChatMessage() {
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSourceUserID() {
        return sourceUserID;
    }

    public void setSourceUserID(String sourceUserID) {
        this.sourceUserID = sourceUserID;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GlobalChatMessage{" +
                "messageID='" + messageID + '\'' +
                ", text='" + text + '\'' +
                ", sourceUserID='" + sourceUserID + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
