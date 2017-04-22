package com.creteil.com.danecreteil.app.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Mohammed on 27/03/2017.
 */

public class FetchTask extends AsyncTask<String, Void, Void> {
    private final String LOG_TAG = FetchTask.class.getSimpleName();
    private final Context mContext;
    private final String mUrl;
    private boolean DEBUG = true;
    ProgressDialog pDialog;

    public FetchTask(Context context, String url) {
        mContext = context;
        mUrl = url;
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
        JSONParser parser = new JSONParser(mUrl,"GET",mContext);
        switch (params[0]) {
            case "update":
                parser.initialiserBase();
                try {
                    parser.getVillesDataFromJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "Initialiser":
                parser.verifierDatabase();
                break;
            case "maj_anim":
                try {
                    parser.majAnim(params[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "maj_etab":
                try {
                    parser.majEtab(params[1]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case "maj_photo":
                    parser.majPhoto(params[1], params[2]);
//                PostData(DaneContract.BASE_URL_UPDATE_PHOTO, params[1], params[2]);
//                PostDataRequette(DaneContract.BASE_URL_UPDATE_PHOTO, params[1], params[2]);
                break;
//            default:
//                return super.onOptionsItemSelected(item);
        }
        return null;
    }

    private void PostDataRequette(String adresse, String id, String photo) {
        System.setProperty("http.keepAlive", "false");
        OutputStreamWriter writer = null;
        BufferedReader reader = null;
        HttpURLConnection connexion = null;
        try {
            // Encodage des paramètres de la requête
            String donnees = URLEncoder.encode("id", "UTF-8")+ "="+URLEncoder.encode(id, "UTF-8");
            donnees += "&"+URLEncoder.encode("photo", "UTF-8")+ "=" + URLEncoder.encode(photo, "UTF-8");

            // On a envoyé les données à une adresse distante
            URL url = new URL(adresse);
            connexion = (HttpURLConnection) url.openConnection();
            connexion.setDoOutput(true);
            connexion.setChunkedStreamingMode(0);
            connexion.setRequestMethod("POST");

            // On envoie la requête ici
            writer = new OutputStreamWriter(connexion.getOutputStream());

            // On insère les données dans notre flux
            writer.write(donnees);

            // Et on s'assure que le flux est vidé
            writer.flush();

            // On lit la réponse ici
            reader = new BufferedReader(new InputStreamReader(connexion.getInputStream()));
            String ligne;

            // Tant que « ligne » n'est pas null, c'est que le flux n'a pas terminé d'envoyer des informations
            while ((ligne = reader.readLine()) != null) {
//                System.out.println(ligne);
                Log.w(LOG_TAG,"ligne : " + ligne);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try{writer.close();}catch(Exception e){}
            try{reader.close();}catch(Exception e){}
            try{connexion.disconnect();}catch(Exception e){}
        }


    }
    private void PostData(String adresse, String id, String photo) {
        Log.w(LOG_TAG,"photo : " + photo);
        URL url = null;
        try {
//            url = new URL(adresse+"?id="+id+"&photo="+photo);
            url = new URL(adresse);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            urlConnection.setDoOutput(true);
            urlConnection.setChunkedStreamingMode(0);
//            urlConnection.setRequestProperty("id",id);
//            urlConnection.setRequestProperty("photo",photo);
            urlConnection.setRequestMethod("POST");
            OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
            writeStream(out);

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            readStream(in);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
    }

    private void writeStream(OutputStream out){
        String output = "Hello world";

        try {
            out.write(output.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.w(LOG_TAG,"writeStream : " + output);
    }

    private void readStream(InputStream in) {
        String resultat = null;
        BufferedReader reader;
        StringBuffer buffer;
        buffer = new StringBuffer();
        String line;
        reader = new BufferedReader(new InputStreamReader(in));
        try {
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultat = buffer.toString();
        Log.w(LOG_TAG,"resultat : " + resultat);
    }
}
