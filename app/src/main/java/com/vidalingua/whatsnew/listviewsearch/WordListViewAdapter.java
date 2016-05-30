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

import java.util.ArrayList;
import java.util.List;

public class WordListViewAdapter extends ArrayAdapter<Word> {

    private final Context context;
    private List<Word> wordArrayList;
    private List<Word> originalWordArrayList;
    private WordFilter wordFilter;

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
        //Log.i("FILTER", "Got word: " + word.getOriginal());
        holder.wordView.setText(word.getOriginal());

        return row;
    }

    public void resetData() {

        wordArrayList = originalWordArrayList;
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
            FilterResults results = new FilterResults();
            boolean asciiSearch = search.matches("\\A\\p{ASCII}*\\z");
            long start = System.currentTimeMillis();

            Log.i("FILTER", "Filtering... searched for: " + constraint + " --> asciiSearch? " + asciiSearch);

            // No filter implemented, so return the whole list
            if (search == null || search.length() == 0) {
                //Log.i("FILTER", "No filter applied");
                results.values = wordArrayList;
                results.count = wordArrayList.size();
                Log.i("FILTER", "Word array list size: " + wordArrayList.size());
            }
            // Perform the filtering operation
            else {
                List<Word> tempWordList = new ArrayList<>();

                for (Word w : wordArrayList) {

                    // TODO: The search term should be normalized so that we can just search against the normalized field

                    // If the search term contained only ASCII, search using the normalized word
                    if (asciiSearch) {

                        // Split the normalized form of the word, and filter against the start of each word
                        String[] normalizedSplit = w.getNormalized().split(" ");

                        for (String s: normalizedSplit) {
                            if (s.startsWith(search.toString())) {
                                //Log.i("FILTER", "Filter matched on normalized split. Adding: " + w.getOriginal());
                                if (!tempWordList.contains(w)) tempWordList.add(w);
                            }
                        }
                    }
                    // Otherwise the search contains accented characters, so use the original word
                    else {

                        // Split the normalized form of the word, and filter against the start of each word
                        String[] originalSplit = w.getOriginal().split(" ");

                        for (String s : originalSplit) {
                            if (s.startsWith(search.toString())) {
                                //Log.i("FILTER", "Filter matched on original split. Adding: " + w.getOriginal());
                                if (!tempWordList.contains(w)) tempWordList.add(w);
                            }
                        }
                    }
                }

                results.values = tempWordList;
                results.count = tempWordList.size();
            }

            long end = System.currentTimeMillis();
            String message = "Filtering took " + (end - start) + " ms and returned " + results.count + " entries";
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
                Log.i("FILTER", "Word array list new length: " + wordArrayList.size());
                notifyDataSetChanged();
            }
        }

    }
}