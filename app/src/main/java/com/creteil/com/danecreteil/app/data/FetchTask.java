package com.creteil.com.danecreteil.app.data;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONException;

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
//            default:
//                return super.onOptionsItemSelected(item);
        }
        return null;
    }
}
