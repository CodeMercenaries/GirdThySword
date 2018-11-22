package com.code.codemercenaries.girdthyswordpro.beans.remote;

/**
 * Created by Joel Kingsley on 17-11-2018.
 */

public class Chunk {
    private String chunkID;
    private String sectionID;
    private String versionID;
    private String bookName;
    private int chapterNum;
    private int startVerseNum;
    private int endVerseNum;
    private int space;
    private int sequenceNum;
    private String nextDateOfReview;
    private boolean mastered;

    public Chunk(String chunkID, String sectionID, String versionID, String bookName, int chapterNum, int startVerseNum, int endVerseNum, int space, int sequenceNum, String nextDateOfReview, boolean mastered) {
        this.chunkID = chunkID;
        this.sectionID = sectionID;
        this.versionID = versionID;
        this.bookName = bookName;
        this.chapterNum = chapterNum;
        this.startVerseNum = startVerseNum;
        this.endVerseNum = endVerseNum;
        this.space = space;
        this.sequenceNum = sequenceNum;
        this.nextDateOfReview = nextDateOfReview;
        this.mastered = mastered;
    }

    public Chunk() {
    }

    public String getChunkID() {
        return chunkID;
    }

    public void setChunkID(String chunkID) {
        this.chunkID = chunkID;
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

    public int getSpace() {
        return space;
    }

    public void setSpace(int space) {
        this.space = space;
    }

    public int getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(int sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public String getNextDateOfReview() {
        return nextDateOfReview;
    }

    public void setNextDateOfReview(String nextDateOfReview) {
        this.nextDateOfReview = nextDateOfReview;
    }

    public boolean isMastered() {
        return mastered;
    }

    public void setMastered(boolean mastered) {
        this.mastered = mastered;
    }

    @Override
    public String toString() {
        return "Chunk{" +
                "chunkID='" + chunkID + '\'' +
                ", sectionID='" + sectionID + '\'' +
                ", versionID='" + versionID + '\'' +
                ", bookName='" + bookName + '\'' +
                ", chapterNum=" + chapterNum +
                ", startVerseNum=" + startVerseNum +
                ", endVerseNum=" + endVerseNum +
                ", space=" + space +
                ", sequenceNum=" + sequenceNum +
                ", nextDateOfReview=" + nextDateOfReview +
                ", mastered=" + mastered +
                '}';
    }
}
