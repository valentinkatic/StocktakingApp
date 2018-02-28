package eu.fiskaljdoo.stocktaking.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import eu.fiskaljdoo.stocktaking.Constants;
import eu.fiskaljdoo.stocktaking.R;
import eu.fiskaljdoo.stocktaking.models.Article;
import eu.fiskaljdoo.stocktaking.models.Result;

/**
 * Created by Valentin on 27.2.2018..
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";

    private SQLiteDatabase db;
    private Context context;

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "stocktaking";

    // Main Table Name
    private static final String TABLE_ARTICLES = "articles";
    private static final String TABLE_RESULTS = "results";

    // Table Columns names TABLE_ARTICLES
    private static final String KEY_ARTICLE_CODE = "article_code";
    private static final String KEY_NAME = "name";

    // Table Columns names TABLE_RESULTS
    private static final String KEY_RESULT_ID = "id";
    private static final String KEY_INVENTURE_NUMBER = "inventure_number";
    private static final String KEY_RELATION_ARTICLE_CODE = KEY_ARTICLE_CODE;
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_DATE = "date";
    private static final String KEY_USER = "user";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTableArticles(sqLiteDatabase);
        createTableResults(sqLiteDatabase);
    }

    private void createTableArticles(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ARTICLES + " ("
                + KEY_ARTICLE_CODE + " TEXT PRIMARY KEY, "
                + KEY_NAME + " TEXT "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    private void createTableResults(SQLiteDatabase db){
        String CREATE_TABLE = "CREATE TABLE " + TABLE_RESULTS + " ("
                + KEY_RESULT_ID + " INTEGER PRIMARY KEY , "
                + KEY_INVENTURE_NUMBER + " INT, "
                + KEY_RELATION_ARTICLE_CODE + " TEXT, "
                + KEY_AMOUNT + " REAL, "
                + KEY_DATE + " TEXT, "
                + KEY_USER + " TEXT "
                + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d("DB ", "onUpgrade " + oldVersion + " to " + newVersion);
        if(oldVersion < newVersion) {
            // Drop older table if existed
            truncateDB(db);
        }
    }

    public void truncateDB(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Insert List article
    public void insertListArticle(List<Article> modelList) {
        for (Article a : modelList) {
            ContentValues values = getArticleValue(a);
            // Inserting or Update Row
            db.insertWithOnConflict(TABLE_ARTICLES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Update one article
    public Article updateArticle(Article article) {
        List<Article> objcs = new ArrayList<>();
        objcs.add(article);
        insertListArticle(objcs);
        if(isArticleExist(article.getCode())){
            return getArticle(article.getCode());
        }
        return null;
    }

    private ContentValues getArticleValue(Article model){
        ContentValues values = new ContentValues();
        values.put(KEY_ARTICLE_CODE, model.getCode());
        values.put(KEY_NAME, model.getName());
        return values;
    }

    public Article getArticle(String article_id) {
        Article a = new Article();
        String query = "SELECT * FROM " + TABLE_ARTICLES + " a WHERE a." + KEY_ARTICLE_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{article_id+""});
        a.setCode(article_id);
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            a = getArticleByCursor(cursor);
        }
        return a;
    }

    private Article getArticleByCursor(Cursor cur){
        return new Article(
                cur.getString(cur.getColumnIndex(KEY_ARTICLE_CODE)),
                cur.getString(cur.getColumnIndex(KEY_NAME))
        );
    }

    // Insert List results
    public void insertListResult(List<Result> modelList) {
        for (Result r : modelList) {
            ContentValues values = getResultValue(r);
            // Inserting or Update Row
            db.insertWithOnConflict(TABLE_RESULTS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    // Update one result
    public Result updateResult(Result result) {
        updateArticle(result.getArticle());
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(context.getString(R.string.ISO_8601_PATTERN_1), Locale.getDefault());
        result.setDate(sdf.format(c.getTime()));
        Log.d(TAG, "resultID: " + result.getId());
        if (result.getId()==0){
            result.setId(getLastResult().getId() + 1);
        }

        List<Result> objcs = new ArrayList<>();
        objcs.add(result);
        insertListResult(objcs);
        if(isResultExist(result.getId())){
            return getResult(result.getArticle().getCode());
        }
        return null;
    }

    private List<Result> getListResultByCursor(Cursor cur) {
        List<Result> resList = new ArrayList<>();
        // looping through all rows and adding to list
        if (cur.moveToFirst()) {
            do {
                // Adding place to list
                resList.add(getResultByCursor(cur));
            } while (cur.moveToNext());
        }
        return resList;
    }

    private List<Integer> getListInventureNumberByCursor(Cursor cur) {
        List<Integer> list = new ArrayList<>();
        // looping through all rows and adding to list
        if (cur.moveToFirst()) {
            do {
                // Adding place to list
                list.add(cur.getInt(cur.getColumnIndex(KEY_INVENTURE_NUMBER)));
            } while (cur.moveToNext());
        }
        return list;
    }

    private ContentValues getResultValue(Result model){
        ContentValues values = new ContentValues();
        values.put(KEY_RESULT_ID, model.getId());
        values.put(KEY_INVENTURE_NUMBER, model.getInventureNumber());
        values.put(KEY_RELATION_ARTICLE_CODE, model.getArticle().getCode());
        values.put(KEY_AMOUNT, model.getAmount());
        values.put(KEY_DATE, model.getDate());
        values.put(KEY_USER, model.getUser());
        return values;
    }

    public Result getResult(String article_id) {
        Result r = new Result();
        String query = "SELECT * FROM " + TABLE_RESULTS + " r WHERE r." + KEY_RELATION_ARTICLE_CODE + " = ? AND r." + KEY_INVENTURE_NUMBER + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{article_id+"", Constants.newInventureStarted ? Constants.inventureNumber+1+"" : Constants.inventureNumber+""});
        r.setArticle(getArticle(article_id));
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            r = getResultByCursor(cursor);
        }
        return r;
    }

    public Result getLastResult(){
        Result r = new Result();
        String query = "SELECT * FROM " + TABLE_RESULTS + " ORDER BY " + KEY_INVENTURE_NUMBER + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            r = getResultByCursor(cursor);
        }
        return r;
    }

    public String getLastResultChangeDate(){
        Result r = new Result();
        String query = "SELECT * FROM " + TABLE_RESULTS + " ORDER BY " + KEY_DATE + " DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, new String[]{});
        if (cursor.moveToFirst()) {
            cursor.moveToFirst();
            r = getResultByCursor(cursor);
        }
        return r.getDate();
    }

    public List<Integer> getInventureNumbers(){
        List<Integer> list;
        Cursor cursor = db.rawQuery("SELECT DISTINCT " + KEY_INVENTURE_NUMBER + " FROM " + TABLE_RESULTS, null);
        list = getListInventureNumberByCursor(cursor);
        return list;
    }

    private Result getResultByCursor(Cursor cur){
        return new Result(
                cur.getInt(cur.getColumnIndex(KEY_RESULT_ID)),
                cur.getInt(cur.getColumnIndex(KEY_INVENTURE_NUMBER)),
                getArticle(cur.getString(cur.getColumnIndex(KEY_RELATION_ARTICLE_CODE))),
                cur.getDouble(cur.getColumnIndex(KEY_AMOUNT)),
                cur.getString(cur.getColumnIndex(KEY_DATE)),
                cur.getString(cur.getColumnIndex(KEY_USER))
        );
    }

    // all Results
    public List<Result> getInventureResults(int inventureNumber) {
        List<Result> resList;
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_RESULTS + " WHERE " + KEY_INVENTURE_NUMBER + " = " + inventureNumber, null);
        resList = getListResultByCursor(cursor);
        return resList;
    }

    private boolean isArticleExist(String id) {
        String query = "SELECT * FROM " + TABLE_ARTICLES + " WHERE " + KEY_ARTICLE_CODE + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isResultExist(int id) {
        String query = "SELECT * FROM " + TABLE_RESULTS + " WHERE " + KEY_RESULT_ID + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{id + ""});
        int count = cursor.getCount();
        cursor.close();
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    // to export database file
    // for debugging only
    public void exportDatabase(){
        try {
            File sd = Environment.getExternalStorageDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + context.getPackageName() + "/databases/"+ DATABASE_NAME;
                String backupDBPath = "backup_"+DATABASE_NAME+".db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
