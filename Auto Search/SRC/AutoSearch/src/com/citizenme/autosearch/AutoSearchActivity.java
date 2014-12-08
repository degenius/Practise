package com.citizenme.autosearch;

import java.io.IOException;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.citizenme.autosearch.DictionaryDB.DictionaryDBOpenHelper;

public class AutoSearchActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_search);
        ActionBar actionBar = getActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Dictionary file is too large and If we load whole dictionary
        // in main memory, It may cause Out of memory on most of the lower
        // end devices, So we have already created a DB ( shipped with the app
        // i.e.
        // asset folder )
        // Now we would be copying the existing db to our app
        // Later on in the same fashion, DB upgradation can be done.
        // Still we are using async task, If It takes time in copying DB
        new dbLoaderAsyncTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.auto_search, menu);
        performSearchOption(menu);

        return true;
    }

    private void performSearchOption(Menu menu) {

        SearchManager searchManager = (SearchManager) this
                .getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();

        SearchableInfo info = searchManager.getSearchableInfo(this
                .getComponentName());
        if (null != searchManager) {
            searchView.setSearchableInfo(info);
        }
    }

    public class dbLoaderAsyncTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pdia;
        private boolean gotException;
        private String errorMessage;;

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pdia.isShowing())
                pdia.dismiss();

            if (gotException) {
                showAlert(AutoSearchActivity.this, errorMessage);
                return;
            }

        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pdia = new ProgressDialog(AutoSearchActivity.this);
            pdia.setTitle("Please wait");
            pdia.setMessage("While Loading ...");
            pdia.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            DictionaryDBOpenHelper mDictionaryDBOpenHelper = new DictionaryDB(
                    AutoSearchActivity.this).new DictionaryDBOpenHelper(
                    AutoSearchActivity.this, DictionaryDB.DBNAME, null,
                    DictionaryDB.VERSION);
            try {
                mDictionaryDBOpenHelper.createDataBase();
                Log.d("DB has been created and and It's time to close it ");
                mDictionaryDBOpenHelper.close();
            } catch (IOException e) {
                Log.e("Got exception in DB creation " + e.getMessage());
                gotException = true;
                errorMessage = e.getMessage();
            }

            Log.d("Its done now");
            return null;
        }
    }

    static void showAlert(Context context, String message) {
        AlertDialog mPopup;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Something Went Wrong");
        builder.setMessage(message);
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mPopup = builder.create();
        mPopup.show();
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

}
