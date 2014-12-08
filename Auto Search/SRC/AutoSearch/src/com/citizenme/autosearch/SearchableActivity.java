package com.citizenme.autosearch;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class SearchableActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor> {

    ListView dicttionarLV;
    SimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_searchable);

        dicttionarLV = (ListView) findViewById(R.id.dictLV);

        dicttionarLV.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {

                Intent searchResultIntent = new Intent(getApplicationContext(),
                        SearchResultActivity.class);
                Uri data = Uri.withAppendedPath(
                        DictionaryContentProvider.CONTENT_URI, String.valueOf(id));
                searchResultIntent.setData(data);
                startActivity(searchResultIntent);
            }
        });

        // Defining CursorAdapter for the ListView
        mCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
                android.R.layout.simple_list_item_1, null,
                new String[] { SearchManager.SUGGEST_COLUMN_TEXT_1 },
                new int[] { android.R.id.text1 }, 0);

        // Setting the adapter
        dicttionarLV.setAdapter(mCursorAdapter);

        Intent intent = getIntent();

        // Through Action View
        if (intent.getAction().equals(Intent.ACTION_VIEW)) {
            Intent countryIntent = new Intent(this, SearchResultActivity.class);
            countryIntent.setData(intent.getData());
            startActivity(countryIntent);
            finish();
        } else if (intent.getAction().equals(Intent.ACTION_SEARCH)) {
            // Through Action Search
            String query = intent.getStringExtra(SearchManager.QUERY);
            performSearch(query);
        }
    }

    private void performSearch(String query) {
        Bundle data = new Bundle();
        data.putString("query", query);
        getSupportLoaderManager().initLoader(1, data, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
        Uri uri = DictionaryContentProvider.CONTENT_URI;
        return new CursorLoader(getBaseContext(), uri, null, null,
                new String[] { data.getString("query") }, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
        mCursorAdapter.swapCursor(c);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub
    }
}