package com.creteil.com.danecreteil.app.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Mohammed on 26/11/2016.
 */

public class DaneContract {

    public static final String BASE_URL ="http://www.bouami.fr/danecreteil/web";
//    public static final String BASE_URL ="http://192.168.1.19:8080/danecreteil/web";
//    public static final String BASE_URL ="http://192.168.1.12/danecreteil/web";
    public static final String BASE_URL_LISTE_DETAIL_VILLES = BASE_URL+"/listedetailvilles";
    public static final String BASE_URL_DETAIL_ETAB = BASE_URL+"/detailetab";
    public static final String BASE_URL_UPDATE_ANIM = BASE_URL+"/majanimateurs";
    public static final String BASE_URL_UPDATE_PHOTO = BASE_URL+"/pnanimateurs/updatephoto";
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

    public static void initialiserBase(Context mContext){
        int effacertablePersonnel = mContext.getContentResolver().delete(DaneContract.PersonnelEntry.CONTENT_URI,null,null);
        int effacertableEtablissement = mContext.getContentResolver().delete(DaneContract.EtablissementEntry.CONTENT_URI,null,null);
        int effacertableVille = mContext.getContentResolver().delete(DaneContract.VilleEntry.CONTENT_URI,null,null);
        int effacertableAnimateur = mContext.getContentResolver().delete(DaneContract.AnimateurEntry.CONTENT_URI,null,null);
        int effacertableDepartements = mContext.getContentResolver().delete(DaneContract.DepartementEntry.CONTENT_URI,null,null);
    }


    public static void getVillesDataFromJson(Context mContext, String resultat)
            throws JSONException {

        final String OWM_ETABS = "etabs";
        final String OWM_PERSONNEL = "personnel";
        final String OWM_ANIMATEUR = "animateur";
        try {
            JSONObject villeJson = new JSONObject(resultat);
            Iterator<String> depart = villeJson.keys();
            while( depart.hasNext() ) {
                String ledepart= (String)depart.next();
                String intitule = "";
                switch (ledepart){
                    case "77" : intitule = "seine et Marne";break;
                    case "93" : intitule = "Seine Saint Denis";break;
                    case "94" : intitule = "Val de Marne";break;
                }
                long insertedDepartement = addDepartement(mContext,ledepart,intitule);
                JSONArray villeArray = villeJson.getJSONArray(ledepart);
                Vector<ContentValues> cVVector = new Vector<ContentValues>(villeArray.length());
                for(int i = 0; i < villeArray.length(); i++) {
                    JSONObject laville = villeArray.getJSONObject(i);
                    long insertedVille = addVilleJson(mContext,laville,insertedDepartement);
                    JSONArray etabsArray = laville.getJSONArray(OWM_ETABS);
                    Vector<ContentValues> etabVector = new Vector<ContentValues>(etabsArray.length());
                    for(int j = 0; j < etabsArray.length(); j++) {
                        JSONObject etab = etabsArray.getJSONObject(j);
//                        Log.d(LOG_TAG, "etab : " +j+"-"+ etab.toString());
                        JSONArray animateurArray = etab.getJSONArray(OWM_ANIMATEUR);
                        long insertedanimateur = 0;
                        if (animateurArray.length()>0) {
                            JSONObject animateur = animateurArray.getJSONObject(0);
                            insertedanimateur = addAnimateurJson(mContext,animateur,insertedDepartement);
                        }
                        long insertedEtab = addEtablissementJson(mContext,etab,insertedVille,insertedanimateur);
                        JSONArray personnelArray = etab.getJSONArray(OWM_PERSONNEL);
                        for(int k = 0; k < personnelArray.length(); k++) {
                            JSONObject personnel = personnelArray.getJSONObject(k);
                            long insertedPersonnel = addPersonnelJson(mContext,personnel,insertedEtab);
                        }

                    }

                }
            }
        } catch (JSONException e) {
//            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public static long addEtablissementJson(Context mContext, JSONObject etab, Long insertedVille, Long insertedanimateur) {
        long etablissementId = 0;
        final String OWM_NOM= "nom";
        final String OWM_CP= "cp";
        final String OWM_ETABLISSEMENT_ID = "id";
        final String OWM_RNE = "rne";
        final String OWM_TEL = "tel";
        final String OWM_FAX = "fax";
        final String OWM_EMAIL = "email";
        final String OWM_ADRESSE = "adresse";
        final String OWM_TYPE = "type";
        // First, check if the location with this city name exists in the db
        Cursor etablissementCursor = null;
        try {
            etablissementCursor = mContext.getContentResolver().query(
                    DaneContract.EtablissementEntry.CONTENT_URI,
                    new String[]{DaneContract.EtablissementEntry._ID},
                    DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID + " = ?",
                    new String[]{etab.getString(OWM_ETABLISSEMENT_ID)},
                    null);
            if (etablissementCursor.moveToFirst()) {
                int etablissementIdIndex = etablissementCursor.getColumnIndex(DaneContract.EtablissementEntry._ID);
                etablissementId = etablissementCursor.getLong(etablissementIdIndex);
            } else {
                ContentValues etablissementValues = new ContentValues();
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_NOM, etab.getString(OWM_NOM));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_RNE, etab.getString(OWM_RNE));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_TEL, etab.getString(OWM_TEL));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_FAX, etab.getString(OWM_FAX));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_CP, etab.getString(OWM_CP));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ADRESSE, etab.getString(OWM_ADRESSE));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_VILLE_ID, insertedVille);
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ANIMATEUR_ID, (insertedanimateur==0)?null:insertedanimateur);
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_EMAIL, etab.getString(OWM_EMAIL));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_TYPE, etab.getString(OWM_TYPE));
                etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID, etab.getString(OWM_ETABLISSEMENT_ID));
                // Finally, insert location data into the database.
                Uri insertedUri = mContext.getContentResolver().insert(
                        DaneContract.EtablissementEntry.CONTENT_URI,
                        etablissementValues
                );

                // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
                etablissementId = ContentUris.parseId(insertedUri);
            }
            etablissementCursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return etablissementId;

    }

    public static long addPersonnelJson(Context mContext, JSONObject personnel, long EtablissementId) {
        long personnelId = 0;
        final String OWM_NOM= "nom";
        final String OWM_PERSONNEL_ID = "id";
        final String OWM_STATUT = "statut";
        // First, check if the location with this city name exists in the db
        Cursor personnelCursor = null;
        try {
            personnelCursor = mContext.getContentResolver().query(
                    DaneContract.PersonnelEntry.CONTENT_URI,
                    new String[]{DaneContract.PersonnelEntry._ID},
                    DaneContract.PersonnelEntry.COLUMN_PERSONNEL_ID + " = ?",
                    new String[]{personnel.getString(OWM_PERSONNEL_ID)},
                    null);
            if (personnelCursor.moveToFirst()) {
                int personnelIdIndex = personnelCursor.getColumnIndex(DaneContract.PersonnelEntry._ID);
                personnelId = personnelCursor.getLong(personnelIdIndex);
            } else {
                ContentValues personnelValues = new ContentValues();
                personnelValues.put(DaneContract.PersonnelEntry.COLUMN_NOM, personnel.getString(OWM_NOM));
                personnelValues.put(DaneContract.PersonnelEntry.COLUMN_STATUT, personnel.getString(OWM_STATUT));
                personnelValues.put(DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID, EtablissementId);
                personnelValues.put(DaneContract.PersonnelEntry.COLUMN_PERSONNEL_ID, personnel.getString(OWM_PERSONNEL_ID));
                // Finally, insert location data into the database.
                Uri insertedUri = mContext.getContentResolver().insert(
                        DaneContract.PersonnelEntry.CONTENT_URI,
                        personnelValues
                );

                // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
                personnelId = ContentUris.parseId(insertedUri);
            }
            personnelCursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return personnelId;
    }

    static long addDepartement(Context mContext, String nom, String intitule) {

        long departementId;
        Cursor departementCursor = mContext.getContentResolver().query(
                DaneContract.DepartementEntry.CONTENT_URI,
                new String[]{DaneContract.DepartementEntry._ID},
                DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM + " = ?",
                new String[]{nom},
                null);
        if (departementCursor.moveToFirst()) {
            int departementIdIndex = departementCursor.getColumnIndex(DaneContract.DepartementEntry._ID);
            departementId = departementCursor.getLong(departementIdIndex);
        } else {
            ContentValues departementValues = new ContentValues();
            departementValues.put(DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_NOM, nom);
            departementValues.put(DaneContract.DepartementEntry.COLUMN_DEPARTEMENT_INTITULE, intitule);
            Uri insertedUri = mContext.getContentResolver().insert(
                    DaneContract.DepartementEntry.CONTENT_URI,
                    departementValues
            );
            departementId = ContentUris.parseId(insertedUri);
        }
        departementCursor.close();
        return departementId;
    }

    static long addVilleJson(Context mContext, JSONObject laville, Long departement_id) {
        long villeId = 0;
        final String OWM_NOM= "nom";
        final String OWM_VILLE_ID = "id";
        Cursor villeCursor = null;
        try {
            villeCursor = mContext.getContentResolver().query(
                    DaneContract.VilleEntry.CONTENT_URI,
                    new String[]{DaneContract.VilleEntry._ID},
                    DaneContract.VilleEntry.COLUMN_VILLE_ID + " = ?",
                    new String[]{laville.getString(OWM_VILLE_ID)},
                    null);
            if (villeCursor.moveToFirst()) {
                int villeIdIndex = villeCursor.getColumnIndex(DaneContract.VilleEntry._ID);
                villeId = villeCursor.getLong(villeIdIndex);
            } else {
                ContentValues villeValues = new ContentValues();
                villeValues.put(DaneContract.VilleEntry.COLUMN_VILLE_NOM, laville.getString(OWM_NOM));
                villeValues.put(DaneContract.VilleEntry.COLUMN_DEPARTEMENT_ID, departement_id);
                villeValues.put(DaneContract.VilleEntry.COLUMN_VILLE_ID, laville.getString(OWM_VILLE_ID));
                Uri insertedUri = mContext.getContentResolver().insert(
                        DaneContract.VilleEntry.CONTENT_URI,
                        villeValues
                );
                villeId = ContentUris.parseId(insertedUri);
            }
            villeCursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return villeId;
    }

    static long addAnimateurJson(Context mContext, JSONObject animateur, Long Departement_id) {
        long animateurId = 0;
        final String OWM_NOM= "nom";
        final String OWM_ANIMATEUR_ID = "id";
        final String OWM_TEL = "tel";
        final String OWM_EMAIL = "email";
        final String OWN_PHOTO = "photo";
        Cursor animateurCursor = null;
        try {
            animateurCursor = mContext.getContentResolver().query(
                    DaneContract.AnimateurEntry.CONTENT_URI,
                    new String[]{DaneContract.AnimateurEntry._ID},
                    DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID + " = ?",
                    new String[]{animateur.getString(OWM_ANIMATEUR_ID)},
                    null);
            if (animateurCursor.moveToFirst()) {
                int animateurIdIndex = animateurCursor.getColumnIndex(DaneContract.AnimateurEntry._ID);
                animateurId = animateurCursor.getLong(animateurIdIndex);
            } else {
                ContentValues animateurValues = new ContentValues();
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_NOM, animateur.getString(OWM_NOM));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_TEL, animateur.getString(OWM_TEL));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_EMAIL, animateur.getString(OWM_EMAIL));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, animateur.getString(OWN_PHOTO));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID, animateur.getString(OWM_ANIMATEUR_ID));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID, Departement_id);
                Uri insertedUri = mContext.getContentResolver().insert(
                        DaneContract.AnimateurEntry.CONTENT_URI,
                        animateurValues
                );
                animateurId = ContentUris.parseId(insertedUri);
//                Log.d(LOG_TAG, "Animateur " + animateur.toString()+"--id--"+animateurId);
            }
            animateurCursor.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return animateurId;
    }


    public static void delPersonnel(Context mContext, String EtablissementId) {
        Cursor personnelCursor = mContext.getContentResolver().query(
                DaneContract.PersonnelEntry.CONTENT_URI,
                new String[]{DaneContract.PersonnelEntry._ID},
                DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID + " = ?",
                new String[]{EtablissementId},
                null);
        if (personnelCursor.moveToFirst()) {
            do {
                int personnelIdIndex = personnelCursor.getColumnIndex(DaneContract.PersonnelEntry._ID);
                Long personnelId = personnelCursor.getLong(personnelIdIndex);
//                Log.d(LOG_TAG, "majEtab personnelId : " + personnelId);
                String whereClause = "_id=?";
                String[] whereArgs = new String[] { String.valueOf(personnelId) };
                int deletedUri = mContext.getContentResolver().delete(
                        DaneContract.PersonnelEntry.CONTENT_URI,
                        whereClause,
                        whereArgs
                );
//                Log.d(LOG_TAG, "majEtab resultatdelete : " + deletedUri);
            } while (personnelCursor.moveToNext());
        }
    }
}
