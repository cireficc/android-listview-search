package com.vidalingua.whatsnew.listviewsearch;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends Activity {

    EditText inputSearch;
    WordListViewAdapter adapter;
    List<Word> wordList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeList();

        ListView lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.dictionary_search);

        adapter = new WordListViewAdapter(MainActivity.this.getBaseContext(), wordList);
        lv.setAdapter(adapter);

        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int start, int before, int count) {

                if (count < before) {
                    // We're deleting a character so we need to reset the adapter data
                    adapter.resetData();
                }

                adapter.getFilter().filter(cs);
            }

            @Override
            public void beforeTextChanged(CharSequence cs, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable editable) {}
        });
    }

    private void initializeList() {

        String words[] = { "English", "two words", "Français", "deux mots",
                "Espagnol", "dos palabras", "Italiano", "deu parole",
                "Deutsche", "zwei Wörter", "Português", "duas palavras",
                "にほんご (日本語)", "ふたつのたんご (二つの単語)" };

        String normalized[] = { "english", "two words", "francais", "deux mots",
                "espagnol", "dos palabras", "italiano", "deu parole",
                "deutsche", "zwei worter", "portugues", "duas palavras",
                "nihongo", "futatsunotango" };

        for (int i = 0; i < words.length; i ++) {
            wordList.add(new Word(words[i], normalized[i]));
        }

        // Sort the word list alphabetically
        Collections.sort(wordList, new Comparator<Word>() {
            @Override
            public int compare(Word word1, Word word2) {

                return word1.getNormalized().compareTo(word2.getNormalized());
            }
        });
    }
}