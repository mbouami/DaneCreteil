package com.creteil.com.danecreteil.app.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.creteil.com.danecreteil.app.FetchVillesTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
            this.parse(new URL(builtUri.toString()), method);
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
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
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

    long addVille(String nom,String departement,String Ville_base_Id) {

        long villeId;
        Cursor villeCursor = mContext.getContentResolver().query(
                DaneContract.VilleEntry.CONTENT_URI,
                new String[]{DaneContract.VilleEntry._ID},
                DaneContract.VilleEntry.COLUMN_VILLE_ID + " = ?",
                new String[]{Ville_base_Id},
                null);
        if (villeCursor.moveToFirst()) {
            int villeIdIndex = villeCursor.getColumnIndex(DaneContract.VilleEntry._ID);
            villeId = villeCursor.getLong(villeIdIndex);
        } else {
            ContentValues villeValues = new ContentValues();
            villeValues.put(DaneContract.VilleEntry.COLUMN_NOM, nom);
            villeValues.put(DaneContract.VilleEntry.COLUMN_DEPARTEMENT, departement);
            villeValues.put(DaneContract.VilleEntry.COLUMN_VILLE_ID, Ville_base_Id);
            Uri insertedUri = mContext.getContentResolver().insert(
                    DaneContract.VilleEntry.CONTENT_URI,
                    villeValues
            );
            villeId = ContentUris.parseId(insertedUri);
        }
        villeCursor.close();
        return villeId;
    }
    long addEtablissement(long VilleId,String etab_id,String nom,String rne,String tel,
                          String fax, String email,String adresse, String cp, String type) {
        long etablissementId;
        // First, check if the location with this city name exists in the db
        Cursor etablissementCursor = mContext.getContentResolver().query(
                DaneContract.EtablissementEntry.CONTENT_URI,
                new String[]{DaneContract.EtablissementEntry._ID},
                DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID + " = ?",
                new String[]{etab_id},
                null);
        if (etablissementCursor.moveToFirst()) {
            int etablissementIdIndex = etablissementCursor.getColumnIndex(DaneContract.EtablissementEntry._ID);
            etablissementId = etablissementCursor.getLong(etablissementIdIndex);
        } else {
            ContentValues etablissementValues = new ContentValues();
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_NOM, nom);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_RNE, rne);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_TEL, tel);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_FAX, fax);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_CP, cp);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ADRESSE, adresse);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_VILLE_ID, VilleId);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_EMAIL, email);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_TYPE, type);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID, etab_id);
            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    DaneContract.EtablissementEntry.CONTENT_URI,
                    etablissementValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            etablissementId = ContentUris.parseId(insertedUri);
        }
        etablissementCursor.close();
        return etablissementId;
    }

    long addPersonnel(long EtablissementId,String personnel_id,String nom,String statut) {
        long personnelId;
        // First, check if the location with this city name exists in the db
        Cursor personnelCursor = mContext.getContentResolver().query(
                DaneContract.PersonnelEntry.CONTENT_URI,
                new String[]{DaneContract.PersonnelEntry._ID},
                DaneContract.PersonnelEntry.COLUMN_PERSONNEL_ID + " = ?",
                new String[]{personnel_id},
                null);
        if (personnelCursor.moveToFirst()) {
            int personnelIdIndex = personnelCursor.getColumnIndex(DaneContract.PersonnelEntry._ID);
            personnelId = personnelCursor.getLong(personnelIdIndex);
        } else {
            ContentValues personnelValues = new ContentValues();
            personnelValues.put(DaneContract.PersonnelEntry.COLUMN_NOM, nom);
            personnelValues.put(DaneContract.PersonnelEntry.COLUMN_STATUT, statut);
            personnelValues.put(DaneContract.PersonnelEntry.COLUMN_ETABLISSEMENT_ID, EtablissementId);
            personnelValues.put(DaneContract.PersonnelEntry.COLUMN_PERSONNEL_ID, personnel_id);
            // Finally, insert location data into the database.
            Uri insertedUri = mContext.getContentResolver().insert(
                    DaneContract.PersonnelEntry.CONTENT_URI,
                    personnelValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            personnelId = ContentUris.parseId(insertedUri);
        }
        personnelCursor.close();
        return personnelId;
    }

    public void getVillesDataFromJson()
            throws JSONException {

//        final String OWM_DEPART = departementSetting;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
//        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        final String OWM_VILLE_ID = "id";
        final String OWM_ETABLISSEMENT_ID = "id";
        final String OWM_PERSONNEL_ID = "id";
        final String OWM_ETABS = "etabs";
        final String OWM_RNE = "rne";
        final String OWM_TEL = "tel";
        final String OWM_FAX = "fax";
        final String OWM_EMAIL = "email";
        final String OWM_ADRESSE = "adresse";
        final String OWM_TYPE = "type";
        final String OWM_PERSONNEL = "personnel";
        final String OWM_STATUT = "statut";
        try {
            JSONObject villeJson = new JSONObject(resultat);
            Iterator<String> depart = villeJson.keys();
            while( depart.hasNext() ) {
                String ledepart= (String)depart.next();
                JSONArray villeArray = villeJson.getJSONArray(ledepart);
                Vector<ContentValues> cVVector = new Vector<ContentValues>(villeArray.length());
                for(int i = 0; i < villeArray.length(); i++) {
                    JSONObject laville = villeArray.getJSONObject(i);
//                    ContentValues villesValues = new ContentValues();
//                    villesValues.put(DaneContract.VilleEntry.COLUMN_NOM, laville.getString(OWM_NOM));
//                    villesValues.put(DaneContract.VilleEntry.COLUMN_DEPARTEMENT, ledepart);
//                    villesValues.put(DaneContract.VilleEntry.COLUMN_VILLE_ID, laville.getLong(OWM_ID));
//                    cVVector.add(villesValues);
                    long insertedVille = addVille(laville.getString(OWM_NOM),ledepart,laville.getString(OWM_VILLE_ID));
//                    JSONObject etabsJson = laville.getJSONObject(OWM_ETABS);
                    Log.d(LOG_TAG, "Lors de cette opération. " + laville.getString(OWM_NOM) + " = id de la ville insérée");
                    JSONArray etabsArray = laville.getJSONArray(OWM_ETABS);
                    Vector<ContentValues> etabVector = new Vector<ContentValues>(etabsArray.length());
                    for(int j = 0; j < etabsArray.length(); j++) {
                        JSONObject etab = etabsArray.getJSONObject(j);
//                        ContentValues etabValues = new ContentValues();
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_NOM,etab.getString(OWM_NOM));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_RNE,etab.getString(OWM_RNE));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_TYPE,etab.getString(OWM_TYPE));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_TEL,etab.getString(OWM_TEL));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_FAX,etab.getString(OWM_FAX));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_ADRESSE,etab.getString(OWM_ADRESSE));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_CP,etab.getString(OWM_CP));
//                        etabValues.put(DaneContract.EtablissementEntry.COLUMN_EMAIL,etab.getString(OWM_EMAIL));
//                        etabVector.add(etabValues);
                        long insertedEtab = addEtablissement(insertedVille,etab.getString(OWM_ETABLISSEMENT_ID),etab.getString(OWM_NOM),etab.getString(OWM_RNE),
                                etab.getString(OWM_TEL),etab.getString(OWM_FAX),etab.getString(OWM_EMAIL),
                                etab.getString(OWM_ADRESSE),etab.getString(OWM_CP),etab.getString(OWM_TYPE));
                        Log.d(LOG_TAG, "Lors de cette opération. " + etab.getString(OWM_NOM) + " = id de letablissement inséré");
                        JSONArray personnelArray = etab.getJSONArray(OWM_PERSONNEL);
                        for(int k = 0; k < personnelArray.length(); k++) {
                            JSONObject personnel = personnelArray.getJSONObject(k);
                            long insertedPersonnel = addPersonnel(insertedEtab,personnel.getString(OWM_PERSONNEL_ID),
                                    personnel.getString(OWM_NOM),personnel.getString(OWM_STATUT));
                            Log.d(LOG_TAG, "Lors de cette opération. " + personnel.getString(OWM_NOM) + " = id du personnel inséré");

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
