package com.creteil.com.danecreteil.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.creteil.com.danecreteil.app.data.DaneContract.AnimateurEntry;
import com.creteil.com.danecreteil.app.data.DaneContract.DepartementEntry;
import com.creteil.com.danecreteil.app.data.DaneContract.VilleEntry;
import com.creteil.com.danecreteil.app.data.DaneContract.EtablissementEntry;
import com.creteil.com.danecreteil.app.data.DaneContract.PersonnelEntry;

/**
 * Created by Mohammed on 26/11/2016.
 */

public class DaneDbHelper extends SQLiteOpenHelper {
    private final String LOG_TAG = DaneDbHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 18;
    private static final String SQL_DELETE_DEPARTEMENTS_TABLE = "DROP TABLE IF EXISTS " + DepartementEntry.TABLE_NAME;
    private static final String SQL_DELETE_ANIMATEUR_TABLE = "DROP TABLE IF EXISTS " + AnimateurEntry.TABLE_NAME;
    private static final String SQL_DELETE_VILLE_TABLE = "DROP TABLE IF EXISTS " + VilleEntry.TABLE_NAME;
    private static final String SQL_DELETE_PERSONNEL_TABLE = "DROP TABLE IF EXISTS " + PersonnelEntry.TABLE_NAME;
    private static final String SQL_DELETE_ETABLISSEMENT_TABLE = "DROP TABLE IF EXISTS " + EtablissementEntry.TABLE_NAME;

    static final String DATABASE_NAME = "danecreteil.db";

    DaneDbHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_DEPARTEMENTS_TABLE = "CREATE TABLE " + DepartementEntry.TABLE_NAME + " (" +
                DepartementEntry._ID + " INTEGER PRIMARY KEY," +
                DepartementEntry.COLUMN_DEPARTEMENT_NOM + " TEXT NOT NULL, " +
                DepartementEntry.COLUMN_DEPARTEMENT_INTITULE + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_ANIMATEUR_TABLE = "CREATE TABLE " + AnimateurEntry.TABLE_NAME + " (" +
                AnimateurEntry._ID + " INTEGER PRIMARY KEY," +
                AnimateurEntry.COLUMN_NOM + " TEXT NOT NULL, " +
                AnimateurEntry.COLUMN_TEL + " TEXT NOT NULL, " +
                AnimateurEntry.COLUMN_EMAIL + " TEXT NOT NULL, " +
                AnimateurEntry.COLUMN_PHOTO + " BLOB, " +
                AnimateurEntry.COLUMN_DEPARTEMENT_ID + " INTEGER NOT NULL, " +
                AnimateurEntry.COLUMN_ANIMATEUR_ID + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_VILLES_TABLE = "CREATE TABLE " + VilleEntry.TABLE_NAME + " (" +
                VilleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                VilleEntry.COLUMN_VILLE_NOM + " TEXT NOT NULL, " +
                VilleEntry.COLUMN_DEPARTEMENT_ID + " INTEGER NOT NULL, " +
                VilleEntry.COLUMN_VILLE_ID + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_ETABLISSEMENTS_TABLE = "CREATE TABLE " + EtablissementEntry.TABLE_NAME + " (" +
                EtablissementEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                EtablissementEntry.COLUMN_NOM + " TEXT NOT NULL, " +
                EtablissementEntry.COLUMN_ETABLISSEMENT_ID + " INTEGER NOT NULL, " +
                EtablissementEntry.COLUMN_VILLE_ID + " INTEGER NOT NULL, " +
                EtablissementEntry.COLUMN_ANIMATEUR_ID + " INTEGER, " +
                EtablissementEntry.COLUMN_TEL + " TEXT, " +
                EtablissementEntry.COLUMN_FAX + " TEXT, " +
                EtablissementEntry.COLUMN_EMAIL + " TEXT, " +
                EtablissementEntry.COLUMN_RNE + " TEXT, " +
                EtablissementEntry.COLUMN_ADRESSE + " TEXT, " +
                EtablissementEntry.COLUMN_CP + " TEXT, " +
                EtablissementEntry.COLUMN_TYPE + " TEXT, " +
                " FOREIGN KEY (" + EtablissementEntry.COLUMN_VILLE_ID + ") REFERENCES " +
                VilleEntry.TABLE_NAME + " (" + VilleEntry._ID + ") " +
                " FOREIGN KEY (" + EtablissementEntry.COLUMN_ANIMATEUR_ID + ") REFERENCES " +
                AnimateurEntry.TABLE_NAME + " (" + AnimateurEntry._ID + ") " +
                " );";

        final String SQL_CREATE_PERSONNEL_TABLE = "CREATE TABLE " + PersonnelEntry.TABLE_NAME + " (" +
                PersonnelEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                PersonnelEntry.COLUMN_NOM + " TEXT NOT NULL, " +
                PersonnelEntry.COLUMN_STATUT + " TEXT NOT NULL, " +
                PersonnelEntry.COLUMN_ETABLISSEMENT_ID + " INTEGER NOT NULL, " +
                PersonnelEntry.COLUMN_PERSONNEL_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + PersonnelEntry.COLUMN_ETABLISSEMENT_ID + ") REFERENCES " +
                EtablissementEntry.TABLE_NAME + " (" + EtablissementEntry._ID + ") " +
                " );";
        sqLiteDatabase.execSQL(SQL_CREATE_DEPARTEMENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ANIMATEUR_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VILLES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ETABLISSEMENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PERSONNEL_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL(SQL_DELETE_ANIMATEUR_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_VILLE_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_DEPARTEMENTS_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_PERSONNEL_TABLE);
        sqLiteDatabase.execSQL(SQL_DELETE_ETABLISSEMENT_TABLE);
        onCreate(sqLiteDatabase);
    }
}
