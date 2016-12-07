package com.creteil.com.danecreteil.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Mohammed on 26/11/2016.
 */

public class DaneProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private DaneDbHelper mDaneHelper;

    static final int VILLES = 100;
    static final int VILLES_PAR_DEPARTEMENT = 101;

    static final int ETABLISSEMENTS = 300;
    static final int ETABLISSEMENTS_PAR_VILLE = 301;
    static final int ETABLISSEMENTS_ID = 302;

    static final int PERSONNEL = 400;
    static final int PERSONNEL_PAR_ETAB = 401;
    static final int PERSONNEL_PAR_ID = 402;

    private static final SQLiteQueryBuilder sEtablissementsParVilleQueryBuilder;
    static{
        sEtablissementsParVilleQueryBuilder = new SQLiteQueryBuilder();


        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sEtablissementsParVilleQueryBuilder.setTables(
                DaneContract.EtablissementEntry.TABLE_NAME + " INNER JOIN " +
                        DaneContract.VilleEntry.TABLE_NAME +
                        " ON " + DaneContract.EtablissementEntry.TABLE_NAME +
                        "." + DaneContract.EtablissementEntry.COLUMN_VILLE_ID +
                        " = " + DaneContract.VilleEntry.TABLE_NAME +
                        "." + DaneContract.VilleEntry._ID);
    }

    private static final SQLiteQueryBuilder sPersonnelParEtablissamentQueryBuilder;
    static{
        sPersonnelParEtablissamentQueryBuilder = new SQLiteQueryBuilder();


        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
//        sPersonnelParEtablissamentQueryBuilder.setTables(
//                DaneContract.PersonnelEntry.TABLE_NAME + " INNER JOIN " +
//                        DaneContract.EtablissementEntry.TABLE_NAME +
//                        " ON " + DaneContract.PersonnelEntry.TABLE_NAME +
//                        "." + DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID +
//                        " = " + DaneContract.EtablissementEntry.TABLE_NAME +
//                        "." + DaneContract.EtablissementEntry._ID);
        sPersonnelParEtablissamentQueryBuilder.setTables(
                DaneContract.PersonnelEntry.TABLE_NAME + " INNER JOIN " +
                        DaneContract.EtablissementEntry.TABLE_NAME +
                        " ON " + DaneContract.PersonnelEntry.TABLE_NAME +
                        "." + DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID +
                        " = " + DaneContract.EtablissementEntry.TABLE_NAME +
                        "." + DaneContract.EtablissementEntry._ID + " INNER JOIN " +
                        DaneContract.VilleEntry.TABLE_NAME +
                        " ON " + DaneContract.EtablissementEntry.TABLE_NAME +
                        "." + DaneContract.EtablissementEntry.COLUMN_VILLE_ID +
                        " = " + DaneContract.VilleEntry.TABLE_NAME +
                        "." + DaneContract.VilleEntry._ID);
    }
   private static final SQLiteQueryBuilder sVillesParDepartementQueryBuilder;
   static{
        sVillesParDepartementQueryBuilder = new SQLiteQueryBuilder();
        sVillesParDepartementQueryBuilder.setTables(DaneContract.VilleEntry.TABLE_NAME);
    }

    private static final SQLiteQueryBuilder sEtablissementParIdQueryBuilder;
    static{
        sEtablissementParIdQueryBuilder = new SQLiteQueryBuilder();
        sEtablissementParIdQueryBuilder.setTables(DaneContract.EtablissementEntry.TABLE_NAME);
    }

    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DaneContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, DaneContract.PATH_VILLES, VILLES);
        matcher.addURI(authority, DaneContract.PATH_VILLES+ "/*", VILLES_PAR_DEPARTEMENT);

        matcher.addURI(authority, DaneContract.PATH_ETABLISSEMENTS, ETABLISSEMENTS);
        matcher.addURI(authority, DaneContract.PATH_ETABLISSEMENTS+ "/*", ETABLISSEMENTS_PAR_VILLE);
        matcher.addURI(authority, DaneContract.PATH_ETABLISSEMENTS+ "/*/etab", ETABLISSEMENTS_ID);

        matcher.addURI(authority, DaneContract.PATH_PERSONNEL, PERSONNEL);
        matcher.addURI(authority, DaneContract.PATH_PERSONNEL+ "/*", PERSONNEL_PAR_ID);
        matcher.addURI(authority, DaneContract.PATH_PERSONNEL+ "/*/etab", PERSONNEL_PAR_ETAB);
        return matcher;
    }

    //location.location_setting = ?
    private static final String sVillesParDepartementSelection =
            DaneContract.VilleEntry.TABLE_NAME+
                    "." + DaneContract.VilleEntry.COLUMN_VILLE_DEPARTEMENT + " = ? ";

    private static final String sEtablissementParIdSelection =
            DaneContract.EtablissementEntry.TABLE_NAME+
                    "." + DaneContract.EtablissementEntry._ID + " = ? ";

    private static final String sPersonnelParEtabSelection =
            DaneContract.PersonnelEntry.TABLE_NAME+
                    "." + DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID + " = ? ";

    private Cursor getVillesParDepartement(Uri uri, String[] projection, String sortOrder) {
        String departement = DaneContract.VilleEntry.getDepartementFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{departement};
        selection = sVillesParDepartementSelection;
        return sVillesParDepartementQueryBuilder.query(mDaneHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private static final String sEtablissementsParVilleSelection =
            DaneContract.EtablissementEntry.TABLE_NAME+
                    "." + DaneContract.EtablissementEntry.COLUMN_VILLE_ID + " = ? ";


    private Cursor getEtablissementsParVille(Uri uri, String[] projection, String sortOrder) {
//        String ville = DaneContract.EtablissementEntry.getVilleFromUri(uri);
        String ville = DaneContract.EtablissementEntry.getVilleFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{ville};
        selection = sEtablissementsParVilleSelection;
        return sEtablissementsParVilleQueryBuilder.query(mDaneHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getEtablissementsParId(Uri uri, String[] projection, String sortOrder) {
        String idetab = DaneContract.EtablissementEntry.getEtablissementFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{idetab};
        selection = sEtablissementParIdSelection;
        return sEtablissementsParVilleQueryBuilder.query(mDaneHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getPersonnelParEtab(Uri uri, String[] projection, String sortOrder) {
        String idetab = DaneContract.PersonnelEntry.getEtablissementFromUri(uri);
        String[] selectionArgs;
        String selection;
        selectionArgs = new String[]{idetab};
        selection = sPersonnelParEtabSelection;
        return sPersonnelParEtablissamentQueryBuilder.query(mDaneHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mDaneHelper = new DaneDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // Student: Uncomment and fill out these two cases
            case VILLES_PAR_DEPARTEMENT:
                return DaneContract.VilleEntry.CONTENT_ITEM_TYPE;
            case VILLES:
                return DaneContract.VilleEntry.CONTENT_TYPE;
            case ETABLISSEMENTS:
                return DaneContract.EtablissementEntry.CONTENT_TYPE;
            case ETABLISSEMENTS_PAR_VILLE:
                return DaneContract.EtablissementEntry.CONTENT_ITEM_TYPE;
            case ETABLISSEMENTS_ID:
                return DaneContract.EtablissementEntry.CONTENT_ITEM_TYPE;
            case PERSONNEL:
                return DaneContract.PersonnelEntry.CONTENT_TYPE;
            case PERSONNEL_PAR_ID:
                return DaneContract.PersonnelEntry.CONTENT_ITEM_TYPE;
            case PERSONNEL_PAR_ETAB:
                return DaneContract.PersonnelEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("uri inconnue: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case VILLES_PAR_DEPARTEMENT:
            {
                retCursor = getVillesParDepartement(uri, projection, sortOrder);
                break;
            }
            case ETABLISSEMENTS_PAR_VILLE: {
                retCursor = getEtablissementsParVille(uri, projection, sortOrder);
                break;
            }
            case ETABLISSEMENTS_ID: {
                retCursor = getEtablissementsParId(uri, projection, sortOrder);
                break;
            }
            case PERSONNEL_PAR_ETAB: {
                retCursor = getPersonnelParEtab(uri, projection, sortOrder);
                break;
            }
            case VILLES: {
                retCursor = mDaneHelper.getReadableDatabase().query(
                        DaneContract.VilleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PERSONNEL: {
                retCursor = mDaneHelper.getReadableDatabase().query(
                        DaneContract.PersonnelEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ETABLISSEMENTS: {
                retCursor = mDaneHelper.getReadableDatabase().query(
                        DaneContract.EtablissementEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            }

            default:
                throw new UnsupportedOperationException("uri inconnue: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }
    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mDaneHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case VILLES: {
                long _id = db.insert(DaneContract.VilleEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = DaneContract.VilleEntry.buildVilleUri(_id);
                else
                    throw new android.database.SQLException("Erreur lors de l'ajout de la ville " + uri);
                break;
            }
            case ETABLISSEMENTS: {
                long _id = db.insert(DaneContract.EtablissementEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = DaneContract.EtablissementEntry.buildEtablissementUri(_id);
                else
                    throw new android.database.SQLException("Erreur lors de l'ajout de l'établissement " + uri);
                break;
            }
            case PERSONNEL: {
                long _id = db.insert(DaneContract.PersonnelEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = DaneContract.PersonnelEntry.buildPersonnelUri(_id);
                else
                    throw new android.database.SQLException("Erreur lors de l'ajout de l'établissement " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("uri inconnue: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDaneHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case VILLES:
                rowsDeleted = db.delete(
                        DaneContract.VilleEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ETABLISSEMENTS:
                rowsDeleted = db.delete(
                        DaneContract.EtablissementEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PERSONNEL:
                rowsDeleted = db.delete(
                        DaneContract.PersonnelEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("uri inconnue: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mDaneHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case VILLES:
                rowsUpdated = db.update(DaneContract.VilleEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case ETABLISSEMENTS:
                rowsUpdated = db.update(DaneContract.EtablissementEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            case PERSONNEL:
                rowsUpdated = db.update(DaneContract.PersonnelEntry.TABLE_NAME, contentValues, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("uri inconnue: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contentValues) {
        final SQLiteDatabase db = mDaneHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case VILLES:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : contentValues) {
                        long _id = db.insert(DaneContract.VilleEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case ETABLISSEMENTS:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : contentValues) {
                        long _id = db.insert(DaneContract.EtablissementEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            case PERSONNEL:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : contentValues) {
                        long _id = db.insert(DaneContract.PersonnelEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;

            default:
                return super.bulkInsert(uri, contentValues);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
//    @Override
//    @TargetApi(11)
//    public void shutdown() {
//        mOpenHelper.close();
//        super.shutdown();
//    }
}
