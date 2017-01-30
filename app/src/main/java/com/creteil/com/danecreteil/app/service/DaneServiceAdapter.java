package com.creteil.com.danecreteil.app.service;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.creteil.com.danecreteil.app.R;
import com.creteil.com.danecreteil.app.data.DaneContract;

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
 * Created by Mohammed on 06/01/2017.
 */

public class DaneServiceAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = DaneServiceAdapter.class.getSimpleName();
    private String BASE_URL ="http://www.bouami.fr/gestionetabs/web/listedetailvilles";
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    public DaneServiceAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultat = null;
        Log.d(LOG_TAG, "onPerformSync : ");
        Log.d(LOG_TAG, "Synchronisation en cours");
        try {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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
                return;
            }
            resultat = buffer.toString();
            initialiserBase();
            getVillesDataFromJson(resultat);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
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
//        Integer nbreetabs = this.getNombreEtablissement();
//        if (nbreetabs > 0) {
//            Log.d(LOG_TAG, "updateDatabase : " + nbreetabs);
//            return;
//        }
//        if (!verifierDatabaseChargee()) {
//            this.ChargerDonneesBase();
//        }
        return;
    }
    private void ChargerDonneesBase(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultat = null;
        Log.d(LOG_TAG, "Synchronisation en cours");
        try {
            Uri builtUri = Uri.parse(BASE_URL).buildUpon().build();
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
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
                return;
            }
            resultat = buffer.toString();
            initialiserBase();
            getVillesDataFromJson(resultat);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
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

    public void initialiserBase(){
        Context mContext = getContext();
        int effacertablePersonnel = mContext.getContentResolver().delete(DaneContract.PersonnelEntry.CONTENT_URI,null,null);
        int effacertableEtablissement = mContext.getContentResolver().delete(DaneContract.EtablissementEntry.CONTENT_URI,null,null);
        int effacertableVille = mContext.getContentResolver().delete(DaneContract.VilleEntry.CONTENT_URI,null,null);
        int effacertableAnimateur = mContext.getContentResolver().delete(DaneContract.AnimateurEntry.CONTENT_URI,null,null);
    }

    public Integer getNombreEtablissement() {
        Cursor NombreetablissementCursor = getContext().getContentResolver().query(
                DaneContract.EtablissementEntry.CONTENT_URI,
                new String[]{DaneContract.EtablissementEntry._ID},
                null,
                null,
                null);
        return NombreetablissementCursor.getCount();
    }

    public boolean verifierDatabaseChargee() {
        Integer nbreetabs = this.getNombreEtablissement();
        if (nbreetabs > 0) {
            Log.d(LOG_TAG, "updateDatabase : " + nbreetabs);
            return true;
        }
        return false;
    }

    public void getVillesDataFromJson(String listevilles)
            throws JSONException {
//        final String OWM_DEPART = departementSetting;
        final String OWM_ID= "id";
        final String OWM_NOM= "nom";
//        final String OWM_DISTRICT= "district";
        final String OWM_CP= "cp";
        final String OWM_VILLE_ID = "id";
        final String OWM_ETABLISSEMENT_ID = "id";
        final String OWM_PERSONNEL_ID = "id";
        final String OWM_ANIMATEUR_ID = "id";
        final String OWM_ETABS = "etabs";
        final String OWM_RNE = "rne";
        final String OWM_TEL = "tel";
        final String OWM_FAX = "fax";
        final String OWM_EMAIL = "email";
        final String OWM_ADRESSE = "adresse";
        final String OWM_TYPE = "type";
        final String OWM_PERSONNEL = "personnel";
        final String OWM_ANIMATEUR = "animateur";
        final String OWM_STATUT = "statut";
        try {
            JSONObject villeJson = new JSONObject(listevilles);
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
                    long insertedVille = addVille(laville.getString(OWM_NOM),insertedDepartement,laville.getString(OWM_VILLE_ID));
                    Log.d(LOG_TAG, "Lors de cette opération. " + laville.getString(OWM_NOM) + " = id de la ville insérée");
                    JSONArray etabsArray = laville.getJSONArray(OWM_ETABS);
                    Vector<ContentValues> etabVector = new Vector<ContentValues>(etabsArray.length());
                    for(int j = 0; j < etabsArray.length(); j++) {
                        JSONObject etab = etabsArray.getJSONObject(j);
                        JSONArray animateurArray = etab.getJSONArray(OWM_ANIMATEUR);
                        long insertedanimateur = 0;
                        if (animateurArray.length()>0) {
                            JSONObject animateur = animateurArray.getJSONObject(0);
                            insertedanimateur = addAnimateur(animateur.getString(OWM_NOM),animateur.getString(OWM_TEL),animateur.getString(OWM_EMAIL),
                                    animateur.getString(OWM_ANIMATEUR_ID),insertedDepartement);
                        }
                        long insertedEtab = addEtablissement(insertedVille,insertedanimateur,etab.getString(OWM_ETABLISSEMENT_ID),etab.getString(OWM_NOM),etab.getString(OWM_RNE),
                                etab.getString(OWM_TEL),etab.getString(OWM_FAX),etab.getString(OWM_EMAIL),
                                etab.getString(OWM_ADRESSE),etab.getString(OWM_CP),etab.getString(OWM_TYPE));
                        Log.d(LOG_TAG, "Lors de cette opération, l'établissement " + etab.getString(OWM_NOM) + "----"+insertedanimateur + " a été inséré");
                        JSONArray personnelArray = etab.getJSONArray(OWM_PERSONNEL);
                        for(int k = 0; k < personnelArray.length(); k++) {
                            JSONObject personnel = personnelArray.getJSONObject(k);
                            long insertedPersonnel = addPersonnel(insertedEtab,personnel.getString(OWM_PERSONNEL_ID),
                                    personnel.getString(OWM_NOM),personnel.getString(OWM_STATUT));
                        }

                    }

                }
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    long addDepartement(String nom,String intitule) {

        long departementId;
        Cursor departementCursor = getContext().getContentResolver().query(
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
            Uri insertedUri = getContext().getContentResolver().insert(
                    DaneContract.DepartementEntry.CONTENT_URI,
                    departementValues
            );
            departementId = ContentUris.parseId(insertedUri);
        }
        departementCursor.close();
        return departementId;
    }

    long addEtablissement(long VilleId,long AnimateurId,String etab_id,String nom,String rne,String tel,
                          String fax, String email,String adresse, String cp, String type) {
        long etablissementId;
        // First, check if the location with this city name exists in the db
        Cursor etablissementCursor = getContext().getContentResolver().query(
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
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ANIMATEUR_ID, (AnimateurId==0)?null:AnimateurId);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_EMAIL, email);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_TYPE, type);
            etablissementValues.put(DaneContract.EtablissementEntry.COLUMN_ETABLISSEMENT_ID, etab_id);
            // Finally, insert location data into the database.
            Uri insertedUri = getContext().getContentResolver().insert(
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
        Cursor personnelCursor = getContext().getContentResolver().query(
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
            Uri insertedUri = getContext().getContentResolver().insert(
                    DaneContract.PersonnelEntry.CONTENT_URI,
                    personnelValues
            );

            // The resulting URI contains the ID for the row.  Extract the locationId from the Uri.
            personnelId = ContentUris.parseId(insertedUri);
        }
        personnelCursor.close();
        return personnelId;
    }

    long addVille(String nom,Long departement_id,String Ville_base_Id) {

        long villeId;
        Cursor villeCursor = getContext().getContentResolver().query(
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
            villeValues.put(DaneContract.VilleEntry.COLUMN_VILLE_NOM, nom);
            villeValues.put(DaneContract.VilleEntry.COLUMN_DEPARTEMENT_ID, departement_id);
            villeValues.put(DaneContract.VilleEntry.COLUMN_VILLE_ID, Ville_base_Id);
            Uri insertedUri = getContext().getContentResolver().insert(
                    DaneContract.VilleEntry.CONTENT_URI,
                    villeValues
            );
            villeId = ContentUris.parseId(insertedUri);
        }
        villeCursor.close();
        return villeId;
    }

    long addAnimateur(String nom,String tel,String email,String Animateur_base_Id,Long Departement_id) {

        long animateurId;
        Cursor animateurCursor = getContext().getContentResolver().query(
                DaneContract.AnimateurEntry.CONTENT_URI,
                new String[]{DaneContract.AnimateurEntry._ID},
                DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID + " = ?",
                new String[]{Animateur_base_Id},
                null);
        if (animateurCursor.moveToFirst()) {
            int animateurIdIndex = animateurCursor.getColumnIndex(DaneContract.AnimateurEntry._ID);
            animateurId = animateurCursor.getLong(animateurIdIndex);
        } else {
            ContentValues animateurValues = new ContentValues();
            animateurValues.put(DaneContract.AnimateurEntry.COLUMN_NOM, nom);
            animateurValues.put(DaneContract.AnimateurEntry.COLUMN_TEL, tel);
            animateurValues.put(DaneContract.AnimateurEntry.COLUMN_EMAIL, email);
            animateurValues.put(DaneContract.AnimateurEntry.COLUMN_ANIMATEUR_ID, Animateur_base_Id);
            animateurValues.put(DaneContract.AnimateurEntry.COLUMN_DEPARTEMENT_ID, Departement_id);
            Uri insertedUri = getContext().getContentResolver().insert(
                    DaneContract.AnimateurEntry.CONTENT_URI,
                    animateurValues
            );
            animateurId = ContentUris.parseId(insertedUri);
        }
        animateurCursor.close();
        return animateurId;
    }

    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        DaneServiceAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
