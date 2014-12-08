package com.citizenme.autosearch;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class DictionaryContentProvider extends ContentProvider {
	
	 public static final String AUTHORITY = "com.citizenme.autosearch.DictionaryContentProvider";
	 public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/words" );

     DictionaryDB mCountryDB = null;

     private static final int SUGGESTIONS_WORDS = 1;
     private static final int SEARCH_WORDS = 2;
     private static final int GET_WORDS = 3;
     
     UriMatcher mUriMatcher = buildUriMatcher();
     
     private UriMatcher buildUriMatcher(){
    	 UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);    	 
    	 
    	 uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY,SUGGESTIONS_WORDS);
    	 
    	 
    	 uriMatcher.addURI(AUTHORITY, "words", SEARCH_WORDS);
    	 
    	 // This URI is invoked, when user selects a suggestion from search dialog or an item from the listview
    	 // Country details for CountryActivity is provided by this uri    	 
    	 // See, SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID in CountryDB.java
    	 uriMatcher.addURI(AUTHORITY, "words/#", GET_WORDS);
    	 
    	 return uriMatcher;
     }
     
     
     @Override
	 public boolean onCreate() {
         Log.d("Its time to create DB");
		 	mCountryDB = new DictionaryDB(getContext());
		 	return true;
	 }
     
     @Override
	 public Cursor query(Uri uri, String[] projection, String selection,
			 String[] selectionArgs, String sortOrder) {
    	 
    	 Cursor c = null;
    	 switch(mUriMatcher.match(uri)){
    	 
    	 case SUGGESTIONS_WORDS :
    		 c = mCountryDB.getWords(selectionArgs);
    		 break;
    	 case SEARCH_WORDS :
    		 c = mCountryDB.getWords(selectionArgs);
    		 break;
    	 case GET_WORDS :
    		 String id = uri.getLastPathSegment();
    		 c = mCountryDB.getWordByID(id);    		
    	 }

    	 return c;
    	 
	}     

	 @Override
	 public int delete(Uri uri, String selection, String[] selectionArgs) {
		 	throw new UnsupportedOperationException();
	 }

	 @Override
	 public String getType(Uri uri) {
		 	throw new UnsupportedOperationException();
	 }

	 @Override
	 public Uri insert(Uri uri, ContentValues values) {
		 	throw new UnsupportedOperationException();
	 }	 
	 

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
	 		throw new UnsupportedOperationException();
	}
}