package com.code.codemercenaries.girdthyswordpro.beans.local;

/**
 * Created by Joel Kingsley on 15-11-2018.
 */

public class Verse {
    String _version;
    String _book_name;
    int _chap_num;
    int _verse_num;
    String _verse_text;
    int _memory;
    int _read;

    public Verse(String _version, String _book_name, int _chap_num, int _verse_num, String _verse_text, int _memory, int _read) {
        this._version = _version;
        this._book_name = _book_name;
        this._chap_num = _chap_num;
        this._verse_num = _verse_num;
        this._verse_text = _verse_text;
        this._memory = _memory;
        this._read = _read;
    }

    public Verse(String _version, String _book_name, int _chap_num, int _verse_num, String _verse_text) {
        this._version = _version;
        this._book_name = _book_name;
        this._chap_num = _chap_num;
        this._verse_num = _verse_num;
        this._verse_text = _verse_text;
        this._memory = 0;
        this._read = 0;
    }

    public String get_version() {
        return _version;
    }

    public void set_version(String _version) {
        this._version = _version;
    }

    public String get_book_name() {
        return _book_name;
    }

    public void set_book_name(String _book_name) {
        this._book_name = _book_name;
    }

    public int get_chap_num() {
        return _chap_num;
    }

    public void set_chap_num(int _chap_num) {
        this._chap_num = _chap_num;
    }

    public int get_verse_num() {
        return _verse_num;
    }

    public void set_verse_num(int _verse_num) {
        this._verse_num = _verse_num;
    }

    public String get_verse_text() {
        return _verse_text;
    }

    public void set_verse_text(String _verse_text) {
        this._verse_text = _verse_text;
    }

    public int get_memory() {
        return _memory;
    }

    public void set_memory(int _memory) {
        this._memory = _memory;
    }

    public int get_read() {
        return _read;
    }

    public void set_read(int _read) {
        this._read = _read;
    }

    @Override
    public String toString() {
        return "Verse{" +
                "_version='" + _version + '\'' +
                ", _book_name='" + _book_name + '\'' +
                ", _chap_num=" + _chap_num +
                ", _verse_num=" + _verse_num +
                ", _verse_text='" + _verse_text + '\'' +
                ", _memory=" + _memory +
                ", _read=" + _read +
                '}';
    }
}
