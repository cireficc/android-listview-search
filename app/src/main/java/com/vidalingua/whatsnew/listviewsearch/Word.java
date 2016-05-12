package com.vidalingua.whatsnew.listviewsearch;

public class Word {

    String word;
    String normalized;

    public Word(String word, String normalized) {
        this.word = word;
        this.normalized = normalized;
    }

    public String getWord() { return word; }

    public void setWord(String word) {
        this.word = word;
    }

    public String getNormalized() { return normalized; }

    public void setNormalized(String normalized) { this.normalized = normalized; }
}
