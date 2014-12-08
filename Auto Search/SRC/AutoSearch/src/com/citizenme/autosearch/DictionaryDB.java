package com.citizenme.autosearch;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

public class DictionaryDB {

    public static final String DBNAME = "dictionary";

    public static final int VERSION = 1;

    private DictionaryDBOpenHelper mDictionaryDBOpenHelper;

    private static final String FIELD_ID = "_id";
    private static final String FIELD_NAME = "name";
    private static final String TABLE_NAME = "words";

    // The Android's default system path of your application database.
    //TODO: Hard coded as of now
    private static String DB_PATH = "/data/data/com.citizenme.autosearch/databases/";

    private HashMap<String, String> mAliasMap;

    public DictionaryDB(Context context) {

        mDictionaryDBOpenHelper = new DictionaryDBOpenHelper(context, DBNAME,
                null, VERSION);

        // This HashMap is used to map table fields to Custom Suggestion fields
        mAliasMap = new HashMap<String, String>();

        // Unique id for the each Suggestions ( Mandatory )
        mAliasMap.put("_ID", FIELD_ID + " as " + "_id");

        // Text for Suggestions ( Mandatory )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, FIELD_NAME + " as "
                + SearchManager.SUGGEST_COLUMN_TEXT_1);

        // This value will be appended to the Intent data on selecting an item
        // from Search result or Suggestions ( Optional )
        mAliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, FIELD_ID
                + " as " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    public Cursor getWords(String[] selectionArgs) {

        Log.d("Inside " + DictionaryDB.class.getName() + "$" + "getWords");

        String selection = FIELD_NAME + " like ? ";

        if (selectionArgs != null) {
            selectionArgs[0] = "%" + selectionArgs[0] + "%";
        }

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setProjectionMap(mAliasMap);

        queryBuilder.setTables(TABLE_NAME);

        Cursor c = queryBuilder.query(
                mDictionaryDBOpenHelper.getReadableDatabase(), new String[] {
                        "_ID", SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID },
                selection, selectionArgs, null, null, FIELD_NAME + " asc ",
                "10");

        return c;

    }

    public Cursor getWordByID(String id) {

        Log.d("Inside " + DictionaryDB.class.getName() + "$" + "getWords");
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        queryBuilder.setTables(TABLE_NAME);

        Cursor c = queryBuilder.query(
                mDictionaryDBOpenHelper.getReadableDatabase(), new String[] {
                        "_id", "name" }, "_id = ?", new String[] { id }, null,
                null, null, "1");

        return c;
    }

    public void closeDb() {

        mDictionaryDBOpenHelper.close();
    }

    class DictionaryDBOpenHelper extends SQLiteOpenHelper {

        private Context mContext;
        private SQLiteDatabase myDataBase;

        public DictionaryDBOpenHelper(Context context, String name,
                CursorFactory factory, int version) {

            super(context, DBNAME, factory, VERSION);
            mContext = context;
        }

        /**
         * Creates a empty database on the system and rewrites it with your own
         * database.
         * */
        public void createDataBase() throws IOException {

            Log.d("Inside "
                    + DictionaryDB.DictionaryDBOpenHelper.class.getName() + "$"
                    + "createDataBase");

            boolean dbExist = checkDataBase();

            if (dbExist) {
                // do nothing - database already exist
                Log.d("DB already exist");
            } else {

                // By calling this method and empty database will be created
                // into the default system path
                // of your application so we are gonna be able to overwrite that
                // database with our database.
                this.getReadableDatabase();

                Log.d("empty db has been created ");

                try {

                    copyDataBase();

                } catch (IOException e) {

                    throw new Error("Error copying database");

                }
            }

        }

        /**
         * Check if the database already exist to avoid re-copying the file each
         * time you open the application.
         * 
         * @return true if it exists, false if it doesn't
         */
        private boolean checkDataBase() {

            SQLiteDatabase checkDB = null;

            try {
                String myPath = DB_PATH + DBNAME;
                checkDB = SQLiteDatabase.openDatabase(myPath, null,
                        SQLiteDatabase.OPEN_READONLY);

            } catch (SQLiteException e) {

                // database does't exist yet.

            }

            if (checkDB != null) {

                checkDB.close();

            }

            return checkDB != null ? true : false;
        }

        private void copyDataBase() throws IOException {

            Log.d("Inside "
                    + DictionaryDB.DictionaryDBOpenHelper.class.getName() + "$"
                    + "copyDataBase");

            // Open your local db as the input stream
            InputStream myInput = mContext.getAssets().open(DBNAME);

            // Path to the just created empty db
            String outFileName = DB_PATH + DBNAME;

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            Log.d("DB has beeb copied ");

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();

        }

        public void openDataBase() throws SQLException {

            // Open the database
            String myPath = DB_PATH + DBNAME;
            myDataBase = SQLiteDatabase.openDatabase(myPath, null,
                    SQLiteDatabase.OPEN_READONLY);

        }

        @Override
        public synchronized void close() {

            if (myDataBase != null)
                myDataBase.close();

            super.close();

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("Inside "
                    + DictionaryDB.DictionaryDBOpenHelper.class.getName() + "$"
                    + "onCreate");

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    }
}