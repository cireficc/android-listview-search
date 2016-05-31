package com.vidalingua.whatsnew.listviewsearch;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class WordListViewAdapter extends ArrayAdapter<Word> {

    private final Context context;
    private final String FILTER_TAG = "FILTER";
    private List<Word> wordArrayList;
    private List<Word> originalWordArrayList;
    private WordFilter wordFilter;
    // Regex to match ASCII-only strings
    private final String ASCII_ONLY_REGEX = "\\A\\p{ASCII}*\\z";
    // Regex to match non-ASCII characters
    private final String NON_ASCII_REGEX = "[^\\p{ASCII}]";

    public WordListViewAdapter(Context context, List<Word> wordArrayList) {

        super(context, R.layout.list_item, wordArrayList);

        this.context = context;
        this.wordArrayList = wordArrayList;
        this.originalWordArrayList = wordArrayList;
    }

    public int getCount() { return wordArrayList.size(); }

    public Word getWord(int index) { return wordArrayList.get(index); }

    public long getWordId(int position) { return wordArrayList.get(position).hashCode(); }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;

        WordListHolder holder = new WordListHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.list_item, parent, false);
            holder.wordView = (TextView) row.findViewById(R.id.word);

            row.setTag(holder);
        } else {
            holder = (WordListHolder) row.getTag();
        }

        Word word = wordArrayList.get(position);
        //Log.i(FILTER_TAG, "Got word: " + word.getOriginal());
        holder.wordView.setText(word.getOriginal());

        return row;
    }

    public void resetData() {

        wordArrayList = originalWordArrayList;
    }

    public boolean matches(String search, String normalizedSearch, Word word, boolean asciiOnly) {

        // If the search term contained only ASCII, search using the normalized word
        if (asciiOnly) {

            if (word.getNormalized().startsWith(normalizedSearch)) {
                //Log.i(FILTER_TAG, "Matched on normalized (whole)");
                return true;
            }

            // Split the normalized form of the word, and filter against the start of each word
            String[] normalizedSplit = word.getNormalized().split(" ");

            for (String s: normalizedSplit) {
                if (s.startsWith(normalizedSearch)) {
                    //Log.i(FILTER_TAG, "Matched on normalized (split)");
                    return true;
                }
            }
        }
        // Otherwise the search contains accented characters, so use the original word.
        // If no entries match on the original field, check the alternative field last
        else {

            if (word.getOriginal().toLowerCase().startsWith(search.toLowerCase())) {
                //Log.i(FILTER_TAG, "Matched on original (whole)");
                return true;
            }

            // Split the original form of the word, and filter against the start of each word
            String[] originalSplit = word.getOriginal().split(" ");

            for (String s : originalSplit) {
                if (s.toLowerCase().startsWith(search.toLowerCase())) {
                    //Log.i(FILTER_TAG, "Matched on original split");
                    return true;
                }
            }

            if (word.getAlternative().contains(search)) {
                //Log.i(FILTER_TAG, "Matched on alternative (whole)");
                return true;
            }
        }

        return false;
    }

    public int getFirstMatchingEntryPosition(CharSequence constraint) {

        String search = constraint.toString();
        String normalizedSearch =
                Normalizer.normalize(search, Normalizer.Form.NFD)
                .replaceAll(NON_ASCII_REGEX, "")
                .toLowerCase();
        //Log.i(FILTER_TAG, "Normalized search term: " + normalizedSearch);
        boolean asciiSearch = search.matches(ASCII_ONLY_REGEX);

        //Log.i(FILTER_TAG, "Using first matching entry position");

        long start = System.currentTimeMillis();

        // If no constraint, simply go to the top of the list
        if (constraint == null || constraint.length() == 0) {
            //Log.i(FILTER_TAG, "Null or empty search: null? " + (constraint == null) + " || empty? " + constraint.length());
            return 0;
        }

        for (Word w : wordArrayList) {

            if (matches(search, normalizedSearch, w, asciiSearch)) {
                long end = System.currentTimeMillis();
                String message = "Scroll to position for " + constraint + " took " + (end - start) + " ms and matched at index " + getPosition(w);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                return getPosition(w);
            }
        }

        // If all else fails and nothing matched, go to the top of the list
        return 0;
    }

    @Override
    public Filter getFilter() {

        if (wordFilter == null) {
            wordFilter = new WordFilter();
        }

        return wordFilter;
    }

    // Holder pattern
    public class WordListHolder {

        public TextView wordView;
    }

    // Implement a custom filter for the Word object
    private class WordFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String search = constraint.toString();
            String normalizedSearch =
                    Normalizer.normalize(search, Normalizer.Form.NFD)
                    .replaceAll(NON_ASCII_REGEX, "")
                    .toLowerCase();
            //Log.i(FILTER_TAG, "Normalized search term: " + normalizedSearch);
            boolean asciiSearch = search.matches(ASCII_ONLY_REGEX);
            FilterResults results = new FilterResults();

            //Log.i(FILTER_TAG, "Filtering... searched for: " + constraint + " --> asciiSearch? " + asciiSearch);

            long start = System.currentTimeMillis();

            // No filter implemented, so return the whole list
            if (search == null || search.length() == 0) {
                //Log.i(FILTER_TAG, "No filter applied");
                results.values = wordArrayList;
                results.count = wordArrayList.size();
                //Log.i(FILTER_TAG, "Word array list size: " + wordArrayList.size());
            }
            // Perform the filtering operation
            else {
                List<Word> tempWordList = new ArrayList<>();

                for (Word w : wordArrayList) {
                    if (matches(search, normalizedSearch, w, asciiSearch)) tempWordList.add(w);
                }

                results.values = tempWordList;
                results.count = tempWordList.size();
            }

            long end = System.currentTimeMillis();
            String message = "Filtering for " + search + " took " + (end - start) + " ms and returned " + results.count + " entries";
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                wordArrayList = (List<Word>) results.values;
                //Log.i(FILTER_TAG, "Word array list new length: " + wordArrayList.size());
                notifyDataSetChanged();
            }
        }

    }
}