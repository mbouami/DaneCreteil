package com.creteil.com.danecreteil.app;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.creteil.com.danecreteil.app.data.DaneContract;
import com.creteil.com.danecreteil.app.data.DaneContract.VilleEntry;
import com.creteil.com.danecreteil.app.data.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Mohammed on 28/11/2016.
 */

public class FetchVillesTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchVillesTask.class.getSimpleName();

    private final Context mContext;
    private boolean DEBUG = true;
    ProgressDialog pDialog;

    public FetchVillesTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage("Chargement des données en cours. Merci de patienter...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        pDialog.dismiss();
    }

    @Override
    protected Void doInBackground(String... params) {

        // If there's no zip code, there's nothing to look up.  Verify size of params.
//        if (params.length == 0) {
//            return null;
//        }
//        String departementQuery = params[0];
//        final String VILLES_BASE_URL ="http://www.bouami.fr/gestionetabs/web/listevilles/";
        final String VILLES_BASE_URL ="http://www.bouami.fr/gestionetabs/web/listedetailvilles";
        JSONParser parser = new JSONParser(VILLES_BASE_URL,"GET",mContext);
        try {
//            parser.getVillesDataFromJson(mContext,departementQuery);
            parser.getVillesDataFromJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
