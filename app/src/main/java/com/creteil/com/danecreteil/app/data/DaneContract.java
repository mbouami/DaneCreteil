package com.creteil.com.danecreteil.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Mohammed on 26/11/2016.
 */

public class DaneContract {


//    public static final String BASE_URL ="http://danecreteil.bouami.fr";
    public static final String BASE_URL ="http://www.bouami.fr/danecreteil/web";
//    public static final String BASE_URL ="http://192.168.1.19:8080/danecreteil/web";
    public static final String BASE_URL_LISTE_DETAIL_VILLES = BASE_URL+"/listedetailvilles";
    public static final String BASE_URL_DETAIL_ETAB = BASE_URL+"/detailetab";
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
    public static final String PATH_ANIMATEURS = "animateurs";
    public static final String PATH_DEPARTEMENTS = "departements";
    public static final String PATH_VILLES = "villes";
    public static final String PATH_ETABLISSEMENTS = "etablissements";
    public static final String PATH_PERSONNEL = "personnel";

    public static final class DepartementEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_DEPARTEMENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEPARTEMENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEPARTEMENTS;

        // Table name
        public static final String TABLE_NAME = "departements";

        public static final String COLUMN_DEPARTEMENT_ID = "departement_id";
        public static final String COLUMN_DEPARTEMENT_NOM = "departement";
        public static final String COLUMN_DEPARTEMENT_INTITULE = "nom";

        public static Uri buildDepartement() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildDepartementParNom(String nomfepart) {
            return CONTENT_URI.buildUpon().appendPath(nomfepart).appendPath("nom").build();
        }
        public static Uri buildDepartementUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static String getDepartementFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class AnimateurEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ANIMATEURS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ANIMATEURS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ANIMATEURS;

        // Table name
        public static final String TABLE_NAME = "animateurs";

        public static final String COLUMN_ID = "animateur_id";
        public static final String COLUMN_NOM = "nom";
        public static final String COLUMN_TEL = "tel";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_ANIMATEUR_ID = "animateur_id";
        public static final String COLUMN_DEPARTEMENT_ID = "departement_id";


        public static Uri buildAnimateurUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildAnimateurs() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildEtabParIdAnimateur(String idanim,String rubrique) {
            return CONTENT_URI.buildUpon().appendPath(idanim).appendPath(rubrique).build();
        }

        public static String getNomAnimateurFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getIdAnimateurFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
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
        public static final String COLUMN_DEPARTEMENT_ID = "departement_id";

        public static Uri buildVille() {
            return CONTENT_URI.buildUpon().build();
        }

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
        public static final String COLUMN_ANIMATEUR_ID = "animateur_id";
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

        public static Uri buildEtablissements() {
            return CONTENT_URI.buildUpon().build();
        }

        public static Uri buildEtablissementParId(String idetab,String rubrique) {
            return CONTENT_URI.buildUpon().appendPath(idetab).appendPath(rubrique).build();
        }

        public static Uri buildEtablissementContenatLeNom(String nometab,String rubrique) {
            return CONTENT_URI.buildUpon().appendPath(nometab).appendPath(rubrique).build();
        }
        public static String getVilleFromUri(Uri uri) {

            return uri.getPathSegments().get(1);
        }

        public static String getEtablissementFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getNomEtablissementFromUri(Uri uri) {
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
        public static Uri buildPersonnel() {
            return CONTENT_URI.buildUpon().build();
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
