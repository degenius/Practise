package com.citizenme.autosearch;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.citizenme.model.GoogleSearchModel;
import com.citizenme.model.Items;
import com.citizenme.network.APIResponseHandler;
import com.citizenme.network.ConnectionManager;

public class SearchResultActivity extends FragmentActivity implements
        LoaderCallbacks<Cursor>,
        com.citizenme.autosearch.ListViewAdapter.ClickEvents {

    private Uri mUri;
    private ListView resultLV;
    private TextView titleTV;
    private TextView searchDetailsTV;

    public static String KEY_LINK_URL = "_KEY_LINK_URL";

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.searchlist);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        resultLV = (ListView) findViewById(R.id.mylistView);
        titleTV = (TextView) findViewById(R.id.mysearchTV);
        searchDetailsTV = (TextView) findViewById(R.id.mysearchdetailsTV);
        searchDetailsTV.setVisibility(View.INVISIBLE);

        Intent intent = getIntent();
        mUri = intent.getData();

        getSupportLoaderManager().initLoader(0, null, this);

    }

    /** Invoked by initLoader() */
    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(getBaseContext(), mUri, null, null, null, null);
    }

    /** Invoked by onCreateLoader(), will be executed in ui-thread */
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
        if (cursor.moveToFirst()) {

            String keyword = cursor.getString(cursor.getColumnIndex(cursor
                    .getColumnName(1)));
            Log.d("The searched keyword is " + keyword);
            titleTV.setText(getString(R.string.search_result, keyword));
            new WordSearchTask(keyword).execute();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }

    public class WordSearchTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pdia;
        private boolean gotException;
        private String errorMessage;
        private String wordsToSearch;
        private LayoutInflater mInflater;
        private GoogleSearchModel searchModel;
        private Items[] itemsArray;

        public WordSearchTask(String keyword) {

            wordsToSearch = keyword;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pdia.isShowing())
                pdia.dismiss();

            if (gotException) {
                AutoSearchActivity.showAlert(SearchResultActivity.this,
                        errorMessage);
                return;
            }

            if (searchModel == null) {
                return;
            }

            String time = searchModel.getSearchInformation()
                    .getFormattedSearchTime();

            int numberOfResult = Integer.parseInt(searchModel
                    .getSearchInformation().getTotalResults());

            if (numberOfResult <= 0) {

                Log.d("No result to see in search response ");
                searchDetailsTV.setVisibility(View.VISIBLE);
                searchDetailsTV.setText(getString(R.string.no_result));
                return;
            }

            searchDetailsTV.setText(getString(R.string.about_result,
                    numberOfResult, time));

            searchDetailsTV.setVisibility(View.VISIBLE);

            itemsArray = searchModel.getItems();

            mInflater = SearchResultActivity.this.getLayoutInflater();

            resultLV.setAdapter(new ListViewAdapter(SearchResultActivity.this,
                    mInflater, itemsArray, R.layout.list_item,
                    SearchResultActivity.this));

            return;
        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pdia = new ProgressDialog(SearchResultActivity.this);
            pdia.setTitle("Please wait");
            pdia.setMessage("While Loading ...");
            pdia.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Log.d("Its done now");

            ConnectionManager.createInstance().customSearch(wordsToSearch,
                    new APIResponseHandler<GoogleSearchModel>() {

                        @Override
                        public void onResponseReceived(
                                GoogleSearchModel response) {

                            searchModel = response;
                            Log.d("Finally got the response from Google "
                                    + response.getItems());
                        }

                        @Override
                        public void onExceptionReceived(String exceptionMessage) {
                            gotException = true;
                            errorMessage = exceptionMessage;
                        }
                    });

            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void startWebViewActivity(String link) {
        Intent intent = new Intent(getApplicationContext(), WebActivity.class);
        intent.putExtra(KEY_LINK_URL, link);
        startActivity(intent);
    }

    @Override
    public void getResult(String item) {
        startWebViewActivity(item);
    }

}
