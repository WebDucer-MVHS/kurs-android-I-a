package de.mvhs.android.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kurs on 14.10.15.
 */
public class DbHelper extends SQLiteOpenHelper {
    // Klassenvariablen
    private static final String _DB_NAME = "zeiterfassung.db";
    private static final int _DB_VERSION = 2;

    public DbHelper(Context context) {
        super(context, _DB_NAME, null, _DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ZeitenTabelle.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ZeitenTabelle.upgradeTable(db, oldVersion);
    }
}
