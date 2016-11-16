package de.mvhs.android.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kurs on 19.10.2016.
 */

public class DbHelper extends SQLiteOpenHelper {
    private final static String _DB_NAME = "zeiten.db";
    private final static int _DB_VERSION = 3;


    public DbHelper(Context context) {
        super(context, _DB_NAME, null, _DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TimeTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TimeTable.onUpgrade(db, oldVersion, newVersion);
    }
}
