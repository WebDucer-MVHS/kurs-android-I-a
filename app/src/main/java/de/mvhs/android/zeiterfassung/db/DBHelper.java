package de.mvhs.android.zeiterfassung.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kurs on 25.03.15.
 */
public class DBHelper extends SQLiteOpenHelper {
    // Klassenvariablen
    // - Name der Datenbank-Datei
    private final static String _DB_NAME = "Zeiten.db";
    // - Benötigte Version der Datenbank
    private final static int _DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, _DB_NAME, null, _DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        ZeitenTable.onCreateTable(db); // Erstellen der Tabelle in der Datenbank
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ZeitenTable.onUpdateTable(db, oldVersion, newVersion); // Aktualisierung der Tabelle in der Datenbank
    }
}
