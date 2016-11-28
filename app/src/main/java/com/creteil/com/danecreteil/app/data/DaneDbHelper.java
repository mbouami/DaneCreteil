package com.creteil.com.danecreteil.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.creteil.com.danecreteil.app.data.DaneContract.UserEntry;
import com.creteil.com.danecreteil.app.data.DaneContract.VilleEntry;
import com.creteil.com.danecreteil.app.data.DaneContract.EtablissementEntry;

/**
 * Created by Mohammed on 26/11/2016.
 */

public class DaneDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "danecreteil.db";

    DaneDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_USERS_TABLE = "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                UserEntry._ID + " INTEGER PRIMARY KEY," +
                UserEntry.COLUMN_LOGIN + " TEXT UNIQUE NOT NULL, " +
                UserEntry.COLUMN_MDP + " TEXT NOT NULL, " +
                UserEntry.COLUMN_KEY + " TEXT NOT NULL " +
                " );";

        final String SQL_CREATE_VILLES_TABLE = "CREATE TABLE " + VilleEntry.TABLE_NAME + " (" +
                VilleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VilleEntry.COLUMN_NOM + " TEXT NOT NULL, " +
                VilleEntry.COLUMN_CODE_DEPARTEMENT + " TEXT NOT NULL, " +
                VilleEntry.COLUMN_NOM_DEPARTEMENT + " TEXT NOT NULL, " +
                VilleEntry.COLUMN_VILLE_BASE_ID + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_ETABLISSEMENTS_TABLE = "CREATE TABLE " + EtablissementEntry.TABLE_NAME + " (" +
                EtablissementEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EtablissementEntry.COLUMN_NOM + " TEXT NOT NULL, " +
                EtablissementEntry.COLUMN_ETABLISSEMENT_ID + " TEXT NOT NULL, " +
                EtablissementEntry.COLUMN_VILLE_ID + " TEXT NOT NULL, " +
                " FOREIGN KEY (" + EtablissementEntry.COLUMN_VILLE_ID + ") REFERENCES " +
                VilleEntry.TABLE_NAME + " (" + VilleEntry._ID + ") " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_USERS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VILLES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ETABLISSEMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VilleEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + EtablissementEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
