package com.vidalingua.whatsnew.listviewsearch;

public class Word {

    String original;
    String normalized;

    public Word(String original, String normalized) {
        this.original = original;
        this.normalized = normalized;
    }

    public String getOriginal() { return original; }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getNormalized() { return normalized; }

    public void setNormalized(String normalized) { this.normalized = normalized; }
}
