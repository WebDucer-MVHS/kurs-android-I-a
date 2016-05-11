package db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kurs on 13.04.16.
 */
public class DbHelper extends SQLiteOpenHelper {
    /**
     * Dateiname der SQLite Datenbank
     */
    private static final String _DB_NAME = "timelog.db";
    /**
     * Datenbankversion, die zum Arbeiten der App benötigt wird
     */
    private static final int _DB_VERSION = 2;

    public DbHelper(Context context) {
        super(context, _DB_NAME, null, _DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        TimelogTable.createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        TimelogTable.upgradeTable(db, oldVersion, newVersion);
    }
}
