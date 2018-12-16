package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 02-11-2018.
 */

public class User {
    private String uuid;
    private String displayName;
    private String email;
    private Integer level;
    private String photoURL;
    private Long versesMemorized;
    private Long versesAdded;
    private String createdBy;
    private String lastUpdatedBy;
    private String equippedSword;
    private boolean optOutOfLB;

    public User(String uuid, String displayName, String email, Integer level, String photoURL , Long versesMemorized, Long versesAdded, String createdBy, String lastUpdatedBy, String equippedSword) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.email = email;
        this.level = level;
        this.photoURL = photoURL;
        this.versesMemorized = versesMemorized;
        this.versesAdded = versesAdded;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
        this.equippedSword = equippedSword;
        this.optOutOfLB = false;
    }

    public User(String uuid, String displayName, String email, Integer level, String photoURL , Long versesMemorized, Long versesAdded) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.email = email;
        this.level = level;
        this.photoURL = photoURL;
        this.versesMemorized = versesMemorized;
        this.versesAdded = versesAdded;
        this.optOutOfLB = false;
    }

    public User(String uuid, String displayName, String email, String photoURL , String createdBy, String lastUpdatedBy) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.email = email;
        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;
        this.level = 0;
        this.photoURL = photoURL;
        this.versesMemorized = 0L;
        this.versesAdded = 0L;
        this.equippedSword = "bronze_sword";
        this.optOutOfLB = false;
    }

    public User() {
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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public Long getVersesMemorized() {
        return versesMemorized;
    }

    public void setVersesMemorized(Long versesMemorized) {
        this.versesMemorized = versesMemorized;
    }

    public Long getVersesAdded() {
        return versesAdded;
    }

    public void setVersesAdded(Long versesAdded) {
        this.versesAdded = versesAdded;
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

    public String getEquippedSword() {
        return equippedSword;
    }

    public void setEquippedSword(String equippedSword) {
        this.equippedSword = equippedSword;
    }

    public boolean isOptOutOfLB() {
        return optOutOfLB;
    }

    public void setOptOutOfLB(boolean optOutOfLB) {
        this.optOutOfLB = optOutOfLB;
    }

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", level=" + level +
                ", photoURL='" + photoURL + '\'' +
                ", versesMemorized=" + versesMemorized +
                ", versesAdded=" + versesAdded +
                ", createdBy='" + createdBy + '\'' +
                ", lastUpdatedBy='" + lastUpdatedBy + '\'' +
                ", equippedSword='" + equippedSword + '\'' +
                ", optOutOfLB=" + optOutOfLB +
                '}';
    }
}
