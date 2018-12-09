package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 09-12-2018.
 */

public class AdminChatMessage {
    private String messageID;
    private boolean isAdmin;
    private String text;
    private String timestamp;

    public AdminChatMessage(String messageID, boolean isAdmin, String text, String timestamp) {
        this.messageID = messageID;
        this.isAdmin = isAdmin;
        this.text = text;
        this.timestamp = timestamp;
    }

    public AdminChatMessage() {
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "AdminChatMessage{" +
                "messageID='" + messageID + '\'' +
                ", isAdmin=" + isAdmin +
                ", text='" + text + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
