package com.vidalingua.whatsnew.listviewsearch;

public class Word {

    String original;
    String normalized;
    String alternative;

    public Word(String original, String normalized, String alternative) {
        this.original = original;
        this.normalized = normalized;
        this.alternative = alternative;
    }

    public String getOriginal() { return original; }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getNormalized() { return normalized; }

    public void setNormalized(String normalized) { this.normalized = normalized; }

    public String getAlternative() { return alternative; }

    public void setAlternative(String alternative) { this.alternative = alternative; }
}
