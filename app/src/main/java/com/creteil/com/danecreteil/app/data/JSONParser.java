package com.creteil.com.danecreteil.app.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by Mohammed on 27/11/2016.
 */

public class JSONParser {
    private final String LOG_TAG = JSONParser.class.getSimpleName();
    private final Context mContext;
    private String resultat = null;

    public JSONParser(String url, String method,Context context) {
        mContext = context;
        try {
            Uri builtUri = Uri.parse(url).buildUpon().build();
            parse(new URL(builtUri.toString()), method);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void parse(URL url, String method) throws IOException {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(method);
            urlConnection.setDoInput(true);
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
//            resultat = readStream(inputStream,100000);
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                resultat = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                resultat = null;
            }
            resultat = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
        } finally {
            urlConnection.disconnect();
            reader.close();
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    private String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        // Read InputStream using the UTF-8 charset.
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        // Create temporary buffer to hold Stream data with specified max length.
        char[] buffer = new char[maxLength];
        // Populate temporary buffer with Stream data.
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
//            publishProgress(DownloadCallback.Progress.PROCESS_INPUT_STREAM_IN_PROGRESS, pct);
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            // The stream was not empty.
            // Create String that is actual length of response body if actual length was less than
            // max length.
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }

    long addDepartement(String nom,String intitule) {

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

    long addVilleJson(JSONObject laville,Long departement_id) {
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

    long addAnimateurJson(JSONObject animateur,Long Departement_id) {
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

    public Integer getNombreEtablissement() {
        Cursor NombreetablissementCursor = mContext.getContentResolver().query(
                DaneContract.EtablissementEntry.CONTENT_URI,
                new String[]{DaneContract.EtablissementEntry._ID},
                null,
                null,
                null);
        return NombreetablissementCursor.getCount();
    }

    public void verifierDatabase() {
        Integer nbreetabs = this.getNombreEtablissement();
        if (nbreetabs > 0) {
            Log.d(LOG_TAG, "updateDatabase : " + nbreetabs);
        } else {
            this.initialiserBase();
            try {
                this.getVillesDataFromJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void majEtab(String idEtab) throws JSONException {
        long etablissementId = 0;
        // First, check if the location with this city name exists in the db
        Cursor etablissementCursor = mContext.getContentResolver().query(
                DaneContract.EtablissementEntry.CONTENT_URI,
                new String[]{DaneContract.EtablissementEntry._ID},
                DaneContract.EtablissementEntry._ID + " = ?",
                new String[]{idEtab},
                null);
        if (etablissementCursor.moveToFirst()) {
            int etablissementIdIndex = etablissementCursor.getColumnIndex(DaneContract.EtablissementEntry._ID);
            etablissementId = etablissementCursor.getLong(etablissementIdIndex);
        }
//        Log.d(LOG_TAG, "majEtab idEtab : " + etablissementId);
        delPersonnel(idEtab);
        try {
            JSONObject detailetabJson = new JSONObject(resultat);
//            Log.d(LOG_TAG, "majEtab idetab : " + detailetabJson.get("id").toString());
            JSONArray listePersonnel = detailetabJson.getJSONArray("personnel");
            for(int j = 0; j < listePersonnel.length(); j++) {
                JSONObject lepersonnel = listePersonnel.getJSONObject(j);
//                Log.d(LOG_TAG, "majEtab personnel : " + lepersonnel.toString());
                long insertedPersonnel = addPersonnelJson(lepersonnel,etablissementId);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    public void majPhoto(String id, String photo) {
            Log.e(LOG_TAG, "id : "+id+"- photo"+photo);
    }

    public void majAnim(String idAnim) throws JSONException {

        final String OWM_NOM= "nom";
        final String OWM_TEL = "tel";
        final String OWM_EMAIL = "email";
        final String OWN_PHOTO = "photo";
        try {
            JSONObject detailanimJson = new JSONObject(resultat);
            if (!idAnim.equals("")) {
                ContentValues animateurValues = new ContentValues();
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_NOM, detailanimJson.getString(OWM_NOM));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_TEL, detailanimJson.getString(OWM_TEL));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_EMAIL, detailanimJson.getString(OWM_EMAIL));
//                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, detailanimJson.getString(OWN_PHOTO));
                animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, Base64.decode(detailanimJson.getString(OWN_PHOTO),Base64.DEFAULT));
                String whereClause = "_id=?";
                String[] whereArgs = new String[] { String.valueOf(idAnim) };
                try {
                    mContext.getContentResolver().update(DaneContract.AnimateurEntry.CONTENT_URI,
                            animateurValues,whereClause,whereArgs);
                } catch (NullPointerException e) {
                    Log.w(LOG_TAG,e.getMessage());
                }
            } else {
                JSONArray animsArray = detailanimJson.getJSONArray("animateurs");
                for(int k = 0; k < animsArray.length(); k++) {
                    JSONObject animateur = animsArray.getJSONObject(k);
                    Cursor animateursCursor = mContext.getContentResolver().query(
                            DaneContract.AnimateurEntry.CONTENT_URI,
                            new String[]{DaneContract.AnimateurEntry._ID},
                            DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID + " = ?",
                            new String[]{animateur.getString("id")},
                            null);
                    if (animateursCursor.moveToFirst()) {
                        ContentValues animateurValues = new ContentValues();
                        animateurValues.put(DaneContract.AnimateurEntry.COLUMN_NOM, animateur.getString(OWM_NOM));
                        animateurValues.put(DaneContract.AnimateurEntry.COLUMN_TEL, animateur.getString(OWM_TEL));
                        animateurValues.put(DaneContract.AnimateurEntry.COLUMN_EMAIL, animateur.getString(OWM_EMAIL));
//                        animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, animateur.getString(OWN_PHOTO));
                        animateurValues.put(DaneContract.AnimateurEntry.COLUMN_PHOTO, Base64.decode(animateur.getString(OWN_PHOTO),Base64.DEFAULT));
                        String whereClause = "_id=?";
                        String[] whereArgs = new String[] { animateursCursor.getString(animateursCursor.getColumnIndex(DaneContract.AnimateurEntry._ID)) };
                        try {
                            mContext.getContentResolver().update(DaneContract.AnimateurEntry.CONTENT_URI,
                                    animateurValues,whereClause,whereArgs);
                        } catch (NullPointerException e) {
                            Log.w(LOG_TAG,e.getMessage());
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }


    long addEtablissementJson(JSONObject etab,Long insertedVille,Long insertedanimateur) {
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

    void delPersonnel(String EtablissementId) {
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

    long addPersonnelJson(JSONObject personnel,long EtablissementId) {
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

    public void initialiserBase(){
        int effacertablePersonnel = mContext.getContentResolver().delete(DaneContract.PersonnelEntry.CONTENT_URI,null,null);
        int effacertableEtablissement = mContext.getContentResolver().delete(DaneContract.EtablissementEntry.CONTENT_URI,null,null);
        int effacertableVille = mContext.getContentResolver().delete(DaneContract.VilleEntry.CONTENT_URI,null,null);
        int effacertableAnimateur = mContext.getContentResolver().delete(DaneContract.AnimateurEntry.CONTENT_URI,null,null);
        int effacertableDepartements = mContext.getContentResolver().delete(DaneContract.DepartementEntry.CONTENT_URI,null,null);
    }

    public void getVillesDataFromJson()
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
                long insertedDepartement = addDepartement(ledepart,intitule);
                JSONArray villeArray = villeJson.getJSONArray(ledepart);
                Vector<ContentValues> cVVector = new Vector<ContentValues>(villeArray.length());
                for(int i = 0; i < villeArray.length(); i++) {
                    JSONObject laville = villeArray.getJSONObject(i);
                    long insertedVille = addVilleJson(laville,insertedDepartement);
//                    long insertedVille = addVille(laville.getString(OWM_NOM),insertedDepartement,laville.getString(OWM_VILLE_ID));
//                    JSONObject etabsJson = laville.getJSONObject(OWM_ETABS);
//                    Log.d(LOG_TAG, "Lors de cette opération. " + laville.getString(OWM_NOM) + " = id de la ville insérée");
                    JSONArray etabsArray = laville.getJSONArray(OWM_ETABS);
                    Vector<ContentValues> etabVector = new Vector<ContentValues>(etabsArray.length());
                    for(int j = 0; j < etabsArray.length(); j++) {
                        JSONObject etab = etabsArray.getJSONObject(j);
//                        Log.d(LOG_TAG, "etab : " +j+"-"+ etab.toString());
                        JSONArray animateurArray = etab.getJSONArray(OWM_ANIMATEUR);
                        long insertedanimateur = 0;
                        if (animateurArray.length()>0) {
                            JSONObject animateur = animateurArray.getJSONObject(0);
                            insertedanimateur = addAnimateurJson(animateur,insertedDepartement);
                        }
//Log.d(LOG_TAG, "ID : " + etab.getString(OWM_ETABLISSEMENT_ID) + " = ETAB : "+etab.getString(OWM_NOM));
                        long insertedEtab = addEtablissementJson(etab,insertedVille,insertedanimateur);
//                        Log.d(LOG_TAG, "Lors de cette opération, l'établissement " + etab.getString(OWM_NOM) + "--du--"+ledepart + " a été inséré ");
                        JSONArray personnelArray = etab.getJSONArray(OWM_PERSONNEL);
                        for(int k = 0; k < personnelArray.length(); k++) {
                            JSONObject personnel = personnelArray.getJSONObject(k);
                            long insertedPersonnel = addPersonnelJson(personnel,insertedEtab);
//                            Log.d(LOG_TAG, "Lors de cette opération. " + personnel.getString(OWM_NOM) + " = id du personnel inséré");

                        }

                    }

                }
//                int inserted = 0;
//                // add to database
//                if ( cVVector.size() > 0 ) {
//                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
//                    cVVector.toArray(cvArray);
//                    inserted = mContext.getContentResolver().bulkInsert(DaneContract.VilleEntry.CONTENT_URI, cvArray);
//                }

//                Log.d(LOG_TAG, "Lors de cette opération. " + inserted + " villes ont été insérées pour le "+ledepart);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
