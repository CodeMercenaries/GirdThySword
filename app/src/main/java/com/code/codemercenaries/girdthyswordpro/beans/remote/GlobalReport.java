package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 09-12-2018.
 */

public class GlobalReport {
    private String reportedMessageID;
    private String complainantID;
    private String complaineeID;

    public GlobalReport(String reportedMessageID, String complainantID, String complaineeID) {
        this.reportedMessageID = reportedMessageID;
        this.complainantID = complainantID;
        this.complaineeID = complaineeID;
    }

    public String getReportedMessageID() {
        return reportedMessageID;
    }

    public void setReportedMessageID(String reportedMessageID) {
        this.reportedMessageID = reportedMessageID;
    }

    public String getComplainantID() {
        return complainantID;
    }

    public void setComplainantID(String complainantID) {
        this.complainantID = complainantID;
    }

    public String getComplaineeID() {
        return complaineeID;
    }

    public void setComplaineeID(String complaineeID) {
        this.complaineeID = complaineeID;
    }

    @Override
    public String toString() {
        return "GlobalReport{" +
                "reportedMessageID='" + reportedMessageID + '\'' +
                ", complainantID='" + complainantID + '\'' +
                ", complaineeID='" + complaineeID + '\'' +
                '}';
    }
}
