package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 02-11-2018.
 */

public class User {
    String uuid;
    String displayName;
    String email;
    Integer level;
    Long versesMemorized;
    String createdBy;
    String lastUpdatedBy;

    public User(String uuid, String displayName, String email, String createdBy, String lastUpdatedBy) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.email = email;
        this.level = 0;
        this.versesMemorized = 0L;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public User(String uuid, String displayName, String email, Integer level, Long versesMemorized, String createdBy, String lastUpdatedBy) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.email = email;
        this.level = level;
        this.versesMemorized = versesMemorized;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Long getVersesMemorized() {
        return versesMemorized;
    }

    public void setVersesMemorized(Long versesMemorized) {
        this.versesMemorized = versesMemorized;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", versesMemorized=" + versesMemorized +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                '}';
    }
}