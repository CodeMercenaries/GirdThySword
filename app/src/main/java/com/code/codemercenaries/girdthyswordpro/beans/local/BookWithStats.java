package com.code.codemercenaries.girdthyswordpro.beans.local;

/**
 * Created by Joel Kingsley on 16-11-2018.
 */

public class BookWithStats {
    private String bookName;
    private int readOrMemorizedPercentage;
    private int readOrMemorizedChapters;
    private int totalChapters;

    public BookWithStats(String bookName, int readOrMemorizedPercentage, int readOrMemorizedChapters, int totalChapters) {
        this.bookName = bookName;
        this.readOrMemorizedPercentage = readOrMemorizedPercentage;
        this.readOrMemorizedChapters = readOrMemorizedChapters;
        this.totalChapters = totalChapters;
    }

    public BookWithStats(String bookName, int totalChapters) {
        this.bookName = bookName;
        this.totalChapters = totalChapters;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getReadOrMemorizedPercentage() {
        return readOrMemorizedPercentage;
    }

    public void setReadOrMemorizedPercentage(int readOrMemorizedPercentage) {
        this.readOrMemorizedPercentage = readOrMemorizedPercentage;
    }

    public int getReadOrMemorizedChapters() {
        return readOrMemorizedChapters;
    }

    public void setReadOrMemorizedChapters(int readOrMemorizedChapters) {
        this.readOrMemorizedChapters = readOrMemorizedChapters;
    }

    public int getTotalChapters() {
        return totalChapters;
    }

    public void setTotalChapters(int totalChapters) {
        this.totalChapters = totalChapters;
    }

    @Override
    public String toString() {
        return "BookWithStats{" +
                "bookName='" + bookName + '\'' +
                ", readOrMemorizedPercentage=" + readOrMemorizedPercentage +
                ", readOrMemorizedChapters=" + readOrMemorizedChapters +
                ", totalChapters=" + totalChapters +
                '}';
    }
}
