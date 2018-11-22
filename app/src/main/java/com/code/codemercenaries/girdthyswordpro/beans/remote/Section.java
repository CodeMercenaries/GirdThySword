package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 17-11-2018.
 */

public class Section {
    private String sectionID;
    private String versionID;
    private String bookName;
    private int chapterNum;
    private int startVerseNum;
    private int endVerseNum;

    public Section(String sectionID, String versionID, String bookName, int chapterNum, int startVerseNum, int endVerseNum) {
        this.sectionID = sectionID;
        this.versionID = versionID;
        this.bookName = bookName;
        this.chapterNum = chapterNum;
        this.startVerseNum = startVerseNum;
        this.endVerseNum = endVerseNum;
    }

    public String getSectionID() {
        return sectionID;
    }

    public void setSectionID(String sectionID) {
        this.sectionID = sectionID;
    }

    public String getVersionID() {
        return versionID;
    }

    public void setVersionID(String versionID) {
        this.versionID = versionID;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getChapterNum() {
        return chapterNum;
    }

    public void setChapterNum(int chapterNum) {
        this.chapterNum = chapterNum;
    }

    public int getStartVerseNum() {
        return startVerseNum;
    }

    public void setStartVerseNum(int startVerseNum) {
        this.startVerseNum = startVerseNum;
    }

    public int getEndVerseNum() {
        return endVerseNum;
    }

    public void setEndVerseNum(int endVerseNum) {
        this.endVerseNum = endVerseNum;
    }

    @Override
    public String toString() {
        return "Section{" +
                "sectionID='" + sectionID + '\'' +
                ", versionID='" + versionID + '\'' +
                ", bookName='" + bookName + '\'' +
                ", chapterNum=" + chapterNum +
                ", startVerseNum=" + startVerseNum +
                ", endVerseNum=" + endVerseNum +
                '}';
    }
}
