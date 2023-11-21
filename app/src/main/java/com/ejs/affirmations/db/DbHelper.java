package com.ejs.affirmations.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DbHelper extends SQLiteOpenHelper {
    private final String TAG = this.getClass().getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Affirmations.db";
    public static final String TABLE_dormir = "dormir";
    public static final String TABLE_manana = "manana";
    public static final String TABLE_riqueza = "riqueza";
    public static final String TABLE_louisehay = "louisehay";
    public static final String TABLE_amorpropio = "amorpropio";
    public static final String TABLE_amor = "amor";
    public static final String TABLE_atencion = "atencion";
    public static final String TABLE_mente = "mente";
    public static final String TABLE_negocio = "negocio";
    public static final String TABLE_salud = "salud";

    public DbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_dormir + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_manana + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_riqueza + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_louisehay + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_amorpropio + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_amor + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_atencion + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_mente + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_negocio + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_salud + "(id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "afirmaciones TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_dormir);
        db.execSQL("DROP TABLE " + TABLE_manana);
        db.execSQL("DROP TABLE " + TABLE_riqueza);
        db.execSQL("DROP TABLE " + TABLE_louisehay);
        db.execSQL("DROP TABLE " + TABLE_amorpropio);
        db.execSQL("DROP TABLE " + TABLE_amor);
        db.execSQL("DROP TABLE " + TABLE_atencion);
        db.execSQL("DROP TABLE " + TABLE_mente);
        db.execSQL("DROP TABLE " + TABLE_negocio);
        db.execSQL("DROP TABLE " + TABLE_salud);
        onCreate(db);
    }
    public String getRandomAffirmations(String TABLE_NAME, SQLiteDatabase db) {
        String query = "SELECT * FROM " + TABLE_NAME + " ORDER BY Random() LIMIT 1";
        Cursor c = db.rawQuery(query, null);

        String afirmaciones = null;
        if (c != null) {
            c.moveToFirst();
            do {
                //Asignamos el valor en nuestras variables para usarlos en lo que necesitemos
                afirmaciones = c.getString(c.getColumnIndex("afirmaciones"));
            } while (c.moveToNext());
        }

        //Cerramos el cursor y la conexion con la base de datos
        c.close();
        //db.close();
        return afirmaciones;
        //return getReadableDatabase().query(TABLE_NAME,null,null,null,null,null,null);
    }
    /**
     * This reads a file from the given Resource-Id and calls every line of it as a SQL-Statement
     *
     * @param context
     *
     * @param resourceId
     *  e.g. R.raw.food_db
     *
     * @return Number of SQL-Statements run
     * @throws IOException
     */
    public static int insertFromFile(Context context, int resourceId, SQLiteDatabase db) throws IOException {
        // Reseting Counter
        int result = 0;

        // Open the resource
        InputStream insertsStream = context.getResources().openRawResource(resourceId);
        BufferedReader insertReader = new BufferedReader(new InputStreamReader(insertsStream));

        // Iterate through lines (assuming each insert has its own line and theres no other stuff)
        while (insertReader.ready()) {
            String insertStmt = insertReader.readLine();
            //Log.e(TAG, "sql "+ insertStmt);
            db.execSQL(insertStmt);
            result++;
        }
        insertReader.close();

        // returning number of inserted rows
        return result;
    }
    //Get Row Count
    public int getCount() {
        String countQuery = "SELECT  * FROM dormir";
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if(!cursor.isClosed()){
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    //Delete Query
    public void removeFav(int id, String TABLE_TEST) {
        String countQuery = "DELETE FROM " + TABLE_TEST + " where id= " + id ;
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(countQuery);
    }

}
