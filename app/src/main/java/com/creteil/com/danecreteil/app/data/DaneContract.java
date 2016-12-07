package com.creteil.com.danecreteil.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mohammed on 26/11/2016.
 */

public class DaneContract {


    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.creteil.com.danecreteil.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not.  Don't be that dev, reader.  Don't be that dev.
    public static final String PATH_USERS = "users";
    public static final String PATH_VILLES = "villes";
    public static final String PATH_ETABLISSEMENTS = "etablissements";
    public static final String PATH_PERSONNEL = "personnel";

    public static final class UserEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_USERS;

        // Table name
        public static final String TABLE_NAME = "users";

        public static final String COLUMN_ID = "user_id";
        public static final String COLUMN_LOGIN = "login";
        public static final String COLUMN_MDP = "mdp";
        public static final String COLUMN_KEY = "cle";


        public static Uri buildVilleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class VilleEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_VILLES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VILLES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VILLES;

        // Table name
        public static final String TABLE_NAME = "villes";

        public static final String COLUMN_VILLE_ID = "ville_id";
        public static final String COLUMN_VILLE_NOM = "nom";
        public static final String COLUMN_VILLE_DEPARTEMENT = "departement";

        public static Uri buildVilleUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        public static Uri buildVilleParDepartement(String departement) {
            return CONTENT_URI.buildUpon().appendPath(departement).build();
        }


        public static String getDepartementFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class EtablissementEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ETABLISSEMENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ETABLISSEMENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ETABLISSEMENTS;

        public static final String TABLE_NAME = "etablissements";

        // Column with the foreign key into the location table.
        public static final String COLUMN_VILLE_ID = "ville_id";
        public static final String COLUMN_ETABLISSEMENT_ID = "etablissement_id";
        public static final String COLUMN_NOM = "nom";
        public static final String COLUMN_RNE = "rne";
        public static final String COLUMN_TEL = "tel";
        public static final String COLUMN_FAX = "fax";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_CP = "cp";
        public static final String COLUMN_ADRESSE = "adresse";
        public static final String COLUMN_TYPE = "type";

        public static Uri buildEtablissementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static Uri buildEtablissementParVille(String idville) {
            return CONTENT_URI.buildUpon().appendPath(idville).build();
        }

        public static Uri buildEtablissementParId(String idetab,String rubrique) {
            return CONTENT_URI.buildUpon().appendPath(idetab).appendPath(rubrique).build();
        }

        public static String getVilleFromUri(Uri uri) {

            return uri.getPathSegments().get(1);
        }

        public static String getEtablissementFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PersonnelEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PERSONNEL).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONNEL;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONNEL;

        public static final String TABLE_NAME = "personnel";

        // Column with the foreign key into the location table.
        public static final String COLUMN_PERSONNEL_ID = "personnel_id";
        public static final String COLUMN_ETABLISSEMENT_ID = "etablissement_id";
        public static final String COLUMN_NOM = "nom";
        public static final String COLUMN_STATUT = "statut";

        public static Uri buildPersonnelUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

//        public static String getVilleFromUri(Uri uri) {
//            return uri.getPathSegments().get(1);
//        }
//
        public static String getEtablissementFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static Uri buildPersonnelParIdEtab(String idetab,String rubrique) {
            return CONTENT_URI.buildUpon().appendPath(idetab).appendPath(rubrique).build();
        }
        public static String getPersonnelFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
