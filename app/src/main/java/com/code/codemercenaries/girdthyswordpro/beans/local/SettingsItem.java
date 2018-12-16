package com.code.codemercenaries.girdthyswordpro.beans.local;

/**
 * Created by Joel Kingsley on 16-12-2018.
 */

public class SettingsItem {

    private String heading;
    private String subHeading;

    public SettingsItem(String heading, String subHeading) {
        this.heading = heading;
        this.subHeading = subHeading;
    }

    public SettingsItem() {
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getSubHeading() {
        return subHeading;
    }

    public void setSubHeading(String subHeading) {
        this.subHeading = subHeading;
    }

    @Override
    public String toString() {
        return "SettingsItem{" +
                "heading='" + heading + '\'' +
                ", subHeading='" + subHeading + '\'' +
                '}';
    }
}
